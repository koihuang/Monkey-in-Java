package com.koi.monkey.object;

/**
 * @author whuang
 * @date 2019/11/1
 */
public class Bool implements Obj,Hashable {
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

    @Override
    public HashKey hashKey() {
        long value ;
        if (this.value == true) {
            value = 1;
        } else {
            value = 0;
        }
        return new HashKey(this.type(),value);
    }
}
