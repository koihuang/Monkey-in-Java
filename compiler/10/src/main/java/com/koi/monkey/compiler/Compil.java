package com.koi.monkey.compiler;

import com.koi.monkey.ast.*;
import com.koi.monkey.code.Code;
import com.koi.monkey.object.*;

import java.util.*;

/**
 * @author whuang
 * @date 2019/11/23
 */
public class Compil {
    public List<Byte> instructions = new ArrayList<>();
    public List<Obj> constants = new ArrayList<>();

    private EmittedInstruction lastInstruction = new EmittedInstruction();
    private EmittedInstruction previousInstruction = new EmittedInstruction();

    public SymbolTable symbolTable = new SymbolTable();

    public List<CompilationScope> scopes = new ArrayList<>();
    {
        scopes.add(new CompilationScope());

        Set<Map.Entry<String, Builtin>> builtins = Builtins.builtins.entrySet();
        int i = 0;
        for (Map.Entry<String, Builtin> builtin : builtins) {
            this.symbolTable.defineBuiltin(i,builtin.getKey());
            i++;
        }
    }

    public int scopeIndex = 0;

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
        return new Bytecode(currentInstructions(), this.constants);
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

            if (lastInstructionIs(Code.OpPop)) {
                removeLastPop();
            }

            int jumpPos = emit(Code.OpJump,9999);
            int afterConsequencePos = currentInstructions().size();
            changeOperand(jumpNotTruthyPos,afterConsequencePos);
            if (((IfExpression) node).alternative == null) {
                emit(Code.OpNull);
            } else {
                compile(((IfExpression) node).alternative);
                if (lastInstructionIs(Code.OpPop)) {
                    removeLastPop();
                }
            }
            int afterAlternativePos = currentInstructions().size();
            changeOperand(jumpPos,afterAlternativePos);

