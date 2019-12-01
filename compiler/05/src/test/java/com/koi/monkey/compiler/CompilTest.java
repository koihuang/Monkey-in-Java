package com.koi.monkey.compiler;

import static com.google.common.truth.Truth.assertThat;

import com.koi.monkey.ast.Program;
import com.koi.monkey.code.Code;
import com.koi.monkey.object.Int;
import com.koi.monkey.object.Obj;
import com.koi.monkey.object.Str;
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
                                        Code.make(Code.OpAdd),
                                        Code.make(Code.OpPop)
                                )
                        )
                ),
                new CompilerTestCase(
                        "1; 2",
                        Arrays.asList(1, 2),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpConstant, 0),
                                        Code.make(Code.OpPop),
                                        Code.make(Code.OpConstant, 1),
                                        Code.make(Code.OpPop)
                                )
                        )
                ),
                new CompilerTestCase(
                        "1-2",
                        Arrays.asList(1, 2),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpConstant, 0),
                                        Code.make(Code.OpConstant, 1),
                                        Code.make(Code.OpSub),
                                        Code.make(Code.OpPop)
                                )
                        )
                ),
                new CompilerTestCase(
                        "1*2",
                        Arrays.asList(1, 2),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpConstant, 0),
                                        Code.make(Code.OpConstant, 1),
                                        Code.make(Code.OpMul),
                                        Code.make(Code.OpPop)
                                )
                        )
                ),
                new CompilerTestCase(
                        "2/1",
                        Arrays.asList(2, 1),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpConstant, 0),
                                        Code.make(Code.OpConstant, 1),
                                        Code.make(Code.OpDiv),
                                        Code.make(Code.OpPop)
                                )
                        )
                ),
                new CompilerTestCase(
                        "-1",
                        Arrays.asList(1),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpConstant, 0),
                                        Code.make(Code.OpMinus),
                                        Code.make(Code.OpPop)
                                )
                        )
                )
        };

        runCompilerTests(tests);
    }

    @Test
    public void testBooleanExpressions() {
        CompilerTestCase[] tests = new CompilerTestCase[]{
                new CompilerTestCase(
                        "true",
                        Arrays.asList(),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpTrue),
                                        Code.make(Code.OpPop)
                                )
                        )
                ),
                new CompilerTestCase(
                        "false",
                        Arrays.asList(),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpFalse),
                                        Code.make(Code.OpPop)
                                )
                        )
                ),
                new CompilerTestCase(
                        "1>2",
                        Arrays.asList(1, 2),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpConstant, 0),
                                        Code.make(Code.OpConstant, 1),
                                        Code.make(Code.OpGreaterThan),
                                        Code.make(Code.OpPop)
                                )
                        )
                ),
                new CompilerTestCase(
                        "1<2",
                        Arrays.asList(2, 1),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpConstant, 0),
                                        Code.make(Code.OpConstant, 1),
                                        Code.make(Code.OpGreaterThan),
                                        Code.make(Code.OpPop)
                                )
                        )
                ),
                new CompilerTestCase(
                        "1==2",
                        Arrays.asList(1, 2),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpConstant, 0),
                                        Code.make(Code.OpConstant, 1),
                                        Code.make(Code.OpEqual),
                                        Code.make(Code.OpPop)
                                )
                        )
                ),
                new CompilerTestCase(
                        "1!=2",
                        Arrays.asList(1, 2),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpConstant, 0),
                                        Code.make(Code.OpConstant, 1),
                                        Code.make(Code.OpNotEqual),
                                        Code.make(Code.OpPop)
                                )
                        )
                ),
                new CompilerTestCase(
                        "true == false",
                        Arrays.asList(),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpTrue),
                                        Code.make(Code.OpFalse),
                                        Code.make(Code.OpEqual),
                                        Code.make(Code.OpPop)
                                )
                        )
                ),
                new CompilerTestCase(
                        "true != false",
                        Arrays.asList(),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpTrue),
                                        Code.make(Code.OpFalse),
                                        Code.make(Code.OpNotEqual),
                                        Code.make(Code.OpPop)
                                )
                        )
                ),
                new CompilerTestCase(
                        "!true",
                        Arrays.asList(),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpTrue),
                                        Code.make(Code.OpBang),
                                        Code.make(Code.OpPop)
                                )
                        )
                ),

        };
        runCompilerTests(tests);
    }

    @Test
    public void testConditionals() {
        CompilerTestCase[] tests = new CompilerTestCase[]{
                new CompilerTestCase("if (true) { 10 }; 3333",
                        Arrays.asList(10, 3333),
                        new ArrayList(
                                Arrays.asList(
                                        //0000
                                        Code.make(Code.OpTrue),
                                        //0001
                                        Code.make(Code.OpJumpNotTruthy, 7),
                                        //0003
                                        Code.make(Code.OpConstant, 0),
                                        //0005
                                        Code.make(Code.OpJump, 8),
                                        //0007
                                        Code.make(Code.OpNull),
                                        //0008
                                        Code.make(Code.OpPop),
                                        //0009
                                        Code.make(Code.OpConstant, 1),
                                        //0011
                                        Code.make(Code.OpPop)
                                )
                        )
                ),
                new CompilerTestCase("if (true) { 10 } else { 20}; 3333",
                        Arrays.asList(10, 20, 3333),
                        new ArrayList(
                                Arrays.asList(
                                        //0000
                                        Code.make(Code.OpTrue),
                                        //0001
                                        Code.make(Code.OpJumpNotTruthy, 7),
                                        //0003
                                        Code.make(Code.OpConstant, 0),
                                        //0005
                                        Code.make(Code.OpJump, 9),
                                        //0007
                                        Code.make(Code.OpConstant, 1),
                                        //0009
                                        Code.make(Code.OpPop),
                                        //0010
                                        Code.make(Code.OpConstant, 2),
                                        //0012
                                        Code.make(Code.OpPop)
                                )
                        )
                ),
        };
        runCompilerTests(tests);
    }

    @Test
    public void testGlobalLetStatements() {
        CompilerTestCase[] tests = new CompilerTestCase[]{
                new CompilerTestCase("let one = 1; let two = 2;", Arrays.asList(1, 2),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpConstant, 0),
                                        Code.make(Code.OpSetGlobal, 0),
                                        Code.make(Code.OpConstant, 1),
                                        Code.make(Code.OpSetGlobal, 1)
                                )
                        )
                ),
                new CompilerTestCase("let one = 1; one;", Arrays.asList(1),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpConstant, 0),
                                        Code.make(Code.OpSetGlobal, 0),
                                        Code.make(Code.OpGetGlobal, 0),
                                        Code.make(Code.OpPop)
                                )
                        )
                ),
                new CompilerTestCase("let one = 1; let two = one; two;", Arrays.asList(1),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpConstant, 0),
                                        Code.make(Code.OpSetGlobal, 0),
                                        Code.make(Code.OpGetGlobal, 0),
                                        Code.make(Code.OpSetGlobal, 1),
                                        Code.make(Code.OpGetGlobal, 1),
                                        Code.make(Code.OpPop)
                                )
                        )
                ),
        };
        runCompilerTests(tests);
    }

    @Test
    public void testStringExpressions() {
        CompilerTestCase[] tests = new CompilerTestCase[]{

                new CompilerTestCase("\"monkey\"", Arrays.asList("monkey"),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpConstant, 0),
                                        Code.make(Code.OpPop)
                                )
                        )
                ),
                new CompilerTestCase("\"mon\" + \"key\"", Arrays.asList("mon", "key"),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpConstant, 0),
                                        Code.make(Code.OpConstant, 1),
                                        Code.make(Code.OpAdd),
                                        Code.make(Code.OpPop)
                                )
                        )
                )
        };
        runCompilerTests(tests);
    }

    public void testArrayLiterals() {
        CompilerTestCase[] tests = new CompilerTestCase[]{

                new CompilerTestCase("[]", Arrays.asList(),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpArray, 0),
                                        Code.make(Code.OpPop)
                                )
                        )
                ),
                new CompilerTestCase("[1,2,3]", Arrays.asList(1,2,3),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpConstant, 0),
                                        Code.make(Code.OpConstant, 1),
                                        Code.make(Code.OpConstant, 2),
                                        Code.make(Code.OpArray, 3),
                                        Code.make(Code.OpPop)
                                )
                        )
                ),
                new CompilerTestCase("[1+2,3-4,5*6]", Arrays.asList(1,2,3,4,5,6),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpConstant, 0),
                                        Code.make(Code.OpConstant, 1),
                                        Code.make(Code.OpAdd ),
                                        Code.make(Code.OpConstant, 2),
                                        Code.make(Code.OpConstant, 3),
                                        Code.make(Code.OpSub ),
                                        Code.make(Code.OpConstant, 4),
                                        Code.make(Code.OpConstant, 5),
                                        Code.make(Code.OpMul ),
                                        Code.make(Code.OpArray, 3),
                                        Code.make(Code.OpPop)
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

            if (constant instanceof String) {
                testStringObject((String) constant, actual.get(i));
            }
        }
    }

    private void testStringObject(String expected, Obj actual) {
        assertThat(actual).isInstanceOf(Str.class);
        assertThat(((Str)actual).value).isEqualTo(expected);
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



















