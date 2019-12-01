package com.koi.monkey.vm;

import com.koi.monkey.ast.Program;
import com.koi.monkey.code.Code;
import com.koi.monkey.compiler.Compil;
import com.koi.monkey.object.*;
import com.koi.monkey.parser.Parser;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.truth.Truth.assertThat;

/**
 * @author whuang
 * @date 2019/11/23
 */
@RunWith(JUnitParamsRunner.class)
public class VmTest {
    class VmTestCase {
        String input;
        Object expected;

        public VmTestCase(String input, Object expected) {
            this.input = input;
            this.expected = expected;
        }

        public VmTestCase() {
        }
    }


    public void runVmTests(String input, Object expected) {
        Program program = parse(input);
        Compil compil = new Compil();
        compil.compile(program);
        Vm vm = new Vm(compil.bytecode());
        vm.run();
        Obj stackElem = vm.lastPoppedStackElem();
        testExpectedObj(expected, stackElem);

    }

    @Test
    @Parameters({
            "1,1",
            "2,2",
            "1+2,3",
            "1 - 2, -1",
            "1 * 2, 2",
            "4 / 2, 2",
            "50 / 2 * 2 + 10 - 5, 55",
            "5 * (2 + 10), 60",
            "5 + 5 + 5 + 5 - 10, 10",
            "2 * 2 * 2 * 2 * 2, 32",
            "5 * 2 + 10, 20",
            "5 + 2 * 10, 25",
            "5 * (2 + 10), 60",
            "-5, -5",
            "-10, -10",
            "-50 + 100 + -50, 0",
            "(5 + 10 * 2 + 15 / 3) * 2 + -10, 50",


    })
    public void testIntegerArithmetic(String input, Integer expected) {
        runVmTests(input, expected);

    }

    @Test
    @Parameters(value = {
            "true,true",
            "false,false",
            "1 < 2, true",
            "1 > 2, false",
            "1 < 1, false",
            "1 > 1, false",
            "1 == 1, true",
            "1 != 1, false",
            "1 == 2, false",
            "1 != 2, true",
            "true == true, true",
            "false == false, true",
            "true == false, false",
            "true != false, true",
            "false != true, true",
            "(1 < 2) == true, true",
            "(1 < 2) == false, false",
            "(1 > 2) == true, false",
            "(1 > 2) == false, true",
            "!true,false",
            "!5,false",
            "!!true,true",
            "!!false,false",
            "!!5,true",
            "!(if(false){5;}),true"

    })
    public void testBooleanExpression(String input, Boolean expected) {
        runVmTests(input, expected);
    }

    @Test
    @Parameters({
            "if (true) { 10 }, 10",
            "if (true) { 10 } else { 20 }, 10",
            "if (false) { 10 } else { 20 } , 20",
            "if (1) { 10 }, 10",
            "if (1 < 2) { 10 }, 10",
            "if (1 < 2) { 10 } else { 20 }, 10",
            "if (1 > 2) { 10 } else { 20 }, 20",
            "if ((if (false) { 10 })) { 10 } else { 20 },20"
    })
    public void testConditionals(String input, Integer expected) {
        runVmTests(input, expected);
    }

    private void testExpectedObj(Object expected, Obj actual) {
        if (expected instanceof Integer) {
            testIntegerObject((Integer) expected, actual);
            return;
        }

        if (expected instanceof Boolean) {
            testBooleanObj((Boolean) expected, actual);
            return;
        }

        if (expected instanceof String) {
            testStringObject(expected, actual);
        }

        if (expected instanceof int[]) {
            assertThat(actual).isInstanceOf(Arr.class);
            assertThat(((Arr) actual).elements.length).isEqualTo(((int[]) expected).length);
            for (int i = 0; i < ((int[]) expected).length; i++) {
                int expectedElem = ((int[]) expected)[i];
                testIntegerObject(expectedElem, ((Arr) actual).elements[i]);
            }
        }

        if (expected instanceof Map) {
            assertThat(actual).isInstanceOf(Hash.class);
            Hash hash = (Hash) actual;
            assertThat(hash.pairs.size()).isEqualTo(((Map) expected).size());
            for (Object o : ((Map) expected).entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                assert hash.pairs.containsKey(entry.getKey());
                HashPair pair = hash.pairs.get(entry.getKey());
                testIntegerObject((Integer) entry.getValue(), pair.value);
            }
        }

    }

    private void testStringObject(Object expected, Obj actual) {
        assertThat(actual).isInstanceOf(Str.class);
        assertThat(((Str) actual).value).isEqualTo(expected);
    }

