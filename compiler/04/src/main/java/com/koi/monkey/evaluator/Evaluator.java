package com.koi.monkey.evaluator;

import com.koi.monkey.ast.*;
import com.koi.monkey.object.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author whuang
 * @date 2019/10/28
 */
public class Evaluator {

    public static final Bool TRUE = new Bool(true);
    public static final Bool FALSE = new Bool(false);
    public static final Nil NULL = new Nil();

    public static Map<String, Builtin> builtins = new HashMap<>();

    static {

        BuiltinFunction len = args -> {
            if (args.length != 1) {
                return newErr("wrong number of arguments. got=%d, want=1", args.length);
            }
            if (args[0] instanceof Str) {
                return new Int(((Str) args[0]).value.length());
            }

            if (args[0] instanceof Arr) {
                return new Int(((Arr) args[0]).elements.length);
            }
            return newErr("argument to `len` not supported, got %s", args[0].type());
        };
        BuiltinFunction first = args -> {
            if (args.length != 1) {
                return newErr("wrong number of arguments. got=%d, want=1", args.length);
            }

            if (!(args[0] instanceof Arr)) {
                return newErr("argument to `first` must be ARRAY, got %s", args[0].type());
            }

            Arr arr = (Arr) args[0];
            if (arr.elements.length > 0) {
                return arr.elements[0];
            }
            return NULL;
        };
        BuiltinFunction last = args -> {
            if (args.length != 1) {
                return newErr("wrong number of arguments. got=%d, want=1", args.length);
            }

            if (!(args[0] instanceof Arr)) {
                return newErr("argument to `first` must be ARRAY, got %s", args[0].type());
            }

            Arr arr = (Arr) args[0];
            if (arr.elements.length > 0) {
                return arr.elements[arr.elements.length - 1];
            }
            return NULL;
        };
        BuiltinFunction rest = args -> {
            if (args.length != 1) {
                return newErr("wrong number of arguments. got=%d, want=1", args.length);
            }

            if (!(args[0] instanceof Arr)) {
                return newErr("argument to `rest` must be ARRAY, got %s", args[0].type());
            }

            Arr arr = (Arr) args[0];
            int length = arr.elements.length;
            if (length > 0) {
                Obj[] newElements = new Obj[length - 1];
                System.arraycopy(arr.elements, 1, newElements, 0, length - 1);
                return new Arr(newElements);
            }
            return NULL;
        };
        BuiltinFunction push = args -> {
            if (args.length != 2) {
                return newErr("wrong number of arguments. got=%d, want=2", args.length);
            }

            if (!(args[0] instanceof Arr)) {
                return newErr("argument to `push` must be ARRAY, got %s", args[0].type());
            }

            Arr arr = (Arr) args[0];
            int length = arr.elements.length;
            Obj[] newElements = new Obj[length + 1];
            System.arraycopy(arr.elements, 0, newElements, 0, length);
            newElements[length] = args[1];
            return new Arr(newElements);
        };

        BuiltinFunction puts = args -> {
            for (Obj arg : args) {
                System.out.println(arg.inspect());
            }
            return NULL;
        };
        builtins.put("len", new Builtin(len));
        builtins.put("first", new Builtin(first));
        builtins.put("last", new Builtin(last));
        builtins.put("rest", new Builtin(rest));
        builtins.put("push", new Builtin(push));
        builtins.put("puts", new Builtin(puts));
    }

