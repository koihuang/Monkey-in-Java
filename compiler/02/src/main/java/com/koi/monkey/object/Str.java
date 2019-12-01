package com.koi.monkey.object;

/**
 * @author whuang
 * @date 2019/11/7
 */
public class Str implements Obj,Hashable{
    public String value;

    public Str() {
    }

    public Str(String value) {
        this.value = value;
    }

    @Override
    public String type() {
        return ObjType.STRING_OBJ;
    }

    @Override
    public String inspect() {
        return value;
    }

    @Override
    public HashKey hashKey() {
        return new HashKey(type(),value.hashCode());
    }
}
