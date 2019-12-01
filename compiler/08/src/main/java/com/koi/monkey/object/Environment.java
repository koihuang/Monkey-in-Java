package com.koi.monkey.object;

import java.util.HashMap;
import java.util.Map;

/**
 * @author whuang
 * @date 2019/11/6
 */
public class Environment {

    private Map<String,Obj> store;
    private Environment outer;
    public Environment() {
    }

    public Environment(Map<String, Obj> store) {
        this.store = store;
    }

    public Obj get(String name) {
        Obj obj = store.get(name);
        return obj == null && outer != null ? outer.store.get(name):obj;
    }

    public Obj set(String name,Obj val) {
        store.put(name,val);
        return val;
    }

    public static Environment newEnclosedEnvironment(Environment outer) {
        Environment env = new Environment(new HashMap<>());
        env.outer = outer;
        return env;
    }
}
