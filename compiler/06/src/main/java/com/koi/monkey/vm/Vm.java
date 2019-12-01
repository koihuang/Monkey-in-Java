package com.koi.monkey.vm;


import com.koi.monkey.code.Code;
import com.koi.monkey.compiler.Bytecode;
import com.koi.monkey.object.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author whuang
 * @date 2019/11/23
 */
public class Vm {

    private List<Obj> constants;
    private List<Byte> instructions;
    private Obj[] stack;
    private int sp;
    public static final int STACK_SIZE = 128;
    public final Bool True = new Bool(true);
    public final Bool False = new Bool(false);
    public static final Nil NULL = new Nil();
    public static final int GlobalsSize = 128;
    public Obj[] globals;

    public Vm(Bytecode bytecode) {
        this.instructions = bytecode.instructions;
        this.constants = bytecode.constants;
        stack = new Obj[STACK_SIZE];
        sp = 0;
        globals = new Obj[GlobalsSize];
    }

    public Vm() {
    }

    public static Vm newWithGlobalStore(Bytecode bytecode, Obj[] s) {
        Vm vm = new Vm(bytecode);
        vm.globals = s;
        return vm;
    }

    public Obj lastPoppedStackElem() {
        return this.stack[this.sp];
    }

    public Obj stackTop() {
        if (this.sp == 0) {
            return null;
        }
        return this.stack[this.sp - 1];
    }

    public void run() {
        for (int ip = 0; ip < instructions.size(); ip++) {
            final byte op = instructions.get(ip);
            switch (op) {
                case Code.OpConstant:
                    int constIndex = instructions.get(ip + 1);
                    ip++;
                    push(this.constants.get(constIndex));
                    break;
                case Code.OpAdd:
                case Code.OpSub:
                case Code.OpMul:
                case Code.OpDiv:
                    executeBinaryOperation(op);
                    break;
                case Code.OpTrue:
                    push(True);
                    break;
                case Code.OpFalse:
                    push(False);
                    break;
                case Code.OpEqual:
                case Code.OpNotEqual:
                case Code.OpGreaterThan:
                    executeComparison(op);
                    break;
                case Code.OpBang:
                    executeBangOperator();
                    break;
                case Code.OpMinus:
                    executeMinusOperator();
                    break;
                case Code.OpPop:
                    pop();
                    break;
                case Code.OpJump:
                    int pos = this.instructions.get(ip + 1);
                    ip = pos - 1;
                    break;
                case Code.OpJumpNotTruthy:
                    int pos2 = this.instructions.get(ip + 1);
                    ip += 1;
                    Obj condition = pop();
                    if (!isTruthy(condition)) {
                        ip = pos2 - 1;
                    }
                    break;
                case Code.OpNull:
                    push(NULL);
                    break;
                case Code.OpSetGlobal:
                    int globalIndex = this.instructions.get(ip + 1);
                    ip++;
                    this.globals[globalIndex] = pop();
                    break;
                case Code.OpGetGlobal:
                    int globalIndex1 = this.instructions.get(ip+1);
                    ip++;
                    push(globals[globalIndex1]);
                    break;
                case Code.OpArray:
                    int numElements = this.instructions.get(ip + 1);
                    ip++;
                    Obj array = buildArray(this.sp - numElements,this.sp);
                    this.sp = this.sp - numElements;
                    push(array);
                    break;
                case Code.OpHash:
                    numElements = this.instructions.get(ip+1);
                    ip++;
                    Obj hash = buildHash(this.sp - numElements,this.sp);
                    this.sp = this.sp - numElements;
                    push(hash);
                    break;
                case Code.OpIndex:
                    Obj index = pop();
                    Obj left = pop();
                    executeIndexExpression(left,index);
                    break;
            }
        }
    }

    private void executeIndexExpression(Obj left, Obj index) {
        if (left instanceof Arr && index instanceof Int) {
            exexcuteArrayIndex(left,index);
            return;
        }
        if (left instanceof Hash) {
            exexcuteHashIndex(left,index);
            return;
        }
        throw new RuntimeException(String.format("index operator not supported: %",left.type()));
    }

    private void exexcuteHashIndex(Obj hash, Obj index) {
        Hash hashObject = (Hash)hash;
        Hashable key = (Hashable) index;
        HashPair pair = hashObject.pairs.get(key.hashKey());
        if(pair == null) {
            push(NULL);
            return;
        }
        push(pair.value);
    }

    private void exexcuteArrayIndex(Obj array, Obj index) {
        Arr arrayObject = (Arr) array;
        int i = ((Int)index).value;
        int max = arrayObject.elements.length - 1;
        if ((i < 0 || i > max)) {
            push(NULL);
            return;
        }
        push(arrayObject.elements[i]);
    }

