package com.koi.monkey.object;

/**
 * @author whuang
 * @date 2019/12/1
 */
public class Closure implements Obj {

    @Override
    public String type() {
        return ObjType.CLOSURE_OBJ;
    }

    @Override
    public String inspect() {
        return String.format("Closure[%s]",this.getClass().getSimpleName());
    }

}