    @Test
    @Parameters({
            "let one = 1; one,1",
            "let one = 1; let two = 2; one + two, 3",
            "let one = 1; let two = one + one; one + two, 3"
    })
    public void testGlobalLetStatements(String input, Integer expected) {
        runVmTests(input, expected);
    }

    @Test
    @Parameters({
            "\"monkey\", monkey",
            "\"mon\" + \"key\", monkey",
            "\"mon\" + \"key\" + \"banana\", monkeybanana"
    })

    public void testStringExpressions(String input, String expected) {
        runVmTests(input, expected);
    }

    @Test
    public void testArrayLiterals() {
        VmTestCase[] tests = new VmTestCase[]{
                new VmTestCase("[]", new int[0]),
                new VmTestCase("[1,2,3]", new int[]{1, 2, 3}),
                new VmTestCase("[1 + 2, 3 * 4, 5 + 6]", new int[]{3, 12, 11})
        };
        for (VmTestCase tt : tests) {
            runVmTests(tt.input, tt.expected);
        }
    }

    @Test
    public void testHashLiterals() {
        Int one = new Int(1);
        Int two = new Int(2);
        Int six = new Int(6);
        Map<HashKey, Integer> m1 = new HashMap<>();
        m1.put(one.hashKey(), 2);
        m1.put(two.hashKey(), 3);

        Map<HashKey, Integer> m2 = new HashMap<>();
        m2.put(two.hashKey(), 4);
        m2.put(six.hashKey(), 16);


        VmTestCase[] tests = new VmTestCase[]{
                new VmTestCase("{}", new HashMap<HashKey, Integer>()),
                new VmTestCase("{1: 2, 2: 3}", m1),
                new VmTestCase("{1 + 1: 2 * 2, 3 + 3: 4 * 4}", m2)
        };
        for (VmTestCase tt : tests) {
            runVmTests(tt.input, tt.expected);
        }
    }

    @Test
    public void testIndexExpressions() {
        VmTestCase[] tests = new VmTestCase[]{
                new VmTestCase("[1, 2, 3][1]", 2),
                new VmTestCase("[1, 2, 3][0 + 2]", 3),
                new VmTestCase("[[1, 1, 1]][0][0]", 1),
                new VmTestCase("[][0]", Vm.NULL),
                new VmTestCase("[1, 2, 3][99]", Vm.NULL),
                new VmTestCase("[1][-1]", Vm.NULL),
                new VmTestCase("{1: 1, 2: 2}[1]", 1),
                new VmTestCase("{1: 1, 2: 2}[2]", 2),
                new VmTestCase("{1: 1}[0]", Vm.NULL),
                new VmTestCase("{}[0]", Vm.NULL),

        };
        for (VmTestCase tt : tests) {
            runVmTests(tt.input, tt.expected);
        }
    }

    @Test
    public void testCallingFunctionsWithoutArguments() {
        VmTestCase[] tests = new VmTestCase[]{
                new VmTestCase("let fivePlusTen = fn() { 5 + 10; };        fivePlusTen();", 15),
                new VmTestCase("let one = fn() { 1; };        let two = fn() { 2; };        one() + two();", 3),
                new VmTestCase("let a = fn() { 1 };        let b = fn() { a() + 1 };  let c = fn() { b() + 1 };        c();", 3),
        };
        for (VmTestCase tt : tests) {
            runVmTests(tt.input, tt.expected);
        }
    }

    @Test
    public void testFunctionsWithReturnStatement() {
        VmTestCase[] tests = new VmTestCase[]{
                new VmTestCase("let earlyExit = fn() { return 99; 100; };        earlyExit();", 99),
                new VmTestCase("let earlyExit = fn() { return 99; return 100; };        earlyExit();", 99),
        };
        for (VmTestCase tt : tests) {
            runVmTests(tt.input, tt.expected);
        }
    }

    @Test
    public void testFunctionWithoutReturnValue() {
        VmTestCase[] tests = new VmTestCase[]{
                new VmTestCase("let noReturn = fn() {};noReturn();", Vm.NULL),
                new VmTestCase("let noReturn = fn() { };        let noReturnTwo = fn() { noReturn(); };        noReturn();        noReturnTwo()", Vm.NULL),
        };
        for (VmTestCase tt : tests) {
            runVmTests(tt.input, tt.expected);
        }
    }

    @Test
    public void testFirstClassFunctions() {
        VmTestCase[] tests = new VmTestCase[]{
                new VmTestCase("let returnsOne = fn() { 1; }; let returnsOneReturner = fn() { returnsOne; }; returnsOneReturner()()", 1),
                new VmTestCase("let returnsOneReturner = fn() {let returnsOne = fn() { 1; };returnsOne;};returnsOneReturner()()", 1),
        };
        for (VmTestCase tt : tests) {
            runVmTests(tt.input, tt.expected);
        }
    }

