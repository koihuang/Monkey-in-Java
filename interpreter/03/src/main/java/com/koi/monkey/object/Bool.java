package com.koi.monkey.object;

/**
 * @author whuang
 * @date 2019/11/1
 */
public class Bool implements Obj {
    public Bool(boolean value) {
        this.value = value;
    }

    public Bool() {
    }

    public boolean value;

    @Override
    public String type() {
        return ObjType.BOOLEAN_OBJ;
    }

    @Override
    public String inspect() {
        return String.valueOf(value);
    }
}
