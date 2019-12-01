package com.koi.monkey.vm;

import com.koi.monkey.ast.Program;
import com.koi.monkey.compiler.Compil;
import com.koi.monkey.object.Int;
import com.koi.monkey.object.Obj;
import com.koi.monkey.parser.Parser;
import junitparams.Parameters;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

/**
 * @author whuang
 * @date 2019/11/23
 */
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

    public void runVmTests(VmTestCase[] tests) {
        for (VmTestCase tt : tests) {
            Program program = parse(tt.input);
            Compil compil = new Compil();
            compil.compile(program);
            Vm vm = new Vm(compil.bytecode());
            vm.run();
            Obj stackElem = vm.stackTop();
            testExpectedObj(tt.expected,stackElem);
        }
    }

    @Test
    public void testIntegerArithmetic() {
        VmTestCase[] tests = new VmTestCase[]{
                new VmTestCase("1",1),
                new VmTestCase("2",2),
                new VmTestCase("1+2",3)
        };
        runVmTests(tests);

    }

    private void testExpectedObj(Object expected, Obj actual) {
        if (expected instanceof Integer) {
            testIntegerObject((Integer) expected,actual);
        }
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