    private Obj buildHash(int startIndex, int endIndex) {
        Map<HashKey,HashPair> hashedPairs = new HashMap<>();
        for (int i = startIndex; i < endIndex; i+=2) {
            Obj key = this.stack[i];
            Obj value = this.stack[i+1];
            HashPair pair = new HashPair(key,value);

            Hashable hashkey = (Hashable) key;
            hashedPairs.put(hashkey.hashKey(),pair);
        }
        return new Hash(hashedPairs);
    }

    private Obj buildArray(int startIndex, int endIndex) {
        Obj[] elements = new Obj[endIndex - startIndex];
        for (int i = startIndex; i < endIndex; i++) {
            elements[i-startIndex] = this.stack[i];
        }
        return new Arr(elements);
    }

    private boolean isTruthy(Obj obj) {
        if (obj instanceof Bool) {
            return ((Bool) obj).value;
        }

        if (obj == NULL) {
            return false;
        }
        return true;
    }


    private void executeMinusOperator() {
        Obj operand = pop();
        if (!(operand instanceof Int)) {
            throw new RuntimeException(String.format("unsupported type for negation: %s", operand.type()));
        }
        push(new Int(-((Int) operand).value));
    }

    private void executeBangOperator() {
        Obj operand = pop();
        if (operand == True) {
            push(False);
            return;
        }
        if (operand == False) {
            push(True);
            return;
        }

        if (operand == NULL) {
            push(True);
            return;
        }
        push(False);
    }

    private void executeComparison(byte op) {
        Obj right = pop();
        Obj left = pop();
        if (left instanceof Int && right instanceof Int) {
            executeIntegerComparison(op, left, right);
            return;
        }

        switch (op) {
            case Code.OpEqual:
                push(nativeBoolToBooleanObj(right == left));
                break;
            case Code.OpNotEqual:
                push(nativeBoolToBooleanObj(right != left));
                break;
            default:
                throw new RuntimeException(String.format("unknown operator: %d (%s %s)", op, left, left.type(), right.type()));
        }
    }

    private Obj nativeBoolToBooleanObj(boolean input) {
        if (input) {
            return True;
        } else {
            return False;
        }
    }

    private void executeIntegerComparison(byte op, Obj left, Obj right) {
        int leftValue = ((Int) left).value;
        int rightValue = ((Int) right).value;
        switch (op) {
            case Code.OpEqual:
                push(nativeBoolToBooleanObj(leftValue == rightValue));
                break;
            case Code.OpNotEqual:
                push(nativeBoolToBooleanObj(leftValue != rightValue));
                break;
            case Code.OpGreaterThan:
                push(nativeBoolToBooleanObj(leftValue > rightValue));
                break;
            default:
                throw new RuntimeException(String.format("unknown operator: %d", op));
        }
    }

    public void executeBinaryOperation(byte op) {
        Obj right = pop();
        Obj left = pop();

        if (left instanceof Int && right instanceof Int) {
            executeBinaryIntegerOperation(op, left, right);
            return;
        }

        if (left instanceof Str && right instanceof Str) {
            executeBinaryStringOperation(op,left,right);
            return;
        }
        throw new RuntimeException(String.format("unsupported types for binary operation: %s %s", left.type(), right.type()));
    }

    private void executeBinaryStringOperation(byte op, Obj left, Obj right) {
        if (op != Code.OpAdd) {
            throw new RuntimeException(String.format("unknown string operator:%d",op));
        }

        String leftValue =  ((Str)left).value;
        String rightValue =  ((Str)right).value;
        push(new Str(leftValue+rightValue));
    }

    private void executeBinaryIntegerOperation(byte op, Obj left, Obj right) {
        int leftValue = ((Int) left).value;
        int rightValue = ((Int) right).value;
        int result;
        switch (op) {
            case Code.OpAdd:
                result = leftValue + rightValue;
                break;
            case Code.OpSub:
                result = leftValue - rightValue;
                break;
            case Code.OpMul:
                result = leftValue * rightValue;
                break;
            case Code.OpDiv:
                result = leftValue / rightValue;
                break;

            default:
                throw new RuntimeException(String.format("unknown integer operator: %d", op));
        }
        push(new Int(result));
    }

    private void push(Obj obj) {
        if (this.sp >= STACK_SIZE) {
            throw new RuntimeException("stack overflow");
        }
        this.stack[this.sp] = obj;
        this.sp++;
    }

    private Obj pop() {
        Obj o = this.stack[this.sp - 1];
        this.sp--;
        return o;
    }
}