    @Test
    @Parameters({
            "let one = fn(){let one = 1;one}; one();,1",
            "let oneAndTwo = fn() { let one = 1; let two = 2; one + two; };oneAndTwo();,3",
            "let oneAndTwo = fn() { let one = 1; let two = 2; one + two; };let threeAndFour = fn() { let three = 3; let four = 4; three + four; };oneAndTwo() + threeAndFour(),10",
            "let firstFoobar = fn() { let foobar = 50; foobar; };let secondFoobar = fn() { let foobar = 100; foobar; };firstFoobar() + secondFoobar(),150",
            "let globalSeed = 50;let minusOne = fn() {let num = 1; globalSeed - num;}; let minusTwo = fn() {let num = 2;globalSeed - num; }; minusOne() + minusTwo();,97",
    })
    public void testCallingFunctionsWithBindings(String input,Integer expected) {
        runVmTests(input,expected);
    }

    @Test
    public void testCallingFunctionsWithArgumentsAndBindings() {
        Map<String,Integer> tests = new HashMap<>();
        tests.put("let identity = fn(a) {a;}; identity(4);",4);
        tests.put("let sum = fn(a, b) { let c = a + b;c;};sum(1, 2);",3);
        tests.put("let sum = fn(a, b) { let c = a + b; c; }; sum(1, 2) + sum(3, 4)",10);
        tests.put("let sum = fn(a, b) { let c = a + b;c;}; let outer = fn() { sum(1, 2) + sum(3, 4); };outer();",10);
        tests.put("let globalNum = 10;let sum = fn(a, b) {let c = a + b;c + globalNum;};let outer = fn() {sum(1, 2) + sum(3, 4) + globalNum;}; outer() + globalNum",50);
        for (Map.Entry<String, Integer> tt : tests.entrySet()) {
            runVmTests(tt.getKey(),tt.getValue());
        }
    }

    @Test
    public void testCallingFunctionsWithWrongArguments() {
        Map<String,String> tests = new HashMap<>();
        tests.put("fn() { 1; }(1);","wrong number of arguments: want=0, got=1");
        tests.put("fn(a) { a; }();","wrong number of arguments: want=1, got=0");
        tests.put("fn(a, b) { a + b; }(1);","wrong number of arguments: want=2, got=1");
        for (Map.Entry<String, String> tt : tests.entrySet()) {
            Program program = parse(tt.getKey());
            Compil comp = new Compil();
            comp.compile(program);
            Vm vm = new Vm(comp.bytecode());
            try {
                vm.run();
            } catch (Exception e) {
                assertThat(tt.getValue()).isEqualTo(e.getMessage());
            }
        }

    }

    @Test
    public void testBuiltinFunctions() {
        VmTestCase[] tests = new VmTestCase[]{
                new VmTestCase("len(\"\")", 0),
                new VmTestCase("len(\"four\")", 4),
        };
        for (VmTestCase tt : tests) {
            runVmTests(tt.input, tt.expected);
        }
    }

    @Test
    public void testClosures() {
        Map<String,Object> tests = new HashMap<>();
        tests.put("let newClosure = fn(a) { fn() { a; };}; let closure = newClosure(99); closure();",99);
        tests.put("let newAdderOuter = fn(a, b) {let c = a + b; fn(d) { let e = d + c;fn(f) { e + f; };};};let newAdderInner = newAdderOuter(1, 2);let adder = newAdderInner(3);adder(8);",14);
        tests.put("let a = 1;let newAdderOuter = fn(b) {fn(c) {fn(d) { a + b + c + d };};};let newAdderInner = newAdderOuter(2);let adder = newAdderInner(3);adder(8);",14);
        tests.put("let newClosure = fn(a, b) {let one = fn() { a; };let two = fn() { b; }; fn() { one() + two(); }; }; let closure = newClosure(9, 90);closure();",99);
        for (Map.Entry<String, Object> tt : tests.entrySet()) {
            runVmTests(tt.getKey(),tt.getValue());
        }
    }

    private void testBooleanObj(Boolean expected, Obj actual) {
        assertThat(actual).isInstanceOf(Bool.class);
        assertThat(expected).isEqualTo(((Bool) actual).value);
    }

    private void testIntegerObject(int constant, Obj obj) {
        assertThat(obj).isInstanceOf(Int.class);
        assertThat(((Int) obj).value).isEqualTo(constant);
    }

    private Program parse(String input) {
        Parser p = new Parser(input);
        return p.parseProgram();
    }


}