    public Obj eval(Node node, Environment env) {
        if (node instanceof Program) {
            return evalProgram(((Program) node), env);
        }

        if (node instanceof BlockStatement) {
            return evalBlockStatement(((BlockStatement) node), env);
        }
        if (node instanceof ReturnStatement) {
            Obj val = eval(((ReturnStatement) node).returnValue, env);
            if (isErr(val)) {
                return val;
            }
            return new ReturnValue(val);
        }
        if (node instanceof ExpressionStatement) {
            return eval(((ExpressionStatement) node).expression, env);
        }

        if (node instanceof LetStatement) {
            Obj val = eval(((LetStatement) node).value, env);
            if (isErr(val)) {
                return val;
            }
            env.set(((LetStatement) node).name.value, val);
        }
        if (node instanceof IntegerLiteral) {
            return new Int(((IntegerLiteral) node).value);
        }

        if (node instanceof BooleanLiteral) {
            return nativeBoolToBooleanObj(((BooleanLiteral) node).value);
        }

        if (node instanceof StringLiteral) {
            return new Str(((StringLiteral) node).value);
        }

        if (node instanceof ArrayLiteral) {
            Obj[] elements = evalExpressions(((ArrayLiteral) node).elements, env);
            if (elements.length == 1 && isErr(elements[0])) {
                return elements[0];
            }
            return new Arr(elements);
        }

        if (node instanceof Identifier) {
            return evalIdentifier((Identifier) node, env);
        }

        if (node instanceof FunctionLiteral) {
            return new Function(((FunctionLiteral) node).parameters, ((FunctionLiteral) node).body, env);
        }

        if (node instanceof HashLiteral) {
            return evalHashLiteral((HashLiteral)node,env);
        }

        if (node instanceof PrefixExpression) {
            Obj right = eval(((PrefixExpression) node).right, env);
            if (isErr(right)) {
                return right;
            }
            return evalPrefixExpression(((PrefixExpression) node).operator, right);
        }

        if (node instanceof InfixExpression) {
            Obj left = eval(((InfixExpression) node).left, env);
            if (isErr(left)) {
                return left;
            }
            Obj right = eval(((InfixExpression) node).right, env);
            if (isErr(right)) {
                return right;
            }
            return evalInfixExpression(((InfixExpression) node).operator, left, right);
        }

        if (node instanceof IndexExpression) {
            Obj left = eval(((IndexExpression) node).left, env);
            if (isErr(left)) {
                return left;
            }
            Obj index = eval(((IndexExpression) node).index, env);
            if (isErr(index)) {
                return left;
            }
            return evalIndexExpression(left, index);
        }

        if (node instanceof IfExpression) {
            return evalIfExpression((IfExpression) node, env);
        }

        if (node instanceof CallExpression) {
            Obj function = eval(((CallExpression) node).function, env);
            if (isErr(function)) {
                return function;
            }
            Obj[] args = evalExpressions(((CallExpression) node).arguments, env);
            if (args.length == 1 && isErr(args[0])) {
                return args[0];
            }
            return applyFunction(function, args);
        }

        return null;
    }

    private Obj evalHashLiteral(HashLiteral node, Environment env) {
        Map<HashKey,HashPair> pairs = new HashMap<>();
        for (Map.Entry<Expression, Expression> pair : node.pairs.entrySet()) {
            Obj key = eval(pair.getKey(), env);
            if (isErr(key)) {
                return key;
            }
            if (!(key instanceof Hashable)) {
                return newErr("unusable as hash key: %s", key.type());
            }
            Obj value = eval(pair.getValue(), env);
            if (isErr(value)) {
                return value;
            }
            pairs.put(((Hashable) key).hashKey(),new HashPair(key,value));
        }
        return new Hash(pairs);
    }

    private Obj evalIndexExpression(Obj left, Obj index) {

        if (left.type().equals(ObjType.ARRAY_OBJ) && index.type().equals(ObjType.INTEGER_OBJ)) {
            return evalArrayIndexExpression(left, index);
        }
        if (left.type().equals(ObjType.HASH_OBJ)) {
            return evalHashIndexExpression(left,index);
        }
        return newErr("index operator not supported: %s", left.type());

    }

    private Obj evalHashIndexExpression(Obj hash, Obj index) {
        Hash hashObj = (Hash) hash;
        if (!(index instanceof Hashable)) {
            return newErr("unusable as hash key: %s", index.type());
        }
        Hashable key = (Hashable) index;
        if (hashObj.pairs.containsKey(key.hashKey())) {
            return hashObj.pairs.get(key.hashKey()).value;
        } else {
            return NULL;
        }
    }

    private Obj evalArrayIndexExpression(Obj left, Obj index) {
        Arr arrObj = (Arr) left;
        int idx = ((Int) index).value;
        int max = arrObj.elements.length - 1;

        if (idx < 0 || idx > max) {
            return NULL;
        }
        return arrObj.elements[idx];
    }

    private Obj applyFunction(Obj fn, Obj[] args) {
        if (fn instanceof Function) {
            Environment extendedEnv = extendFunctionEnv((Function) fn, args);
            Obj evaluated = eval(((Function) fn).body, extendedEnv);
            return unwrapReturnValue(evaluated);
        }

        if (fn instanceof Builtin) {
            return ((Builtin) fn).fn.fn(args);
        }

        return newErr("not a function: %s", fn.type());
    }

    private Obj unwrapReturnValue(Obj obj) {
        if (obj instanceof ReturnValue) {
            return ((ReturnValue) obj).value;
        }
        return obj;
    }

