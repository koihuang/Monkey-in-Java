package com.koi.monkey.vm;

import com.koi.monkey.object.CompiledFunction;

import java.util.List;

/**
 * @author whuang
 * @date 2019/11/28
 */
public class Frame {
    public CompiledFunction fn;
    public int ip;
    public int basePointer;

    public Frame(CompiledFunction fn, int basePointer) {
        this.fn = fn;
        this.ip = -1;
        this.basePointer = basePointer;
    }

    public Frame(CompiledFunction fn) {
        this.fn = fn;
        this.ip = -1;
    }

    public List<Byte> instructions() {
        return this.fn.instructions;
    }
}
