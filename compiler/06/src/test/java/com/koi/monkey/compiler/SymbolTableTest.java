package com.koi.monkey.compiler;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.*;

/**
 * @author whuang
 * @date 2019/11/25
 */
public class SymbolTableTest {


    @Test
    public void testDefine() {
        Map<String,Symbol> expected = new HashMap<>();
        expected.put("a",new Symbol("a",SymbolTable.GloableScope,0));
        expected.put("b",new Symbol("b",SymbolTable.GloableScope,1));
        SymbolTable global = new SymbolTable();
        assertThat(global.define("a")).isEqualTo(expected.get("a"));
        assertThat(global.define("b")).isEqualTo(expected.get("b"));
    }

    @Test
    public void testResolveGlobal() {
        Map<String,Symbol> expected = new HashMap<>();
        expected.put("a",new Symbol("a",SymbolTable.GloableScope,0));
        expected.put("b",new Symbol("b",SymbolTable.GloableScope,1));
        SymbolTable global = new SymbolTable();
        global.define("a");
        global.define("b");
        for (Map.Entry<String, Symbol> sym : expected.entrySet()) {
            assertThat(global.resolve(sym.getValue().name)).isEqualTo(sym.getValue());
        }
    }
}