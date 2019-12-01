package com.koi.monkey.evaluator;

import com.koi.monkey.ast.*;
import com.koi.monkey.object.*;

import java.util.List;

/**
 * @author whuang
 * @date 2019/10/28
 */
public class Evaluator {

    public static final Bool TRUE = new Bool(true);
    public static final Bool FALSE = new Bool(false);
    public static final Nil NULL = new Nil();

    public static Obj eval(Node node,Environment env) {
        if (node instanceof Program) {
            return evalProgram(((Program) node),env);
        }

        if (node instanceof BlockStatement) {
            return evalBlockStatement(((BlockStatement) node),env);
        }
        if (node instanceof ReturnStatement) {
            Obj val = eval(((ReturnStatement) node).returnValue,env);
            if (isErr(val)) {
                return val;
            }
            return new ReturnValue(val);
        }
        if (node instanceof ExpressionStatement) {
            return eval(((ExpressionStatement) node).expression,env);
        }

        if (node instanceof LetStatement) {
            Obj val = eval(((LetStatement) node).value, env);
            if (isErr(val)) {
                return val;
            }
            env.set(((LetStatement) node).name.value,val);
        }
        if (node instanceof IntegerLiteral) {
            return new Int(((IntegerLiteral) node).value);
        }

        if (node instanceof BooleanLiteral) {
            return nativeBoolToBooleanObj(((BooleanLiteral) node).value);
        }

        if (node instanceof Identifier) {
            return evalIdentifier((Identifier) node,env);
        }

        if (node instanceof FunctionLiteral) {
            return new Function(((FunctionLiteral) node).parameters,((FunctionLiteral) node).body,env);
        }

        if (node instanceof PrefixExpression) {
            Obj right = eval(((PrefixExpression) node).right,env);
            if (isErr(right)) {
                return right;
            }
            return evalPrefixExpression(((PrefixExpression) node).operator, right);
        }

        if (node instanceof InfixExpression) {
            Obj left = eval(((InfixExpression) node).left,env);
            if (isErr(left)) {
                return left;
            }
            Obj right = eval(((InfixExpression) node).right,env);
            if (isErr(right)) {
                return right;
            }
            return evalInfixExpression(((InfixExpression) node).operator, left, right);
        }

        if (node instanceof IfExpression) {
            return evalIfExpression((IfExpression) node,env);
        }

        if (node instanceof CallExpression) {
            Obj function = eval(((CallExpression) node).function, env);
            if (isErr(function)) {
                return function;
            }
            Obj[] args = evalExpressions(((CallExpression) node).arguments,env);
            if (args.length == 1 && isErr(args[0])) {
                return args[0];
            }
            return applyFunction(function,args);
        }

        return null;
    }

    private static Obj applyFunction(Obj fn, Obj[] args) {
        if (!(fn instanceof Function)) {
            return newErr("not a function: %s",fn.type());
        }
        Environment extendedEnv = extendFunctionEnv((Function)fn,args);
        Obj evaluated = eval(((Function) fn).body, extendedEnv);
        return unwrapReturnValue(evaluated);
    }

    private static Obj unwrapReturnValue(Obj obj) {
        if(obj instanceof  ReturnValue){
            return ((ReturnValue) obj).value;
        }
        return obj;
    }

    private static Environment extendFunctionEnv(Function fn, Obj[] args) {
        Environment env = Environment.newEnclosedEnvironment(fn.env);
        for (int i = 0; i < fn.parameters.size(); i++) {
            env.set(fn.parameters.get(i).value,args[i]);
        }
        return env;
    }

    private static Obj[] evalExpressions(List<Expression> exps, Environment env) {
        Obj[] result = new Obj[exps.size()];
        for (int i = 0; i < exps.size(); i++) {
            Obj evaluated = eval(exps.get(i),env);
            if (isErr(evaluated)) {
                return new Obj[]{evaluated};
            }
            result[i] = evaluated;
        }
        return result;
    }

    private static Obj evalIdentifier(Identifier node, Environment env) {
        Obj val = env.get(node.value);
        if(val == null) {
            return newErr("identifier not found: " + node.value);
        }
        return val;
    }


