package com.koi.monkey.compiler;

/**
 * @author whuang
 * @date 2019/11/24
 */
public class EmittedInstruction {
    public byte opCode;
    public int position;

    public EmittedInstruction(byte opCode, int position) {
        this.opCode = opCode;
        this.position = position;
    }

    public EmittedInstruction() {
    }
}
