package com.koi.monkey.compiler;

import static com.google.common.truth.Truth.assertThat;

import com.koi.monkey.ast.Program;
import com.koi.monkey.code.Code;
import com.koi.monkey.object.Int;
import com.koi.monkey.object.Obj;
import com.koi.monkey.parser.Parser;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author whuang
 * @date 2019/11/23
 */
public class CompilTest {
    class CompilerTestCase {
        String input;
        List<Object> expectedConstants;
        List<byte[]> expectedInstructions;

        public CompilerTestCase(String input, List<Object> expectedConstants, List<byte[]> expectedInstructions) {
            this.input = input;
            this.expectedConstants = expectedConstants;
            this.expectedInstructions = expectedInstructions;
        }
    }

    @Test
    public void testIntegerArithmetic() {
        CompilerTestCase[] tests = new CompilerTestCase[]{
                new CompilerTestCase(
                        "1 + 2",
                        Arrays.asList(1, 2),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpConstant, (byte) 0),
                                        Code.make(Code.OpConstant, (byte) 1),
                                        Code.make(Code.OpAdd)
                                )
                        )
                )
        };

        runCompilerTests(tests);
    }


    private void runCompilerTests(CompilerTestCase[] tests) {
        for (CompilerTestCase tt : tests) {
            Program program = parse(tt.input);
            Compil compil = new Compil();
            compil.compile(program);
            Bytecode bytecode = compil.bytecode();
            assertThat(concatInstructions(tt.expectedInstructions)).isEqualTo(bytecode.instructions);
            testConstants(tt.expectedConstants, bytecode.constants);
        }
    }

    private void testConstants(List<Object> expected, List<Obj> actual) {
        assertThat(expected.size()).isEqualTo(actual.size());
        for (int i = 0; i < expected.size(); i++) {
            Object constant = expected.get(i);
            if (constant instanceof Integer) {
                testIntegerObject((Integer) constant, actual.get(i));
            }
        }
    }

    private void testIntegerObject(int constant, Obj obj) {
        assertThat(obj).isInstanceOf(Int.class);
        assertThat(((Int) obj).value).isEqualTo(constant);
    }

    private Program parse(String input) {
        Parser p = new Parser(input);
        return p.parseProgram();
    }

    public static void main(String[] args) {
        List<byte[]> l1 = Arrays.asList(new byte[]{0, 0}, new byte[]{0, 1});
        List<byte[]> l2 = Arrays.asList(new byte[]{0, 0}, new byte[]{0, 1});
    }

    private List<Byte> concatInstructions(List<byte[]> s) {
        List<Byte> out = new ArrayList<>();
        for (byte[] in : s) {
            for (byte i : in) {
                out.add(i);
            }
        }
        return out;
    }
}



















