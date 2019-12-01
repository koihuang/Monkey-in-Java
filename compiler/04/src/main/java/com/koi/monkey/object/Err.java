package com.koi.monkey.object;

/**
 * @author whuang
 * @date 2019/11/6
 */
public class Err implements Obj{
    public String message;

    public Err() {
    }

    public Err(String message) {
        this.message = message;
    }

    @Override
    public String type() {
        return ObjType.ERROR_OBJ;
    }

    @Override
    public String inspect() {
        return "ERROR: " + message;
    }
}
