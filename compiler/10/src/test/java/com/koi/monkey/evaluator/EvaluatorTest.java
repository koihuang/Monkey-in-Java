package com.koi.monkey.evaluator;

import static com.google.common.truth.Truth.assertThat;
import com.koi.monkey.ast.Program;
import com.koi.monkey.object.*;
import com.koi.monkey.parser.Parser;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import junitparams.converters.Nullable;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author whuang
 * @date 2019/11/5
 */
@RunWith(JUnitParamsRunner.class)
public class EvaluatorTest {

    @Test
    public void testArrayIndexExpressions() {
        class Struct {
            String input;
            Object expected;

            public Struct(String input, Object expected) {
                this.input = input;
                this.expected = expected;
            }
        }
        Struct[] tests = new Struct[]{
                new Struct("[1, 2, 3][0]",1),
                new Struct("[1, 2, 3][1]",2),
                new Struct("[1, 2, 3][2]",3),
                new Struct("let i = 0; [1][i];",1),
                new Struct("[1, 2, 3][1 + 1];",3),
                new Struct("let myArray = [1, 2, 3]; myArray[2];",3),
                new Struct("let myArray = [1, 2, 3]; myArray[0] + myArray[1] + myArray[2];",6),
                new Struct("let myArray = [1, 2, 3]; let i = myArray[0]; myArray[i]",2),
                new Struct("[1, 2, 3][3]",null),
                new Struct("[1, 2, 3][-1]",null)
        };

        for (Struct tt : tests) {
            Obj evaluated = testEval(tt.input);
            if (tt.expected instanceof Integer) {
                testIntegerObj(evaluated, (Integer) tt.expected);
            } else {
                testNullObj(evaluated);
            }
        }
    }
    
    @Test
    public void testArralyLiteral() {
        Obj evaluated = testEval("[1, 2 * 2, 3 + 3]");
        if (!(evaluated instanceof Arr)) {
            throw new RuntimeException(String.format("object is not Array. got=%s (%s)",evaluated.getClass().getSimpleName(),evaluated.inspect()));
        }
        if (((Arr) evaluated).elements.length != 3) {
            throw new RuntimeException(String.format("array has wrong num of elements. got=%d",((Arr) evaluated).elements.length));
        }
        testIntegerObj(((Arr) evaluated).elements[0],1);
        testIntegerObj(((Arr) evaluated).elements[1],4);
        testIntegerObj(((Arr) evaluated).elements[2],6);
    }

    @Test
    public  void testBuiltinFunction() {
        class Struct {
            String input;
            Object expected;

            public Struct(String input, Object expected) {
                this.input = input;
                this.expected = expected;
            }
        }
        Struct[] tests = new Struct[]{
                new Struct("len(\"\")", 0),
                new Struct("len(\"four\")", 4),
                new Struct("len(\"hello world\")", 11),
                new Struct("len(1)", "argument to `len` not supported, got INTEGER"),
                new Struct("len(\"one\", \"two\")", "wrong number of arguments. got=2, want=1")
        };
        for (Struct tt : tests) {
            Obj evaluated = testEval(tt.input);
            if (tt.expected instanceof String) {

                if (!(evaluated instanceof Err)) {
                    System.err.printf("object is not Error. got=%s (%s)",evaluated.getClass().getSimpleName(),evaluated.inspect());
                    continue;
                }
                if (!((Err) evaluated).message.equals(tt.expected)) {
                    System.err.printf("wrong error message. expected=%s, got=%s",tt.expected,((Err) evaluated).message);
                }

            } else {
                testIntegerObj(evaluated, ((Integer) tt.expected));
            }
        }
    }


    @Test
    public void testStringConcatenation() {
        String input = "\"Hello\" + \" \" + \"World!\"";
        Obj evaluated = testEval(input);
        if (!(evaluated instanceof Str)) {
            System.err.printf("object is not String. got=%s (%s)",evaluated.getClass().getSimpleName(),evaluated.inspect());
        }
        Str str = (Str) evaluated;
        if (!"Hello World!".equals(str.value)) {
            System.err.printf("String has wrong value. got=%s", str.value);
        }
    }