    private static Obj evalIfExpression(IfExpression ie, Environment env) {
        Obj condition = eval(ie.condition,env);
        if (isErr(condition)) {
            return condition;
        }
        if(isTruthy(condition)){
            return eval(ie.consequence,env);
        } else if(ie.alternative != null) {
            return eval(ie.alternative,env);
        } else {
            return NULL;
        }
    }

    private static boolean isTruthy(Obj obj) {
        if(obj == NULL) {
            return false;
        }
        if (obj == TRUE) {
            return true;
        }

        if (obj == FALSE) {
            return false;
        }


        return true;
    }

    private static Obj evalInfixExpression(String operator, Obj left, Obj right) {
        if (left.type().equals(ObjType.INTEGER_OBJ) && right.type().equals(ObjType.INTEGER_OBJ)) {
            return evalIntegerInfixExpression(operator, left, right);
        }
        if (operator.equals("==")) {
            return nativeBoolToBooleanObj(left == right);
        }

        if (operator.equals("!=")) {
            return nativeBoolToBooleanObj(left != right);
        }

        if(!left.type().equals(right.type())) {
            return newErr("type mismatch: %s %s %s",left.type(),operator,right.type());
        }
        return newErr("unknown operator: %s %s %s",left.type(),operator,right.type());
    }


    private static Obj evalIntegerInfixExpression(String operator, Obj left, Obj right) {
        int leftVal = ((Int) left).value;
        int rightVal = ((Int) right).value;
        switch (operator) {
            case "+":
                return new Int(leftVal + rightVal);
            case "-":
                return new Int(leftVal - rightVal);
            case "*":
                return new Int(leftVal * rightVal);
            case "/":
                return new Int(leftVal / rightVal);
            case "<":
                return nativeBoolToBooleanObj(leftVal < rightVal);
            case ">":
                return nativeBoolToBooleanObj(leftVal > rightVal);
            case "==":
                return nativeBoolToBooleanObj(leftVal == rightVal);
            case "!=":
                return nativeBoolToBooleanObj(leftVal != rightVal);
            default:
                return newErr("unknown operator: %s %s %s",left.type(),operator,right.type());
        }
    }

    private static Obj evalPrefixExpression(String operator, Obj right) {
        switch (operator) {
            case "!":
                return evalBangOperatorExpression(right);
            case "-":
                return evalMinusOperatorExpression(right);
            default:
                return newErr("unknown operator: %s%s",operator,right.type());
        }
    }

    private static Obj evalMinusOperatorExpression(Obj right) {

        if (!right.type().equals(ObjType.INTEGER_OBJ)) {
            return newErr("unknown operator: -%s", right.type());
        }

        return new Int(-((Int) right).value);

    }

    private static Obj evalBangOperatorExpression(Obj right) {
        if (right == TRUE) {
            return FALSE;
        } else if (right == FALSE) {
            return TRUE;
        } else if (right == NULL) {
            return TRUE;
        } else {
            return FALSE;
        }
    }

    private static Obj nativeBoolToBooleanObj(boolean input) {
        if (input) {
            return TRUE;
        } else {
            return FALSE;
        }
    }
    private static Obj evalBlockStatement(BlockStatement node, Environment env) {
        Obj result = null;
        for (Statement stmt : node.statements) {
            result = eval(stmt,env);
            if ( result instanceof ReturnValue || result instanceof Err) {
                return  result;
            }
        }
        return result;
    }

    private static Obj evalProgram(Program program,Environment env) {
        Obj result = null;
        for (Statement stmt : program.statements) {
            result = eval(stmt,env);
            if (result instanceof ReturnValue) {
                return ((ReturnValue) result).value;
            }
            if (result instanceof Err) {
                return result;
            }
        }
        return result;
    }

    private static Err newErr(String format,Object...a) {
        return new Err(String.format(format,a));
    }

    private static boolean isErr(Obj obj) {
        if (obj != null) {
            return obj.type().equals(ObjType.ERROR_OBJ);
        }
        return false;
    }
}
