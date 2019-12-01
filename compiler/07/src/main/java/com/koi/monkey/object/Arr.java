package com.koi.monkey.object;

/**
 * @author whuang
 * @date 2019/11/14
 */
public class Arr implements Obj{
    public Arr() {
    }

    public Arr(Obj[] elements) {
        this.elements = elements;
    }

    public Obj[] elements;

    @Override
    public String type() {
        return ObjType.ARRAY_OBJ;
    }

    @Override
    public String inspect() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < elements.length; i++) {
            sb.append(elements[i].inspect());
            if(i < elements.length - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
