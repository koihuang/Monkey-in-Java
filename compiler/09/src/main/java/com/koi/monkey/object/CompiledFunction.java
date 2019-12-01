package com.koi.monkey.object;

import java.util.List;

/**
 * @author whuang
 * @date 2019/11/27
 */
public class CompiledFunction implements Obj {


    public List<Byte> instructions;
    public int numLocals;
    public int numParameters;

    public CompiledFunction(List<Byte> instructions, int numLocals, int numParameters) {
        this.instructions = instructions;
        this.numLocals = numLocals;
        this.numParameters = numParameters;
    }

    public CompiledFunction() {
    }

    @Override
    public String type() {
        return ObjType.COMPILED_FUNCTION_OBJ;
    }

    public CompiledFunction(List<Byte> instructions, int numLocals) {
        this.instructions = instructions;
        this.numLocals = numLocals;
    }

    public CompiledFunction(List<Byte> instructions) {
        this.instructions = instructions;
    }

    @Override
    public String inspect() {
        return String.format("CompiledFunction[%s]",this);
    }
}
