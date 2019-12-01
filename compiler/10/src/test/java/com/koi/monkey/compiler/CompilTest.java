package com.koi.monkey.compiler;

import static com.google.common.truth.Truth.assertThat;

import com.koi.monkey.ast.Program;
import com.koi.monkey.code.Code;
import com.koi.monkey.object.CompiledFunction;
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

    @Test
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
                new CompilerTestCase("[1,2,3]", Arrays.asList(1, 2, 3),
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
                new CompilerTestCase("[1+2,3-4,5*6]", Arrays.asList(1, 2, 3, 4, 5, 6),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpConstant, 0),
                                        Code.make(Code.OpConstant, 1),
                                        Code.make(Code.OpAdd),
                                        Code.make(Code.OpConstant, 2),
                                        Code.make(Code.OpConstant, 3),
                                        Code.make(Code.OpSub),
                                        Code.make(Code.OpConstant, 4),
                                        Code.make(Code.OpConstant, 5),
                                        Code.make(Code.OpMul),
                                        Code.make(Code.OpArray, 3),
                                        Code.make(Code.OpPop)
                                )
                        )
                )
        };
        runCompilerTests(tests);
    }

    @Test
    public void testHashLiteral() {
        CompilerTestCase[] tests = new CompilerTestCase[]{

                new CompilerTestCase("{}", Arrays.asList(),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpHash, 0),
                                        Code.make(Code.OpPop)
                                )
                        )
                ),
                new CompilerTestCase("{1:2,3:4,5:6}", Arrays.asList(1, 2, 3, 4, 5, 6),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpConstant, 0),
                                        Code.make(Code.OpConstant, 1),
                                        Code.make(Code.OpConstant, 2),
                                        Code.make(Code.OpConstant, 3),
                                        Code.make(Code.OpConstant, 4),
                                        Code.make(Code.OpConstant, 5),
                                        Code.make(Code.OpHash, 6),
                                        Code.make(Code.OpPop)
                                )
                        )
                ),
                new CompilerTestCase("{1:2+3,4:5*6}", Arrays.asList(1, 2, 3, 4, 5, 6),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpConstant, 0),
                                        Code.make(Code.OpConstant, 1),
                                        Code.make(Code.OpConstant, 2),
                                        Code.make(Code.OpAdd),
                                        Code.make(Code.OpConstant, 3),
                                        Code.make(Code.OpConstant, 4),
                                        Code.make(Code.OpConstant, 5),
                                        Code.make(Code.OpMul),
                                        Code.make(Code.OpHash, 4),
                                        Code.make(Code.OpPop)
                                )
                        )
                )
        };
        runCompilerTests(tests);
    }

    @Test
    public void testIndexExpressions() {
        CompilerTestCase[] tests = new CompilerTestCase[]{

                new CompilerTestCase("[1,2,3][1+1]", Arrays.asList(1, 2, 3, 1, 1),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpConstant, 0),
                                        Code.make(Code.OpConstant, 1),
                                        Code.make(Code.OpConstant, 2),
                                        Code.make(Code.OpArray, 3),
                                        Code.make(Code.OpConstant, 3),
                                        Code.make(Code.OpConstant, 4),
                                        Code.make(Code.OpAdd),
                                        Code.make(Code.OpIndex),
                                        Code.make(Code.OpPop)
                                )
                        )
                ),
                new CompilerTestCase("{1:2}[2-1]", Arrays.asList(1, 2, 2, 1),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpConstant, 0),
                                        Code.make(Code.OpConstant, 1),
                                        Code.make(Code.OpHash, 2),
                                        Code.make(Code.OpConstant, 2),
                                        Code.make(Code.OpConstant, 3),
                                        Code.make(Code.OpSub),
                                        Code.make(Code.OpIndex),
                                        Code.make(Code.OpPop)
                                )
                        )
                )
        };
        runCompilerTests(tests);
    }

    @Test
    public void testFunctions() {
        CompilerTestCase[] tests = new CompilerTestCase[]{

                new CompilerTestCase("fn() { return 5 + 10 }",
                        Arrays.asList(5, 10,
                                new ArrayList(Arrays.asList(
                                        Code.make(Code.OpConstant, 0),
                                        Code.make(Code.OpConstant, 1),
                                        Code.make(Code.OpAdd),
                                        Code.make(Code.OpReturnValue)
                                ))),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpClosure, 2,0),
                                        Code.make(Code.OpPop)
                                )
                        )
                ),
                new CompilerTestCase("fn() { 5 + 10 }",
                        Arrays.asList(5, 10,
                                new ArrayList(Arrays.asList(
                                        Code.make(Code.OpConstant, 0),
                                        Code.make(Code.OpConstant, 1),
                                        Code.make(Code.OpAdd),
                                        Code.make(Code.OpReturnValue)
                                ))),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpClosure, 2,0),
                                        Code.make(Code.OpPop)
                                )
                        )
                ),
                new CompilerTestCase("fn() { 1;2 }",
                        Arrays.asList(1, 2,
                                new ArrayList(Arrays.asList(
                                        Code.make(Code.OpConstant, 0),
                                        Code.make(Code.OpPop),
                                        Code.make(Code.OpConstant, 1),
                                        Code.make(Code.OpReturnValue)
                                ))),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpClosure, 2,0),
                                        Code.make(Code.OpPop)
                                )
                        )
                )
        };
        runCompilerTests(tests);
    }

    @Test
    public void testFunctionCalls() {
        CompilerTestCase[] tests = new CompilerTestCase[]{

                new CompilerTestCase("fn() { 24; }();",
                        Arrays.asList(24,
                                new ArrayList(Arrays.asList(
                                        Code.make(Code.OpConstant, 0),
                                        Code.make(Code.OpReturnValue)
                                ))),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpClosure, 1,0),
                                        Code.make(Code.OpCall,0),
                                        Code.make(Code.OpPop)
                                )
                        )
                ),
                new CompilerTestCase("let noArg = fn(){24}; noArg();",
                        Arrays.asList(24,
                                new ArrayList(Arrays.asList(
                                        Code.make(Code.OpConstant, 0),
                                        Code.make(Code.OpReturnValue)
                                ))),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpClosure, 1,0),
                                        Code.make(Code.OpSetGlobal, 0),
                                        Code.make(Code.OpGetGlobal,0),
                                        Code.make(Code.OpCall,0),
                                        Code.make(Code.OpPop)
                                )
                        )
                ),
                new CompilerTestCase("let oneArg = fn(a){a;}; oneArg(24);",
                        Arrays.asList(
                                new ArrayList(Arrays.asList(
                                        Code.make(Code.OpGetLocal, 0),
                                        Code.make(Code.OpReturnValue)
                                )),24),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpClosure, 0,0),
                                        Code.make(Code.OpSetGlobal, 0),
                                        Code.make(Code.OpGetGlobal,0),
                                        Code.make(Code.OpConstant, 1),
                                        Code.make(Code.OpCall,1),
                                        Code.make(Code.OpPop)
                                )
                        )
                ),
                new CompilerTestCase("let manyArg = fn(a,b,c){a;b;c;}; manyArg(24,25,26);",
                        Arrays.asList(
                                new ArrayList(Arrays.asList(
                                        Code.make(Code.OpGetLocal, 0),
                                        Code.make(Code.OpPop),
                                        Code.make(Code.OpGetLocal, 1),
                                        Code.make(Code.OpPop),
                                        Code.make(Code.OpGetLocal, 2),
                                        Code.make(Code.OpReturnValue)
                                )),24,25,26),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpClosure, 0,0),
                                        Code.make(Code.OpSetGlobal, 0),
                                        Code.make(Code.OpGetGlobal,0),
                                        Code.make(Code.OpConstant, 1),
                                        Code.make(Code.OpConstant, 2),
                                        Code.make(Code.OpConstant, 3),
                                        Code.make(Code.OpCall,3),
                                        Code.make(Code.OpPop)
                                )
                        )
                )
        };
        runCompilerTests(tests);
    }

    @Test
    public void testCompilerScopes() {
        Compil compiler = new Compil();
        assertThat(compiler.scopeIndex).isEqualTo(0);
        compiler.emit(Code.OpMul);
        compiler.enterScope();
        assertThat(compiler.scopeIndex).isEqualTo(1);
        compiler.emit(Code.OpSub);
        assertThat(compiler.scopes.get(compiler.scopeIndex).instructions.size()).isEqualTo(1);
        assertThat(compiler.scopes.get(compiler.scopeIndex).lastInstruction.opCode).isEqualTo(Code.OpSub);
        compiler.leaveScope();
        assertThat(compiler.scopeIndex).isEqualTo(0);
        compiler.emit(Code.OpAdd);
        assertThat(compiler.scopes.get(compiler.scopeIndex).instructions.size()).isEqualTo(2);
        assertThat(compiler.scopes.get(compiler.scopeIndex).lastInstruction.opCode).isEqualTo(Code.OpAdd);
        assertThat(compiler.scopes.get(compiler.scopeIndex).previousInstruction.opCode).isEqualTo(Code.OpMul);
    }


    @Test
    public void testFunctionsWithoutRetureValue() {
        CompilerTestCase[] tests = new CompilerTestCase[]{

                new CompilerTestCase("fn() { }",
                        Arrays.asList(
                                new ArrayList(Arrays.asList(
                                        Code.make(Code.OpReturn)
                                ))),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpClosure, 0,0),
                                        Code.make(Code.OpPop)
                                )
                        )
                )
        };
        runCompilerTests(tests);
    }

    @Test
    public void testLetStatementScopes() {
        CompilerTestCase[] tests = new CompilerTestCase[]{
                new CompilerTestCase("let num = 55; fn() { num }",
                        Arrays.asList(
                                55,
                                new ArrayList(Arrays.asList(
                                        Code.make(Code.OpGetGlobal,0),
                                        Code.make(Code.OpReturnValue)
                                ))),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpConstant, 0),
                                        Code.make(Code.OpSetGlobal,0),
                                        Code.make(Code.OpClosure, 1,0),
                                        Code.make(Code.OpPop)
                                )
                        )
                ),
                new CompilerTestCase("fn() { let num = 55; num}",
                        Arrays.asList(
                                55,
                                new ArrayList(Arrays.asList(
                                        Code.make(Code.OpConstant,0),
                                        Code.make(Code.OpSetLocal,0),
                                        Code.make(Code.OpGetLocal,0),
                                        Code.make(Code.OpReturnValue)
                                ))),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpClosure, 1,0),
                                        Code.make(Code.OpPop)
                                )
                        )
                ),
                new CompilerTestCase("fn() { let a = 55;let b = 77; a + b}",
                        Arrays.asList(
                                55,
                                77,
                                new ArrayList(Arrays.asList(
                                        Code.make(Code.OpConstant,0),
                                        Code.make(Code.OpSetLocal,0),
                                        Code.make(Code.OpConstant,1),
                                        Code.make(Code.OpSetLocal,1),
                                        Code.make(Code.OpGetLocal,0),
                                        Code.make(Code.OpGetLocal,1),
                                        Code.make(Code.OpAdd),
                                        Code.make(Code.OpReturnValue)
                                ))),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpClosure, 2,0),
                                        Code.make(Code.OpPop)
                                )
                        )
                ),
        };
        runCompilerTests(tests);
    }

    @Test
    public void testBuiltins() {
        CompilerTestCase[] tests = new CompilerTestCase[]{
                new CompilerTestCase("len([]); push([],1);",
                        Arrays.asList(
                                1),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpGetBuiltin, 0),
                                        Code.make(Code.OpArray, 0),
                                        Code.make(Code.OpCall, 1),
                                        Code.make(Code.OpPop ),
                                        Code.make(Code.OpGetBuiltin, 5),
                                        Code.make(Code.OpArray, 0),
                                        Code.make(Code.OpConstant, 0),
                                        Code.make(Code.OpCall, 2),
                                        Code.make(Code.OpPop)
                                )
                        )
                ),
                new CompilerTestCase("fn(){len([])}",
                        Arrays.asList(
                                new ArrayList(Arrays.asList(
                                        Code.make(Code.OpGetBuiltin,0),
                                        Code.make(Code.OpArray,0),
                                        Code.make(Code.OpCall,1),
                                        Code.make(Code.OpReturnValue)
                                ))),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpClosure, 0,0),
                                        Code.make(Code.OpPop )
                                )
                        )
                )
        };
        runCompilerTests(tests);
    }

    @Test
    public void testClosures() {
        CompilerTestCase[] tests = new CompilerTestCase[]{
                new CompilerTestCase("fn(a) { fn(b) { a + b }}",
                        Arrays.asList(
                                new ArrayList(Arrays.asList(
                                        Code.make(Code.OpGetFree,0),
                                        Code.make(Code.OpGetLocal,0),
                                        Code.make(Code.OpAdd),
                                        Code.make(Code.OpReturnValue)
                                )),
                                new ArrayList(Arrays.asList(
                                        Code.make(Code.OpGetLocal,0),
                                        Code.make(Code.OpClosure,0,1),
                                        Code.make(Code.OpReturnValue)
                                ))
                        ),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpClosure, 1,0),
                                        Code.make(Code.OpPop)
                                )
                        )
                ),
                new CompilerTestCase("fn(a) { fn(b) { fn(c) { a + b + c } } };",
                        Arrays.asList(
                                new ArrayList(Arrays.asList(
                                        Code.make(Code.OpGetFree,0),
                                        Code.make(Code.OpGetFree,1),
                                        Code.make(Code.OpAdd),
                                        Code.make(Code.OpGetLocal,0),
                                        Code.make(Code.OpAdd),
                                        Code.make(Code.OpReturnValue)
                                )),
                                new ArrayList(Arrays.asList(
                                        Code.make(Code.OpGetFree,0),
                                        Code.make(Code.OpGetLocal,0),
                                        Code.make(Code.OpClosure,0,2),
                                        Code.make(Code.OpReturnValue)
                                )),
                                new ArrayList(Arrays.asList(
                                        Code.make(Code.OpGetLocal,0),
                                        Code.make(Code.OpClosure,1,1),
                                        Code.make(Code.OpReturnValue)
                                ))
                        ),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpClosure, 2,0),
                                        Code.make(Code.OpPop)
                                )
                        )
                ),
                new CompilerTestCase(" let global = 55; fn() { let a = 66; fn() { let b = 77; fn() {  let c = 88; global + a + b + c; } } }",
                        Arrays.asList(
                                55,
                                66,
                                77,
                                88,
                                new ArrayList(Arrays.asList(
                                        Code.make(Code.OpConstant,3),
                                        Code.make(Code.OpSetLocal,0),
                                        Code.make(Code.OpGetGlobal,0),
                                        Code.make(Code.OpGetFree,0),
                                        Code.make(Code.OpAdd),
                                        Code.make(Code.OpGetFree,1),
                                        Code.make(Code.OpAdd),
                                        Code.make(Code.OpGetLocal,0),
                                        Code.make(Code.OpAdd),
                                        Code.make(Code.OpReturnValue)
                                )),
                                new ArrayList(Arrays.asList(
                                        Code.make(Code.OpConstant,2),
                                        Code.make(Code.OpSetLocal,0),
                                        Code.make(Code.OpGetFree,0),
                                        Code.make(Code.OpGetLocal,0),
                                        Code.make(Code.OpClosure,4,2),
                                        Code.make(Code.OpReturnValue)
                                )),
                                new ArrayList(Arrays.asList(
                                        Code.make(Code.OpConstant,1),
                                        Code.make(Code.OpSetLocal,0),
                                        Code.make(Code.OpGetLocal,0),
                                        Code.make(Code.OpClosure,5,1),
                                        Code.make(Code.OpReturnValue)
                                ))
                        ),
                        new ArrayList(
                                Arrays.asList(
                                        Code.make(Code.OpConstant,0),
                                        Code.make(Code.OpSetGlobal,0),
                                        Code.make(Code.OpClosure, 6,0),
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
            if (constant instanceof List) {
                assertThat(concatInstructions((List<byte[]>) constant)).isEqualTo(((CompiledFunction)actual.get(i)).instructions);
            }
        }
    }

    private void testStringObject(String expected, Obj actual) {
        assertThat(actual).isInstanceOf(Str.class);
        assertThat(((Str) actual).value).isEqualTo(expected);
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



