    @Test
    public void testStringLiteral() {
        Obj evaluated = testEval("\"Hello World!\"");
        if (!(evaluated instanceof Str)) {
            System.err.printf("object is not String. got=%s (%s)",evaluated.getClass().getSimpleName(),evaluated.inspect());
        }
        Str str = (Str) evaluated;
        if (!str.value.equals("Hello World!")) {
            System.err.printf("String has wrong value. got=%q", str.value);
        }
    }
    
    @Test
    public void testClosures() {
        String input = "let newAdder = fn(x) {  fn(y) { x + y };};let addTwo = newAdder(2);addTwo(2);";
        testIntegerObj(testEval(input),4);
    }
    
    @Test
    public void testFunctionApplication() {
        class Struct {
            String input;
            int expected;

            public Struct(String input, int expected) {
                this.input = input;
                this.expected = expected;
            }
        }
        Struct[] tests = new Struct[]{
                new Struct("let identity = fn(x) { x; }; identity(5);", 5),
                new Struct("let identity = fn(x) { return x; }; identity(5);", 5),
                new Struct("let double = fn(x) { x * 2; }; double(5);", 10),
                new Struct("let add = fn(x, y) { x + y; }; add(5, 5);", 10),
                new Struct("let add = fn(x, y) { x + y; }; add(5 + 5, add(5, 5));", 20),
                new Struct("fn(x) { x; }(5)", 5)
        };
        for (Struct tt : tests) {
            testIntegerObj(testEval(tt.input),tt.expected);
        }
    }

    @Test
    public void testFunctionObj() {
        String input = "fn(x) { x + 2; };";
        Obj evaluated = testEval(input);
        if (!(evaluated instanceof Function)) {
            System.err.printf("object is not Function. got=%s (%s)", evaluated.getClass().getSimpleName(), evaluated.inspect());
            return;
        }
        Function fn = (Function) evaluated;
        if (fn.parameters.size() != 1) {
            System.err.printf("function has wrong parameters. Parameters=%s", fn.parameters);
            return;
        }
        if (!(fn.parameters.get(0).equals("x"))) {
            System.err.printf("parameter is not 'x'. got=%q", fn.parameters.get(0).value);
            return;
        }

        if (!("(x + 2)".equals(fn.body.string()))) {
            System.err.printf("body is not %s. got=%s", "(x + 2)", fn.body.string());
            return;
        }

    }

    @Test
    public void testLetStatements() {
        class Struct {
            String input;
            int expected;

            public Struct(String input, int expected) {
                this.input = input;
                this.expected = expected;
            }
        }
        Struct[] tests = new Struct[]{
                new Struct("let a = 5; a;", 5),
                new Struct("let a = 5 * 5; a;", 25),
                new Struct("let a = 5; let b = a; b;", 5),
                new Struct("let a = 5; let b = a; let c = a + b + 5; c;", 15),
        };
        for (Struct tt : tests) {
            testIntegerObj(testEval(tt.input), tt.expected);
        }
    }

    @Test
    public void testErrorHandling() {
        class Struct {
            String input;
            String expectedMessage;

            public Struct(String input, String expectedMessage) {
                this.input = input;
                this.expectedMessage = expectedMessage;
            }
        }
        Struct[] tests = new Struct[]{
                new Struct("5 + true;", "type mismatch: INTEGER + BOOLEAN"),
                new Struct("5 + true; 5;", "type mismatch: INTEGER + BOOLEAN"),
                new Struct("-true", "unknown operator: -BOOLEAN"),
                new Struct("true + false;", "unknown operator: BOOLEAN + BOOLEAN"),
                new Struct("5; true + false; 5", "unknown operator: BOOLEAN + BOOLEAN"),
                new Struct("if (10 > 1) { true + false; }", "unknown operator: BOOLEAN + BOOLEAN"),
                new Struct("foobar", "identifier not found: foobar"),
                new Struct("\"Hello\" - \"World\"","unknown operator: STRING - STRING"),
                new Struct("{\"name\": \"Monkey\"}[fn(x) { x }];","unusable as hash key: FUNCTION")
        };
        for (Struct tt : tests) {
            Obj evaluated = testEval(tt.input);
            if (!(evaluated instanceof Err)) {
                System.err.printf("no error object returned,got=%s (%s)\n", evaluated.getClass().getSimpleName(), ((Err) evaluated).message);
                continue;
            }

            if (!(tt.expectedMessage.equals(((Err) evaluated).message))) {
                System.err.printf("wrong error message. expected=%s, got=%s\n", tt.expectedMessage, ((Err) evaluated).message);
            }
        }

    }

