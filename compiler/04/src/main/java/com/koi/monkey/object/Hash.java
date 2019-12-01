package com.koi.monkey.object;

import java.util.Map;

/**
 * @author whuang
 * @date 2019/11/19
 */
public class Hash implements Obj{
    public Map<HashKey,HashPair> pairs;

    public Hash(Map<HashKey, HashPair> pairs) {
        this.pairs = pairs;
    }

    @Override
    public String type() {
        return ObjType.HASH_OBJ;
    }

    public Hash() {
    }

    @Override
    public String inspect() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        for (Map.Entry<HashKey, HashPair> pair : pairs.entrySet()) {
            sb.append(String.format("%s: %s%s",pair.getKey(),pair.getValue(),", "));
        }
        sb.substring(0,sb.length()-1);
        sb.append("}");
        return sb.toString();
    }
}
