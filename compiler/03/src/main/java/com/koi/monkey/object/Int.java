package com.koi.monkey.object;

/**
 * @author whuang
 * @date 2019/11/1
 */
public class Int implements Obj,Hashable {

    public int value;

    public Int() {
    }

    public Int(int value) {
        this.value = value;
    }

    @Override
    public String type() {
        return ObjType.INTEGER_OBJ;
    }

    @Override
    public String inspect() {
        return String.valueOf(this.value);
    }

    @Override
    public HashKey hashKey() {
        return new HashKey(type(),value);
    }
}
