package com.koi.monkey.compiler;

import java.util.HashMap;
import java.util.Map;

/**
 * @author whuang
 * @date 2019/11/25
 */
public class SymbolTable {
    public static final String GloableScope = "GLOBAL";
    public Map<String,Symbol> store = new HashMap<>();
    public int numDefinitions;

    public Symbol define(String name) {
        Symbol symbol = new Symbol(name,GloableScope,numDefinitions);
        this.store.put(name,symbol);
        this.numDefinitions++;
        return symbol;
    }

    public Symbol resolve(String name) {
        return this.store.get(name);
    }
}
