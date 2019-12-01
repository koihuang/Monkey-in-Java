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
        expected.put("c",new Symbol("c",SymbolTable.LocalScope,0));
        expected.put("d",new Symbol("d",SymbolTable.LocalScope,1));
        expected.put("e",new Symbol("e",SymbolTable.LocalScope,0));
        expected.put("f",new Symbol("f",SymbolTable.LocalScope,1));
        SymbolTable global = new SymbolTable();
        assertThat(global.define("a")).isEqualTo(expected.get("a"));
        assertThat(global.define("b")).isEqualTo(expected.get("b"));
        SymbolTable firstLocal = Compil.newEnclosedSymbolTable(global);
        assertThat(firstLocal.define("c")).isEqualTo(expected.get("c"));
        assertThat(firstLocal.define("d")).isEqualTo(expected.get("d"));

        SymbolTable secondLocal = Compil.newEnclosedSymbolTable(firstLocal);
        assertThat(secondLocal.define("e")).isEqualTo(expected.get("e"));
        assertThat(secondLocal.define("f")).isEqualTo(expected.get("f"));
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

    @Test
    public void testResolvLocal() {
        SymbolTable global = new SymbolTable();
        global.define("a");
        global.define("b");

        SymbolTable local = Compil.newEnclosedSymbolTable(global);
        local.define("c");
        local.define("d");

        Symbol[] expected = new Symbol[]{
                new Symbol("a",SymbolTable.GloableScope,0),
                new Symbol("b",SymbolTable.GloableScope,1),
                new Symbol("c",SymbolTable.LocalScope,0),
                new Symbol("d",SymbolTable.LocalScope,1),
        };
        for (Symbol sym : expected) {
            Symbol result = local.resolve(sym.name);
            assertThat(result).isEqualTo(sym);
        }
    }

    @Test
    public void testResolveNestedLocal() {
        SymbolTable global = new SymbolTable();
        global.define("a");
        global.define("b");

        SymbolTable firstLocal = Compil.newEnclosedSymbolTable(global);
        firstLocal.define("c");
        firstLocal.define("d");

        SymbolTable secondLocal = Compil.newEnclosedSymbolTable(firstLocal);
        secondLocal.define("e");
        secondLocal.define("f");

        Map<SymbolTable,Symbol[]> tests = new HashMap<>();
        Symbol[] firstExpectedSymbols = new Symbol[]{
                new Symbol("a",SymbolTable.GloableScope,0),
                new Symbol("b",SymbolTable.GloableScope,1),
                new Symbol("c",SymbolTable.LocalScope,0),
                new Symbol("d",SymbolTable.LocalScope,1),
        };
        tests.put(firstLocal,firstExpectedSymbols);
        Symbol[] secondExpectedSymbols = new Symbol[]{
                new Symbol("a",SymbolTable.GloableScope,0),
                new Symbol("b",SymbolTable.GloableScope,1),
                new Symbol("e",SymbolTable.LocalScope,0),
                new Symbol("f",SymbolTable.LocalScope,1),
        };

        tests.put(secondLocal,secondExpectedSymbols);
        for (Map.Entry<SymbolTable, Symbol[]> tt : tests.entrySet()) {
            for (Symbol sym : tt.getValue()) {
                Symbol result = tt.getKey().resolve(sym.name);
                assertThat(result).isEqualTo(sym);
            }
        }
    }

    @Test
    public void testDefineResolveBuiltins() {
        SymbolTable global = new SymbolTable();
        SymbolTable firstLocal = Compil.newEnclosedSymbolTable(global);
        SymbolTable secondLocal = Compil.newEnclosedSymbolTable(firstLocal);
        Symbol[] expected = new Symbol[]{
                new Symbol("a",SymbolTable.BuiltinScope,0),
                new Symbol("c",SymbolTable.BuiltinScope,1),
                new Symbol("e",SymbolTable.BuiltinScope,2),
                new Symbol("f",SymbolTable.BuiltinScope,3),
        };
        for (int i = 0; i < expected.length; i++) {
            global.defineBuiltin(i,expected[i].name);
        }

        for (SymbolTable table : new SymbolTable[]{global, firstLocal, secondLocal}) {
            for (Symbol sym : expected) {
                Symbol result = table.resolve(sym.name);
                assertThat(result).isEqualTo(sym);
            }
        }
    }


}