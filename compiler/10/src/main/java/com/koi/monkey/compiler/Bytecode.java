package com.koi.monkey.compiler;

import com.koi.monkey.object.Obj;

import java.util.List;

/**
 * @author whuang
 * @date 2019/11/23
 */
public class Bytecode {
    public List<Byte> instructions ;
    public List<Obj> constants;

    public Bytecode(List<Byte> instructions, List<Obj> constants) {
        this.instructions = instructions;
        this.constants = constants;
    }

    public Bytecode() {

    }
}