    private Environment extendFunctionEnv(Function fn, Obj[] args) {
        Environment env = Environment.newEnclosedEnvironment(fn.env);
        for (int i = 0; i < fn.parameters.size(); i++) {
            env.set(fn.parameters.get(i).value, args[i]);
        }
        return env;
    }

    private Obj[] evalExpressions(List<Expression> exps, Environment env) {
        Obj[] result = new Obj[exps.size()];
        for (int i = 0; i < exps.size(); i++) {
            Obj evaluated = eval(exps.get(i), env);
            if (isErr(evaluated)) {
                return new Obj[]{evaluated};
            }
            result[i] = evaluated;
        }
        return result;
    }

    private Obj evalIdentifier(Identifier node, Environment env) {
        Obj val = env.get(node.value);
        if (val == null) {
            if (builtins.containsKey(node.value)) {
                return builtins.get(node.value);
            }
            return newErr("identifier not found: " + node.value);
        }
        return val;
    }


    private Obj evalIfExpression(IfExpression ie, Environment env) {
        Obj condition = eval(ie.condition, env);
        if (isErr(condition)) {
            return condition;
        }
        if (isTruthy(condition)) {
            return eval(ie.consequence, env);
        } else if (ie.alternative != null) {
            return eval(ie.alternative, env);
        } else {
            return NULL;
        }
    }

    private boolean isTruthy(Obj obj) {
        if (obj == NULL) {
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

    private Obj evalInfixExpression(String operator, Obj left, Obj right) {
        if (left.type().equals(ObjType.INTEGER_OBJ) && right.type().equals(ObjType.INTEGER_OBJ)) {
            return evalIntegerInfixExpression(operator, left, right);
        }
        if (left.type().equals(ObjType.STRING_OBJ) && right.type().equals(ObjType.STRING_OBJ)) {
            return evalStringInfixExpression(operator, left, right);
        }
        if (operator.equals("==")) {
            return nativeBoolToBooleanObj(left == right);
        }

        if (operator.equals("!=")) {
            return nativeBoolToBooleanObj(left != right);
        }

        if (!left.type().equals(right.type())) {
            return newErr("type mismatch: %s %s %s", left.type(), operator, right.type());
        }
        return newErr("unknown operator: %s %s %s", left.type(), operator, right.type());
    }

    private Obj evalStringInfixExpression(String operator, Obj left, Obj right) {
        if (!"+".equals(operator)) {
            return newErr("unknown operator: %s %s %s", left.type(), operator, right.type());
        }
        return new Str(((Str) left).value + ((Str) right).value);
    }

    private Obj evalIntegerInfixExpression(String operator, Obj left, Obj right) {
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
                return newErr("unknown operator: %s %s %s", left.type(), operator, right.type());
        }
    }

    private Obj evalPrefixExpression(String operator, Obj right) {
        switch (operator) {
            case "!":
                return evalBangOperatorExpression(right);
            case "-":
                return evalMinusOperatorExpression(right);
            default:
                return newErr("unknown operator: %s%s", operator, right.type());
        }
    }

    private Obj evalMinusOperatorExpression(Obj right) {

        if (!right.type().equals(ObjType.INTEGER_OBJ)) {
            return newErr("unknown operator: -%s", right.type());
        }

        return new Int(-((Int) right).value);

    }

    private Obj evalBangOperatorExpression(Obj right) {
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

    private Obj nativeBoolToBooleanObj(boolean input) {
        if (input) {
            return TRUE;
        } else {
            return FALSE;
        }
    }

    private Obj evalBlockStatement(BlockStatement node, Environment env) {
        Obj result = null;
        for (Statement stmt : node.statements) {
            result = eval(stmt, env);
            if (result instanceof ReturnValue || result instanceof Err) {
                return result;
            }
        }
        return result;
    }

    private Obj evalProgram(Program program, Environment env) {
        Obj result = null;
        for (Statement stmt : program.statements) {
            result = eval(stmt, env);
            if (result instanceof ReturnValue) {
                return ((ReturnValue) result).value;
            }
            if (result instanceof Err) {
                return result;
            }
        }
        return result;
    }

    private static Err newErr(String format, Object... a) {
        return new Err(String.format(format, a));
    }

    private boolean isErr(Obj obj) {
        if (obj != null) {
            return obj.type().equals(ObjType.ERROR_OBJ);
        }
        return false;
    }
}