    @Test
    public void testReturnStatements() {
        class Struct {
            String input;
            int expected;

            public Struct(String input, int expected) {
                this.input = input;
                this.expected = expected;
            }
        }

        Struct[] tests = new Struct[]{
                new Struct("if (10 > 1) {  if (10 > 1) {    return 10;  }  return 1;}", 10),
                new Struct("return 10;", 10),
                new Struct("return 10; 9;", 10),
                new Struct("return 2 * 5; 9;", 10),
                new Struct("9; return 2 * 5; 9;", 10),
        };

        for (Struct tt : tests) {
            Obj evaluated = testEval(tt.input);
            testIntegerObj(evaluated, tt.expected);
        }

    }

    @Test
    public void testIfElseExpression() {
        class Struct {
            String input;
            Object expected;

            public Struct(String input, Object expected) {
                this.input = input;
                this.expected = expected;
            }
        }
        Struct[] tests = new Struct[]{
                new Struct("if (true) { 10 }", 10),
                new Struct("if (false) { 10 }", null),
                new Struct("if (1) { 10 }", 10),
                new Struct("if (1 < 2) { 10 }", 10),
                new Struct("if (1 > 2) { 10 }", null),
                new Struct("if (1 > 2) { 10 } else { 20 }", 20),
                new Struct("if (1 < 2) { 10 } else { 20 }", 10)
        };

        for (Struct tt : tests) {
            Obj evaluated = testEval(tt.input);
            if (tt.expected != null) {
                testIntegerObj(evaluated, (Integer) tt.expected);
            } else {
                testNullObj(evaluated);
            }
        }

    }

    private boolean testNullObj(Obj obj) {
        if (!(obj == Evaluator.NULL || obj == null)) {
            System.err.printf("obj is not NULL, got=%s(%s)\n", obj.getClass().getSimpleName(), obj.inspect());
            return false;
        }
        return true;
    }

    @Test
    public void testBangOperator() {
        class Struct {
            String input;
            boolean expected;

            public Struct(String input, boolean expected) {
                this.input = input;
                this.expected = expected;
            }
        }
        Struct[] tests = new Struct[]{
                new Struct("!true", false),
                new Struct("!false", true),
                new Struct("!5", false),
                new Struct("!!true", true),
                new Struct("!!false", false),
                new Struct("!!5", true),
        };

        for (Struct tt : tests) {
            Obj evaluated = testEval(tt.input);
            testBollObj(evaluated, tt.expected);
        }
    }

    @Test
    public void testEvalBoolExpression() {

        class Struct {
            String input;
            boolean expected;

            public Struct(String input, boolean expected) {
                this.input = input;
                this.expected = expected;
            }
        }
        Struct[] tests = new Struct[]{
                new Struct("true", true),
                new Struct("false", false),
                new Struct("1 < 2", true),
                new Struct("1 > 2", false),
                new Struct("1 < 1", false),
                new Struct("1 > 1", false),
                new Struct("1 == 1", true),
                new Struct("1 != 1", false),
                new Struct("1 == 2", false),
                new Struct("1 != 2", true),
                new Struct("true == true", true),
                new Struct("false == false", true),
                new Struct("true == false", false),
                new Struct("true != false", true),
                new Struct("false != true", true),
                new Struct("(1 < 2) == true", true),
                new Struct("(1 < 2) == false", false),
                new Struct("(1 > 2) == true", false),
                new Struct("(1 > 2) == false", true)
        };

        for (Struct tt : tests) {
            Obj evaluated = testEval(tt.input);
            testBollObj(evaluated, tt.expected);
        }
    }

