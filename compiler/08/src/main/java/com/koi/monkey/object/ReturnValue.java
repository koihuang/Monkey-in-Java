package com.koi.monkey.object;

/**
 * @author whuang
 * @date 2019/11/6
 */
public class ReturnValue implements Obj {
    public Obj value;

    public ReturnValue() {
    }

    public ReturnValue(Obj value) {
        this.value = value;
    }

    @Override
    public String type() {
        return ObjType.RETURN_VALUE_OBJ;
    }

    @Override
    public String inspect() {
        return value.inspect();
    }
}
