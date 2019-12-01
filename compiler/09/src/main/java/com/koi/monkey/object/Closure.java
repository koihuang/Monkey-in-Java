package com.koi.monkey.object;

/**
 * @author whuang
 * @date 2019/12/1
 */
public class Closure implements Obj {

    public CompiledFunction fn;
    public Obj[] free;

    public Closure() {
    }

    public Closure(CompiledFunction fn, Obj[] free) {
        this.fn = fn;
        this.free = free;
    }

    public Closure(CompiledFunction fn) {
        this.fn = fn;
    }

    @Override
    public String type() {
        return ObjType.CLOSURE_OBJ;
    }

    @Override
    public String inspect() {
        return String.format("Closure[%s]",this.getClass().getSimpleName());
    }

}
