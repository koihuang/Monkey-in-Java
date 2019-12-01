package com.koi.monkey.object;

/**
 * @author whuang
 * @date 2019/11/1
 */
public class Nil implements Obj {

    @Override
    public String type() {
        return ObjType.NULL_OBJ;
    }

    @Override
    public String inspect() {
        return "null";
    }
}