            return;
        }

        if (node instanceof BlockStatement) {
            for (Statement s : ((BlockStatement) node).statements) {
                compile(s);
            }
            return;
        }

        if (node instanceof LetStatement) {

            Symbol symbol = this.symbolTable.define(((LetStatement) node).name.value);
            compile(((LetStatement) node).value);
            if (symbol.scope.equals(SymbolTable.GloableScope)) {
                emit(Code.OpSetGlobal,symbol.index);
            } else {
                emit(Code.OpSetLocal,symbol.index);
            }

            return;
        }

        if (node instanceof Identifier) {
            Symbol symbol = this.symbolTable.resolve(((Identifier) node).value);
            loadSymbol(symbol);
            return;
        }

        if (node instanceof StringLiteral) {
            Str str = new Str(((StringLiteral) node).value);
            emit(Code.OpConstant,addConstant(str));
            return;
        }

        if (node instanceof ArrayLiteral) {
            for (Expression el : ((ArrayLiteral) node).elements) {
                compile(el);
            }
            emit(Code.OpArray,((ArrayLiteral) node).elements.size());
            return;
        }

        if (node instanceof HashLiteral) {
            Expression[] keys = ((HashLiteral) node).pairs.keySet().toArray(new Expression[0]);
            Arrays.sort(keys, new Comparator<Expression>() {
                @Override
                public int compare(Expression o1, Expression o2) {
                    return o1.string().compareTo(o2.string());
                }
            });
            for (Expression k : keys) {
                compile(k);
                compile(((HashLiteral) node).pairs.get(k));
            }
            emit(Code.OpHash,((HashLiteral) node).pairs.size()*2);
            return;
        }

        if (node instanceof IndexExpression) {
            compile(((IndexExpression) node).left);
            compile(((IndexExpression) node).index);
            emit(Code.OpIndex);
            return;
        }

        if (node instanceof FunctionLiteral) {
            enterScope();

            for (Identifier p : ((FunctionLiteral) node).parameters) {
                this.symbolTable.define(p.value);
            }

            compile(((FunctionLiteral) node).body);

            if (lastInstructionIs(Code.OpPop)) {
                replaceLastPopWithReturn();
            }

            if (!lastInstructionIs(Code.OpReturnValue)) {
                emit(Code.OpReturn);
            }

            List<Symbol> freeSymbols = this.symbolTable.freeSymbols;

            int numLocals = this.symbolTable.numDefinitions;
            List<Byte> instructions = leaveScope();

            for (Symbol s : freeSymbols) {
                loadSymbol(s);
            }
            CompiledFunction compiledFn = new CompiledFunction(instructions,numLocals,((FunctionLiteral) node).parameters.size());
            int fnIndex = this.addConstant(compiledFn);
            emit(Code.OpClosure,fnIndex,freeSymbols.size());
            return;
        }

        if (node instanceof ReturnStatement) {
            compile(((ReturnStatement) node).returnValue);
            emit(Code.OpReturnValue);
        }

        if (node instanceof CallExpression) {
            compile(((CallExpression) node).function);
            for (Expression a : ((CallExpression) node).arguments) {
                compile(a);
            }
            emit(Code.OpCall,((CallExpression) node).arguments.size());
        }

    }

    public void loadSymbol(Symbol s) {
        switch (s.scope) {
            case SymbolTable.GloableScope:
                emit(Code.OpGetGlobal,s.index);
                break;
            case SymbolTable.LocalScope:
                emit(Code.OpGetLocal,s.index);
                break;
            case SymbolTable.BuiltinScope:
                emit(Code.OpGetBuiltin,s.index);
                break;
            case SymbolTable.FreeScope:
                emit(Code.OpGetFree,s.index);
                break;
        }
    }


    public static SymbolTable newEnclosedSymbolTable(SymbolTable outer) {
        SymbolTable s = new SymbolTable();
        s.outer = outer;
        return s;
    }

    private void replaceLastPopWithReturn() {
        int lastPos = this.scopes.get(this.scopeIndex).lastInstruction.position;
        replaceInstruction(lastPos,Code.make(Code.OpReturnValue));
        this.scopes.get(this.scopeIndex).lastInstruction.opCode = Code.OpReturnValue;
    }

    public List<Byte> leaveScope() {
        List<Byte> instructions = currentInstructions();
        this.scopes = this.scopes.subList(0,this.scopes.size()-1);
        this.scopeIndex--;
        this.symbolTable = this.symbolTable.outer;
        return instructions;
    }

    public void enterScope() {
        CompilationScope scope = new CompilationScope();
        this.scopes.add(scope);
        this.scopeIndex++;
        this.symbolTable = newEnclosedSymbolTable(this.symbolTable);
    }

    private boolean lastInstructionIs(byte op) {
        if (currentInstructions().size() == 0) {
            return false;
        }
        return this.scopes.get(this.scopeIndex).lastInstruction.opCode == op;
    }


    public List<Byte> currentInstructions() {
        return this.scopes.get(this.scopeIndex).instructions;
    }

    private void changeOperand(int opPos,int operand) {
        byte op = currentInstructions().get(opPos);
        byte[] newInstruction = Code.make(op,operand);
        replaceInstruction(opPos,newInstruction);
    }

    private void replaceInstruction(int pos,byte[] newInstruction) {
        for (int i = 0; i < newInstruction.length; i++) {
            this.scopes.get(this.scopeIndex).instructions.set(pos+i, newInstruction[i]);
        }
    }

    private void removeLastPop() {
        this.scopes.get(this.scopeIndex).instructions = this.scopes.get(this.scopeIndex).instructions.subList(0,this.scopes.get(this.scopeIndex).lastInstruction.position);
        this.scopes.get(this.scopeIndex).lastInstruction = this.scopes.get(this.scopeIndex).previousInstruction;
    }

    private boolean lastInstructionIsPop() {
        return this.scopes.get(this.scopeIndex).lastInstruction.opCode == Code.OpPop;
    }

    private int addConstant(Obj obj) {
        this.constants.add(obj);
        return this.constants.size() - 1;
    }

    public int emit(byte op, int... operands) {
        byte[] ins = Code.make(op, operands);
        int pos = addInstruction(ins);
        setLastInstruction(op,pos);
        return pos;
    }

    private void setLastInstruction(byte op, int pos) {
        EmittedInstruction previous = this.scopes.get(this.scopeIndex).lastInstruction;
        EmittedInstruction last = new EmittedInstruction(op,pos);
        this.scopes.get(this.scopeIndex).previousInstruction = previous;
        this.scopes.get(this.scopeIndex).lastInstruction = last;
    }

    private int addInstruction(byte[] ins) {
        int posNewInstruction = currentInstructions().size();
        if (ins != null) {
            for (byte in : ins) {
                currentInstructions().add(in);
            }
        }
        return posNewInstruction;
    }

}


















