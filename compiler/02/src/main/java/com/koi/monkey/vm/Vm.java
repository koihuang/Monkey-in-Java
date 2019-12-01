package com.koi.monkey.vm;


import com.koi.monkey.code.Code;
import com.koi.monkey.compiler.Bytecode;
import com.koi.monkey.object.Int;
import com.koi.monkey.object.Obj;

import java.util.List;


/**
 * @author whuang
 * @date 2019/11/23
 */
public class Vm {

    private List<Obj> constants;
    private List<Byte> instructions ;
    private Obj[] stack;
    private int sp;
    public static final int STACK_SIZE = 128;

    public Vm(Bytecode bytecode) {
        this.instructions = bytecode.instructions;
        this.constants = bytecode.constants;
        stack = new Obj[STACK_SIZE];
        sp = 0;
    }

    public Obj stackTop() {
        if (this.sp == 0) {
            return null;
        }
        return this.stack[this.sp-1];
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
                    Obj right = pop();
                    Obj left = pop();
                    int leftValue = ((Int)left).value;
                    int rightValue = ((Int)right).value;
                    int result = leftValue + rightValue;
                    push(new Int(result));
                    break;
            }
        }
    }

    private void push(Obj obj) {
        if (this.sp >= STACK_SIZE) {
            throw new RuntimeException("stack overflow");
        }
        this.stack[this.sp] = obj;
        this.sp++;
    }

    private Obj pop() {
        Obj o = this.stack[this.sp-1];
        this.sp--;
        return o;
    }
}
