package com.koi.monkey.compiler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author whuang
 * @date 2019/11/27
 */
public class CompilationScope {
    public List<Byte> instructions = new ArrayList<>();
    public EmittedInstruction lastInstruction = new EmittedInstruction();
    public EmittedInstruction previousInstruction = new EmittedInstruction();

    public CompilationScope(List<Byte> instructions, EmittedInstruction lastInstruction, EmittedInstruction previousInstruction) {
        this.instructions = instructions;
        this.lastInstruction = lastInstruction;
        this.previousInstruction = previousInstruction;
    }

    public CompilationScope() {
    }
}
