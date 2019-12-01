package com.koi.monkey.compiler;

import java.util.Objects;

/**
 * @author whuang
 * @date 2019/11/25
 */
public class Symbol {
    public String name;
    public String scope;
    public int index;

    public Symbol(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public Symbol(String name, String scope, int index) {
        this.name = name;
        this.scope = scope;
        this.index = index;
    }

    public Symbol() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Symbol symbol = (Symbol) o;
        return index == symbol.index &&
                Objects.equals(name, symbol.name) &&
                Objects.equals(scope, symbol.scope);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, scope, index);
    }
}
