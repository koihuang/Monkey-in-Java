package com.koi.monkey.evaluator;

import com.koi.monkey.ast.Program;
import com.koi.monkey.object.*;
import com.koi.monkey.parser.Parser;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author whuang
 * @date 2019/11/5
 */
public class EvaluatorTest {




    
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
                new Struct("\"Hello\" - \"World\"","unknown operator: STRING - STRING")
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
        return Evaluator.eval(program, new Environment(new HashMap<>()));
    }
}