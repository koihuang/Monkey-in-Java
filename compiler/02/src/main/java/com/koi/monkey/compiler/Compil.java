package com.koi.monkey.compiler;

import com.koi.monkey.ast.*;
import com.koi.monkey.code.Code;
import com.koi.monkey.object.Int;
import com.koi.monkey.object.Obj;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author whuang
 * @date 2019/11/23
 */
public class Compil {
    public List<Byte> instructions = new ArrayList<>();
    public List<Obj> constants = new ArrayList<>();

    public Compil() {

    }

    public Compil(List<Byte> instructions, List<Obj> constants) {
        this.instructions = instructions;
        this.constants = constants;
    }

    public Bytecode bytecode() {
        return new Bytecode(this.instructions, this.constants);
    }

    public void compile(Node node) {
        if (node instanceof Program) {
            for (Statement s : ((Program) node).statements) {
                compile(s);
            }
            return;
        }

        if (node instanceof ExpressionStatement) {
            compile(((ExpressionStatement) node).expression);
            return;
        }

        if (node instanceof InfixExpression) {
            compile(((InfixExpression) node).left);
            compile(((InfixExpression) node).right);
            switch (((InfixExpression) node).operator) {
                case "+":
                    emit(Code.OpAdd);
                    break;
                default:
                    throw new RuntimeException(String.format("unknown operator %s", ((InfixExpression) node).operator));
            }
            return;
        }

        if (node instanceof IntegerLiteral) {
            Int integer = new Int(((IntegerLiteral) node).value);
            emit(Code.OpConstant, addConstant(integer));
            return;
        }
    }

    private int addConstant(Obj obj) {
        this.constants.add(obj);
        return this.constants.size() - 1;
    }

    private int emit(byte op, int... operands) {
        byte[] ins = Code.make(op, operands);
        int pos = addInstruction(ins);
        return pos;
    }

    private int addInstruction(byte[] ins) {
        int posNewInstruction = this.instructions.size();
        for (byte in : ins) {
            this.instructions.add(in);
        }
        return posNewInstruction;
    }

}


















