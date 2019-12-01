package com.koi.monkey.compiler;

import com.koi.monkey.object.Obj;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author whuang
 * @date 2019/11/25
 */
public class SymbolTable {
    public static final String GloableScope = "GLOBAL";
    public static final String LocalScope = "LOCAL";
    public static final String BuiltinScope = "BUILTIN";
    public static final String FreeScope = "FREE";
    public Map<String,Symbol> store = new HashMap<>();
    public int numDefinitions;

    public SymbolTable outer;
    public List<Symbol> freeSymbols = new ArrayList<>();

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
            Symbol obj = this.outer.resolve(name);
            if (obj == null) {
                return obj;
            }
            if (GloableScope.equals(obj.scope) || BuiltinScope.equals(obj.scope)) {
                return obj;
            }
            Symbol free = defineFree(obj);
            return free;
        }
        return null;
    }

    public Symbol defineBuiltin(int index,String name) {
        Symbol symbol = new Symbol(name,BuiltinScope,index);
        this.store.put(name,symbol);
        return symbol;
    }

    public Symbol defineFree(Symbol origin) {
        this.freeSymbols.add(origin);
        Symbol symbol = new Symbol(origin.name,this.freeSymbols.size()-1);
        symbol.scope = FreeScope;
        this.store.put(origin.name,symbol);
        return symbol;
    }

}
