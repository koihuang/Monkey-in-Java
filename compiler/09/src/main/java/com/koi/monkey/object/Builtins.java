package com.koi.monkey.object;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author whuang
 * @date 2019/12/1
 */
public class Builtins {
    public static final LinkedHashMap<String, Builtin> builtins = new LinkedHashMap<>();
    static {
        BuiltinFunction len = args -> {
            if (args.length != 1) {
                return newErr("wrong number of arguments. got=%d, want=1", args.length);
            }
            if (args[0] instanceof Str) {
                return new Int(((Str) args[0]).value.length());
            }

            if (args[0] instanceof Arr) {
                return new Int(((Arr) args[0]).elements.length);
            }
            return newErr("argument to `len` not supported, got %s", args[0].type());
        };
        BuiltinFunction first = args -> {
            if (args.length != 1) {
                return newErr("wrong number of arguments. got=%d, want=1", args.length);
            }

            if (!(args[0] instanceof Arr)) {
                return newErr("argument to `first` must be ARRAY, got %s", args[0].type());
            }

            Arr arr = (Arr) args[0];
            if (arr.elements.length > 0) {
                return arr.elements[0];
            }
            return null;
        };
        BuiltinFunction last = args -> {
            if (args.length != 1) {
                return newErr("wrong number of arguments. got=%d, want=1", args.length);
            }

            if (!(args[0] instanceof Arr)) {
                return newErr("argument to `first` must be ARRAY, got %s", args[0].type());
            }

            Arr arr = (Arr) args[0];
            if (arr.elements.length > 0) {
                return arr.elements[arr.elements.length - 1];
            }
            return null;
        };
        BuiltinFunction rest = args -> {
            if (args.length != 1) {
                return newErr("wrong number of arguments. got=%d, want=1", args.length);
            }

            if (!(args[0] instanceof Arr)) {
                return newErr("argument to `rest` must be ARRAY, got %s", args[0].type());
            }

            Arr arr = (Arr) args[0];
            int length = arr.elements.length;
            if (length > 0) {
                Obj[] newElements = new Obj[length - 1];
                System.arraycopy(arr.elements, 1, newElements, 0, length - 1);
                return new Arr(newElements);
            }
            return null;
        };
        BuiltinFunction push = args -> {
            if (args.length != 2) {
                return newErr("wrong number of arguments. got=%d, want=2", args.length);
            }

            if (!(args[0] instanceof Arr)) {
                return newErr("argument to `push` must be ARRAY, got %s", args[0].type());
            }

            Arr arr = (Arr) args[0];
            int length = arr.elements.length;
            Obj[] newElements = new Obj[length + 1];
            System.arraycopy(arr.elements, 0, newElements, 0, length);
            newElements[length] = args[1];
            return new Arr(newElements);
        };

        BuiltinFunction puts = args -> {
            for (Obj arg : args) {
                System.out.println(arg.inspect());
            }
            return null;
        };
        builtins.put("len", new Builtin(len));
        builtins.put("puts", new Builtin(puts));
        builtins.put("first", new Builtin(first));
        builtins.put("last", new Builtin(last));
        builtins.put("rest", new Builtin(rest));
        builtins.put("push", new Builtin(push));

    }

    private static Err newErr(String format, Object... a) {
        return new Err(String.format(format, a));
    }
}
