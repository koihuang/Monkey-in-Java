package com.koi.monkey.compiler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author whuang
 * @date 2019/11/25
 */
public class SymbolTable {
    public static final String GloableScope = "GLOBAL";
    public static final String LocalScope = "LOCAL";
    public Map<String,Symbol> store = new HashMap<>();
    public int numDefinitions;

    public SymbolTable outer;

    public Symbol define(String name) {
        Symbol symbol = new Symbol(name,numDefinitions);
        if (outer == null) {
            symbol.scope = GloableScope;
        } else {
            symbol.scope = LocalScope;
        }
        this.store.put(name,symbol);
        this.numDefinitions++;
        return symbol;
    }

    public Symbol resolve(String name) {
        if (this.store.containsKey(name)) {
            return this.store.get(name);
        }
        if (this.outer != null) {
            return this.outer.resolve(name);
        }
        return null;
    }

}
