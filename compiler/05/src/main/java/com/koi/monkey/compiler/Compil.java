package com.koi.monkey.compiler;

import com.koi.monkey.ast.*;
import com.koi.monkey.code.Code;
import com.koi.monkey.object.Bool;
import com.koi.monkey.object.Int;
import com.koi.monkey.object.Obj;
import com.koi.monkey.object.Str;

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

    private EmittedInstruction lastInstruction = new EmittedInstruction();
    private EmittedInstruction previousInstruction = new EmittedInstruction();

    private SymbolTable symbolTable = new SymbolTable();

    public Compil() {

    }

    public Compil(List<Byte> instructions, List<Obj> constants) {
        this.instructions = instructions;
        this.constants = constants;
    }

    public static Compil newWithState(SymbolTable s,List<Obj> constants) {
        Compil compil = new Compil();
        compil.symbolTable = s;
        compil.constants =constants;
        return compil;
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
            emit(Code.OpPop);
            return;
        }

        if (node instanceof InfixExpression) {
            if (((InfixExpression) node).operator.equals("<")) {
                compile(((InfixExpression) node).right);
                compile(((InfixExpression) node).left);
                emit(Code.OpGreaterThan);
                return;
            }
            compile(((InfixExpression) node).left);
            compile(((InfixExpression) node).right);
            switch (((InfixExpression) node).operator) {
                case "+":
                    emit(Code.OpAdd);
                    break;
                case "-":
                    emit(Code.OpSub);
                    break;
                case "*":
                    emit(Code.OpMul);
                    break;
                case "/":
                    emit(Code.OpDiv);
                    break;
                case ">":
                    emit(Code.OpGreaterThan);
                    break;
                case "==":
                    emit(Code.OpEqual);
                    break;
                case "!=":
                    emit(Code.OpNotEqual);
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

        if (node instanceof BooleanLiteral) {
            if (((BooleanLiteral) node).value) {
                emit(Code.OpTrue);
            } else {
                emit(Code.OpFalse);
            }
            return;
        }

        if (node instanceof PrefixExpression) {

            compile(((PrefixExpression) node).right);
            switch (((PrefixExpression) node).operator) {
                case "!":
                    emit(Code.OpBang);
                    break;
                case "-":
                    emit(Code.OpMinus);
                    break;
                    default:
                        throw new RuntimeException(String.format("unknown operator %s", ((PrefixExpression) node).operator));
            }
            return;
        }

        if (node instanceof IfExpression) {
            compile(((IfExpression) node).condition);

            int jumpNotTruthyPos = emit(Code.OpJumpNotTruthy,9999);

            compile(((IfExpression) node).consequence);

            if (lastInstructionIsPop()) {
                removeLastPop();
            }

            int jumpPos = emit(Code.OpJump,9999);
            int afterConsequencePos = this.instructions.size();
            changeOperand(jumpNotTruthyPos,afterConsequencePos);
            if (((IfExpression) node).alternative == null) {
                emit(Code.OpNull);
            } else {
                compile(((IfExpression) node).alternative);
                if (lastInstructionIsPop()) {
                    removeLastPop();
                }
            }
            int afterAlternativePos = this.instructions.size();
            changeOperand(jumpPos,afterAlternativePos);

            return;
        }

        if (node instanceof BlockStatement) {
            for (Statement s : ((BlockStatement) node).statements) {
                compile(s);
            }
        }

        if (node instanceof LetStatement) {
            compile(((LetStatement) node).value);
            Symbol symbol = this.symbolTable.define(((LetStatement) node).name.value);
            emit(Code.OpSetGlobal,symbol.index);
        }

        if (node instanceof Identifier) {
            Symbol symbol = this.symbolTable.resolve(((Identifier) node).value);
            emit(Code.OpGetGlobal,symbol.index);
        }

        if (node instanceof StringLiteral) {
            Str str = new Str(((StringLiteral) node).value);
            emit(Code.OpConstant,addConstant(str));
        }


    }

    private void changeOperand(int opPos,int operand) {
        byte op = this.instructions.get(opPos);
        byte[] newInstruction = Code.make(op,operand);
        replaceInstruction(opPos,newInstruction);
    }

    private void replaceInstruction(int pos,byte[] newInstruction) {
        for (int i = 0; i < newInstruction.length; i++) {
            this.instructions.set(pos + i, newInstruction[i]);
        }
    }

    private void removeLastPop() {
        this.instructions = this.instructions.subList(0,this.lastInstruction.position);
        this.lastInstruction = this.previousInstruction;
    }

    private boolean lastInstructionIsPop() {
        return this.lastInstruction.opCode == Code.OpPop;
    }

    private int addConstant(Obj obj) {
        this.constants.add(obj);
        return this.constants.size() - 1;
    }

    private int emit(byte op, int... operands) {
        byte[] ins = Code.make(op, operands);
        int pos = addInstruction(ins);
        setLastInstruction(op,pos);
        return pos;
    }

    private void setLastInstruction(byte op, int pos) {
        EmittedInstruction previous = lastInstruction;
        EmittedInstruction last = new EmittedInstruction(op,pos);
        this.previousInstruction = previous;
        this.lastInstruction = last;
    }

    private int addInstruction(byte[] ins) {
        int posNewInstruction = this.instructions.size();
        for (byte in : ins) {
            this.instructions.add(in);
        }
        return posNewInstruction;
    }

}


