    private boolean testBollObj(Obj obj, boolean expected) {
        if (!(obj instanceof Bool)) {
            System.err.println("obj is not Bool, got=" + obj.getClass());
            return false;
        }

        Bool result = (Bool) obj;
        if (result.value != expected) {
            System.err.println("obj has wrong value. got=" + result.value + ", want=" + expected);
            return false;
        }
        return true;
    }


    @Test
    public void testEvalIntegerExpression() {

        class Struct {
            String input;
            int expected;

            public Struct(String input, int expected) {
                this.input = input;
                this.expected = expected;
            }
        }

        Struct[] tests = new Struct[]{
                new Struct("5", 5),
                new Struct("10", 10),
                new Struct("-10", -10),
                new Struct("-5", -5),
                new Struct("5 + 5 + 5 + 5 - 10", 10),
                new Struct("2 * 2 * 2 * 2 * 2", 32),
                new Struct("-50 + 100 + -50", 0),
                new Struct("5 * 2 + 10", 20),
                new Struct("5 + 2 * 10", 25),
                new Struct("20 + 2 * -10", 0),
                new Struct("50 / 2 * 2 + 10", 60),
                new Struct("2 * (5 + 10)", 30),
                new Struct("3 * 3 * 3 + 10", 37),
                new Struct("3 * (3 * 3) + 10", 37),
                new Struct("(5 + 10 * 2 + 15 / 3) * 2 + -10", 50)
        };
        for (Struct tt : tests) {
            Obj evaluated = testEval(tt.input);
            testIntegerObj(evaluated, tt.expected);
        }
    }
    @Test
    public void testHashLiterals() {
        Obj evaluated = testEval("let two = \"two\";    {        \"one\": 10 - 9,        two: 1 + 1,        \"thr\" + \"ee\": 6 / 2,        4: 4,        true: 5,        false: 6    }");
        assertThat(evaluated).isInstanceOf(Hash.class);
        Hash result = (Hash) evaluated;
        Map<HashKey,Integer> expected = new HashMap<>();
        expected.put(new Str("one").hashKey(),1);
        expected.put(new Str("two").hashKey(),2);
        expected.put(new Str("three").hashKey(),3);
        expected.put(new Int(4).hashKey(),4);
        expected.put(Evaluator.TRUE.hashKey(),5);
        expected.put(Evaluator.FALSE.hashKey(),6);
        assertThat(result.pairs.size()).isEqualTo(expected.size());
        for (Map.Entry<HashKey, Integer> expect : expected.entrySet()) {
            assertThat(result.pairs.containsKey(expect.getKey()));
            testIntegerObj(result.pairs.get(expect.getKey()).value,expect.getValue());
        }


    }

    @Test
    @Parameters({
            "{\"foo\": 5}[\"foo\"],5",
            "let key = \"foo\"; {\"foo\": 5}[key],5",
            "{}[\"foo\"],null"
    })
    public void testHashIndexExpression(String input,@Nullable Integer expected) {
        Obj evaluated = testEval(input);
        if (expected == null) {
            testNullObj(evaluated);
        }else {
            testIntegerObj(evaluated,expected);
        }
    }

    private boolean testIntegerObj(Obj obj, int expected) {
        if (obj == null || !(obj instanceof Int)) {
            System.err.printf("obj is not Int, got=%s\n", (obj == null ? "null" : obj.getClass().getName()));
            return false;
        }

        Int result = (Int) obj;
        if (result.value != expected) {
            System.err.printf("obj has wrong value. got=%d,want=%d\n", result.value, expected);
            return false;
        }
        return true;
    }

    private Obj testEval(String input) {
        Parser p = new Parser(input);
        Program program = p.parseProgram();
        Evaluator evaluator = new Evaluator();
        return evaluator.eval(program, new Environment(new HashMap<>()));
    }
}