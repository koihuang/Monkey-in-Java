package com.koi.monkey.object;

/**
 * @author whuang
 * @date 2019/11/7
 */
public class Builtin implements Obj {
    public BuiltinFunction fn;

    public Builtin() {
    }

    public Builtin(BuiltinFunction fn) {
        this.fn = fn;
    }

    public String type() {
        return ObjType.BUILTIN_OBJ;
    }

    public String inspect() {
        return "builtin function";
    }
}
