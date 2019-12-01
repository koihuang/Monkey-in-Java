package com.koi.monkey.vm;

import com.koi.monkey.object.Closure;
import com.koi.monkey.object.CompiledFunction;

import java.util.List;

/**
 * @author whuang
 * @date 2019/11/28
 */
public class Frame {
    public Closure cl;
    public int ip;
    public int basePointer;

    public Frame(Closure cl, int basePointer) {
        this.cl = cl;
        this.ip = -1;
        this.basePointer = basePointer;
    }

    public Frame(Closure cl) {
        this.cl = cl;
        this.ip = -1;
    }

    public List<Byte> instructions() {
        return this.cl.fn.instructions;
    }
}
