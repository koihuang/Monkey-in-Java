package com.koi.monkey.object;

import java.util.Objects;

/**
 * @author whuang
 * @date 2019/11/19
 */
public class HashKey {
    public String type;
    public long value;

    public HashKey() {
    }

    public HashKey(String type, long value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HashKey hashKey = (HashKey) o;
        return value == hashKey.value &&
                Objects.equals(type, hashKey.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }
}
