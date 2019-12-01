package com.koi.monkey.vm;

import com.koi.monkey.ast.Program;
import com.koi.monkey.compiler.Compil;
import com.koi.monkey.object.Bool;
import com.koi.monkey.object.Int;
import com.koi.monkey.object.Obj;
import com.koi.monkey.parser.Parser;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

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
            testExpectedObj(expected,stackElem);

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
    public void testIntegerArithmetic(String input, Object expected) {
        runVmTests(input,expected);

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
    public void testBooleanExpression(String input,Boolean expected) {
        runVmTests(input,expected);
    }

    @Test
    @Parameters({
            "if (true) { 10 }, 10",
            "if (true) { 10 } else { 20 }, 10",
            "if (false) { 10 } else { 20 } , 20",
            "if (1) { 10 }, 10",
            "if (1 < 2) { 10 }, 10",
            "if (1 < 2) { 10 } else { 20 }, 10",
            "if (1 > 2) { 10 } else { 20 }, 20}",
            "if ((if (false) { 10 })) { 10 } else { 20 },20"
    })
    public void testConditionals(String input,Object expected) {
        runVmTests(input,expected);
    }
    private void testExpectedObj(Object expected, Obj actual) {
        if (expected instanceof Integer) {
            testIntegerObject((Integer) expected,actual);
            return;
        }

        if (expected instanceof Boolean) {
            testBooleanObj((Boolean)expected,actual);
            return;
        }

    }

    private void testBooleanObj(Boolean expected, Obj actual) {
        assertThat(actual).isInstanceOf(Bool.class);
        assertThat(expected).isEqualTo(((Bool)actual).value);
    }

    private void testIntegerObject(int constant, Obj obj) {
        assertThat(obj).isInstanceOf(Int.class);
        assertThat(((Int)obj).value).isEqualTo(constant);
    }

    private Program parse(String input) {
        Parser p = new Parser(input);
        return p.parseProgram();
    }



}