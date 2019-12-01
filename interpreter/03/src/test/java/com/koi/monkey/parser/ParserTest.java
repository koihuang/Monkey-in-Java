package com.koi.monkey.parser;

import com.koi.monkey.ast.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


/**
 * @author whuang
 * @date 2019/10/18
 */
public class ParserTest {

    @Test
    public void testStringLiteralExpression() {
        String input = "\"hello world\";";
        Parser p = new Parser(input);
        Program program = p.parseProgram();
        checkParserErrors(p);
        ExpressionStatement stmt = null;
        try {
            stmt = (ExpressionStatement) program.statements.get(0);
        } catch (Exception e) {
            System.err.printf("exp not *ast.StringLiteral. got=%s",program.statements.get(0).getClass().getSimpleName());
            return;
        }

        StringLiteral literal = null;
        try {
            literal = (StringLiteral) stmt.expression;
        } catch (Exception e) {
            System.err.printf("exp not *ast.StringLiteral. got=%s",stmt.expression.getClass().getSimpleName());
            return;
        }

        if (!"hello world".equals(literal.value)) {
            System.err.printf("literal.Value not %s. got=%s", "hello world",literal.value);
        }
    }

    @Test
    public void testLetStatements() {

        // first version
        /*String input = "let x = 5;let y = 10;let  838383;";
        Parser p = new Parser(input);
        Program program = p.parseProgram();
        checkParserErrors(p);
        if (program == null) {
            throw new RuntimeException("parseProgram() return null");
        }

        if (program.statements.size() != 3) {
            throw new RuntimeException("program.statements does not contain 3 statements, got=" + program.statements.size());
        }

        String[][] tests = {
                {"x"},
                {"y"},
                {"foobar"}
        };

        for (int i = 0; i < tests.length; i++) {
            String[] tt = tests[i];
            Statement stmt = program.statements.get(i);
            if (!checkLetStatement(stmt,tt[0])) {
                return;
            }

        }*/

        // second version
        class LetStatementTest{
            String input;
            String expectedIdentifier;
            Object expectedValue;

            public LetStatementTest(String input, String expectedIdentifier, Object expectedValue) {
                this.input = input;
                this.expectedIdentifier = expectedIdentifier;
                this.expectedValue = expectedValue;
            }
        }
        LetStatementTest[] tests = new LetStatementTest[]{
                new LetStatementTest("let x = 5;", "x", 5),
                new LetStatementTest("let y = true;", "y", true),
                new LetStatementTest("let foobar = y", "foobar", "y"),
        };
        for (LetStatementTest tt : tests) {
            Parser p = new Parser(tt.input);
            Program program = p.parseProgram();
            checkParserErrors(p);

            if (program.statements.size() != 1) {
                throw new RuntimeException("program has not enough statements. got=" + program.statements.size());
            }

            Statement stmt = program.statements.get(0);
            if (!testLetStatement(stmt, tt.expectedIdentifier)) {
                return;
            }
            Expression val = ((LetStatement)stmt).value;
            if (!testLiteralExpression(val,tt.expectedValue)) {
                return;
            }
        }
    }

    @Test
    public void testReturnStatements() {
        String input = "return 5;return 10;return 993322;";
        Parser p = new Parser(input);
        Program program = p.parseProgram();
        checkParserErrors(p);

        if (program.statements.size() != 3) {
            throw new RuntimeException("program.statements does not contain 3 statements, got=" + program.statements.size());
        }

        for (Statement stmt : program.statements) {
            if (!(stmt instanceof ReturnStatement)) {
                System.err.println("stmt not returnStatement. got=" + stmt.getClass());
                continue;
            }
            if (!stmt.tokenLiteral().equals("return")) {
                System.err.println("stmt.tokenLiteral not 'return',got "+stmt.tokenLiteral());
            }
        }

    }
    private boolean testLetStatement(Statement s, String name) {
        if (!s.tokenLiteral().equals("let")) {
            System.err.println("s.TokenLiteral not 'let'. got=" + s.tokenLiteral());
            return false;
        }
        LetStatement letStmt = null;
        try {
            letStmt = (LetStatement) s;
        } catch (Exception e) {
            System.err.println("s not LetStatement. got=" + s.getClass());
            return false;
        }

        if (!letStmt.name.value.equals(name)) {
            System.err.println("letStmt.Name.Value not "+name+". got="+letStmt.name);
            return false;
        }

        return true;
    }

    private void checkParserErrors(Parser p) {
        if (p.errors.size() == 0) {
            return;
        }

        System.err.println("parser has " + p.errors.size() + " errors");
        for (String msg : p.errors) {
            System.err.println("parser error:" + msg);
        }
        if (p.errors.size() > 0) {
            System.exit(1);
        }
    }

    private boolean checkLetStatement(Statement s, String name) {
        if (!s.tokenLiteral().equals( "let")) {
            System.err.println("s.tokenLiteral() != let,got="+s.tokenLiteral());
            return false;
        }

        if (!(s instanceof LetStatement)) {
            System.err.println("s is not letStatement,got="+s.getClass());
            return false;
        }

        LetStatement letStmt = (LetStatement)s;
        if (!letStmt.name.value.equals(name)) {
            System.err.println("letStmt.name.value not "+name+",got="+letStmt.name.value);
            return false;
        }

        if (!letStmt.name.tokenLiteral().equals(name)) {
            System.err.println("s.name not " + name + ",got="+letStmt.name.tokenLiteral());
            return false;
        }
        return true;
    }

    @Test
    public void testIdentifierExpression() {
        Parser p = new Parser("foobar;");

        Program program = p.parseProgram();
        checkParserErrors(p);

        if (program.statements.size() != 1) {
            throw new RuntimeException("program has not enough statements. got="+program.statements.size());
        }
        Statement stmt = program.statements.get(0);
        if (!(stmt instanceof ExpressionStatement)) {
            throw new RuntimeException("program.statements.get(0) is not ExpressionStatement. got=" + stmt.getClass());
        }
        Identifier ident = null;
        try {
            ident = (Identifier) ((ExpressionStatement) stmt).expression;
        } catch (Exception e) {
            throw new RuntimeException("exp not Identifier. got = " + ((ExpressionStatement) stmt).expression.getClass());
        }

        if (!ident.value.equals("foobar")) {
            throw new RuntimeException("ident.value not " + "foobar" + ". got=" + ident.value);
        }

        if (!ident.tokenLiteral().equals("foobar")) {
            throw new RuntimeException("ident.tokenLiteral() not " + "foobar" + ". got=" + ident.tokenLiteral());
        }
    }

    @Test
    public void testIntegerLiteralExpression() {
        Parser p = new Parser("5;");

        Program program = p.parseProgram();
        checkParserErrors(p);

        if (program.statements.size() != 1) {
            throw new RuntimeException("program has not enough statements. got="+program.statements.size());
        }
        Statement stmt = program.statements.get(0);
        if (!(stmt instanceof ExpressionStatement)) {
            throw new RuntimeException("program.statements.get(0) is not ExpressionStatement. got=" + stmt.getClass());
        }

        IntegerLiteral literal = null;
        try {
            literal = (IntegerLiteral) ((ExpressionStatement) stmt).expression;
        } catch (Exception e) {
            throw new RuntimeException("exp not IntegerLiteral. got = " + ((ExpressionStatement) stmt).expression.getClass());
        }

        if (literal.value != 5) {
            throw new RuntimeException("literal.value not " + 5 + ". got=" + literal.value);
        }

        if (!literal.tokenLiteral().equals("5")) {
            throw new RuntimeException("literal.tokenLiteral() not " + "5" + ". got=" + literal.tokenLiteral());
        }
    }

    @Test
    public void testParsingPrefixExpressions() {
        class PrefixTest {
            public String input;
            public String operator;
            public Object value;

            public PrefixTest(String input, String operator, Object value) {
                this.input = input;
                this.operator = operator;
                this.value = value;
            }
        }

        List<PrefixTest> prefixTests = new ArrayList<>();
        prefixTests.add(new PrefixTest("!5;","!",5));
        prefixTests.add(new PrefixTest("-15;","-",15));
        prefixTests.add(new PrefixTest("!true;","!",true));
        prefixTests.add(new PrefixTest("!false;","!",false));

        for (PrefixTest tt : prefixTests) {
            Parser p = new Parser(tt.input);
            Program program = p.parseProgram();
            checkParserErrors(p);

            if (program.statements.size() != 1) {
                throw new RuntimeException("program has not enough statements. got="+program.statements.size());
            }
            Statement stmt = program.statements.get(0);
            if (!(stmt instanceof ExpressionStatement)) {
                throw new RuntimeException("program.statements.get(0) is not ExpressionStatement. got=" + stmt.getClass());
            }

            PrefixExpression exp = null;
            try {
                exp = (PrefixExpression) ((ExpressionStatement) stmt).expression;
            } catch (Exception e) {
                throw new RuntimeException("exp not PrefixExpression. got = " + ((ExpressionStatement) stmt).expression.getClass());
            }

            if (!exp.operator.equals(tt.operator)) {
                throw new RuntimeException("exp.operator is not " + tt.operator + ". got=" + exp.operator);
            }

            if (!testLiteralExpression(exp.right,tt.value)) {
                return;
            }
        }
    }

    @Test
    public void testParsingInfixExpression() {
        class InfixTest {
            String input;
            Object leftValue;
            String operator;
            Object rightValue;

            public InfixTest(String input, Object leftValue, String operator, Object rightValue) {
                this.input = input;
                this.leftValue = leftValue;
                this.operator = operator;
                this.rightValue = rightValue;
            }
        }

        List<InfixTest> infixTests = new ArrayList<>();
        infixTests.add(new InfixTest("5 + 5;",5, "+",5));
        infixTests.add(new InfixTest("5 - 5;",5, "-",5));
        infixTests.add(new InfixTest("5 * 5;",5, "*",5));
        infixTests.add(new InfixTest("5 / 5;",5, "/",5));
        infixTests.add(new InfixTest("5 > 5;",5, ">",5));
        infixTests.add(new InfixTest("5 < 5;",5, "<",5));
        infixTests.add(new InfixTest("5 == 5;",5, "==",5));
        infixTests.add(new InfixTest("5 != 5;",5, "!=",5));
        infixTests.add(new InfixTest("true == true", true, "==", true));
        infixTests.add(new InfixTest("true != false", true, "!=", false));
        infixTests.add(new InfixTest("false == false", false, "==", false));


        for (InfixTest tt : infixTests) {
            Parser p = new Parser(tt.input);
            Program program = p.parseProgram();
            checkParserErrors(p);

            if (program.statements.size() != 1) {
                throw new RuntimeException("program has not enough statements. got="+program.statements.size());
            }
            Statement stmt = program.statements.get(0);
            if (!(stmt instanceof ExpressionStatement)) {
                throw new RuntimeException("program.statements.get(0) is not ExpressionStatement. got=" + stmt.getClass());
            }
            InfixExpression exp = null;
            try {
                exp = (InfixExpression) ((ExpressionStatement) stmt).expression;
            } catch (Exception e) {
                throw new RuntimeException("exp not InfixExpression. got = " + ((ExpressionStatement) stmt).expression.getClass());
            }

            if (!testLiteralExpression(exp.left,tt.leftValue)) {
                return;
            }

            if (!exp.operator.equals(tt.operator)) {
                throw new RuntimeException("exp.operator is not " + tt.operator + ". got=" + exp.operator);
            }

            if (!testLiteralExpression(exp.right,tt.rightValue)) {
                return;
            }

        }
    }

    @Test
    public void testOperatorPrecedenceParsing() {
        String[][] tests = new String[][]{
                {"-a * b","((-a) * b)"},
                {"!-a","(!(-a))"},
                {"a + b + c","((a + b) + c)"},
                {"a + b - c","((a + b) - c)"},
                {"a * b * c","((a * b) * c)"},
                {"a * b / c","((a * b) / c)"},
                {"a + b / c","(a + (b / c))"},
                {"a + b * c + d / e - f","(((a + (b * c)) + (d / e)) - f)",},
                {"3 + 4; -5 * 5","(3 + 4)((-5) * 5)"},
                {"5 > 4 == 3 < 4","((5 > 4) == (3 < 4))"},
                {"5 < 4 != 3 > 4","((5 < 4) != (3 > 4))"},
                {"3 + 4 * 5 == 3 * 1 + 4 * 5","((3 + (4 * 5)) == ((3 * 1) + (4 * 5)))"},
                {"3 + 4 * 5 == 3 * 1 + 4 * 5","((3 + (4 * 5)) == ((3 * 1) + (4 * 5)))"},
                {
                        "true",
                        "true",
                },
                {
                        "false",
                        "false",
                },
                {
                        "3 > 5 == false",
                        "((3 > 5) == false)",
                },
                {
                        "3 < 5 == true",
                        "((3 < 5) == true)",
                },
                {
                        "1 + (2 + 3) + 4",
                        "((1 + (2 + 3)) + 4)",
                },
                {
                        "(5 + 5) * 2",
                        "((5 + 5) * 2)",
                },
                {
                        "2 / (5 + 5)",
                        "(2 / (5 + 5))",
                },
                {
                        "(5 + 5) * 2 * (5 + 5)",
                        "(((5 + 5) * 2) * (5 + 5))",
                },
                {
                        "-(5 + 5)",
                        "(-(5 + 5))",
                },
                {
                        "!(true == true)",
                        "(!(true == true))",
                },
                {
                        "a + add(b * c) + d",
                        "((a + add((b * c))) + d)",
                },
                {
                        "add(a, b, 1, 2 * 3, 4 + 5, add(6, 7 * 8))",
                        "add(a, b, 1, (2 * 3), (4 + 5), add(6, (7 * 8)))",
                },
                {
                        "add(a + b + c * d / f + g)",
                        "add((((a + b) + ((c * d) / f)) + g))",
                },
        };
        for (String[] tt : tests) {
            Parser p = new Parser(tt[0]);
            Program program = p.parseProgram();
            checkParserErrors(p);
            if (!program.string().equals(tt[1])) {
                System.err.println("expected=" + tt[1] + " got=" + program.string());
            }
        }
    }
    @Test
    public void testBooleanLiteralExpression() {
        Parser p = new Parser("true;");

        Program program = p.parseProgram();
        checkParserErrors(p);

        if (program.statements.size() != 1) {
            throw new RuntimeException("program has not enough statements. got="+program.statements.size());
        }
        Statement stmt = program.statements.get(0);
        if (!(stmt instanceof ExpressionStatement)) {
            throw new RuntimeException("program.statements.get(0) is not ExpressionStatement. got=" + stmt.getClass());
        }

        BooleanLiteral literal = null;
        try {
            literal = (BooleanLiteral) ((ExpressionStatement) stmt).expression;
        } catch (Exception e) {
            throw new RuntimeException("exp not BooleanLiteral. got = " + ((ExpressionStatement) stmt).expression.getClass());
        }

        if (literal.value != true) {
            throw new RuntimeException("literal.value not " + true + ". got=" + literal.value);
        }

        if (!literal.tokenLiteral().equals("true")) {
            throw new RuntimeException("literal.tokenLiteral() not " + "true" + ". got=" + literal.tokenLiteral());
        }
    }

    @Test
    public void testIfExpression() {
        Parser p = new Parser("if (x < y) {x} else {y}");
        Program program = p.parseProgram();
        checkParserErrors(p);

        if (program.statements.size() != 1) {
            throw new RuntimeException("program has not enough statements. got="+program.statements.size());
        }
        Statement stmt = program.statements.get(0);
        if (!(stmt instanceof ExpressionStatement)) {
            throw new RuntimeException("program.statements.get(0) is not ExpressionStatement. got=" + stmt.getClass());
        }

        IfExpression exp = null;
        try {
            exp = (IfExpression) ((ExpressionStatement) stmt).expression;
        } catch (Exception e) {
            throw new RuntimeException("exp is not IfExpression. got=" + ((ExpressionStatement) stmt).expression.getClass());
        }

        if (!testInfixExpression(exp.condition,"x","<","y")) {
            return;
        }

        if (exp.consequence.statements.size() != 1) {
            throw new RuntimeException("concequence is not 1 statements. got=" + exp.consequence.statements.size());
        }

        if (!(exp.consequence.statements.get(0) instanceof ExpressionStatement)) {
            throw new RuntimeException("statements[0] is not ExpressionStatement. got=" + exp.consequence.statements.get(0).getClass());
        }

        ExpressionStatement consequence = (ExpressionStatement) exp.consequence.statements.get(0);
        if (!testIdentifier(consequence.expression,"x")) {
            return;
        }

        /*if (exp.alternative != null) {
            System.err.println("exp.alternative was not null. got=" + exp.alternative);
        }*/
    }

    @Test
    public void testFunctionLiteralParsing() {
        Parser p = new Parser("fn(x,y){x+y;}");
        Program program = p.parseProgram();
        checkParserErrors(p);

        if (program.statements.size() != 1) {
            throw new RuntimeException("program has not enough statements. got=" + program.statements.size());
        }
        Statement stmt = program.statements.get(0);
        if (!(stmt instanceof ExpressionStatement)) {
            throw new RuntimeException("program.statements.get(0) is not ExpressionStatement. got=" + stmt.getClass());
        }

        FunctionLiteral function = null;
        try {
            function = (FunctionLiteral) ((ExpressionStatement) stmt).expression;
        } catch (Exception e) {
            throw new RuntimeException("exp is not FunctionLiteral. got=" + ((ExpressionStatement) stmt).expression.getClass());
        }

        if (function.parameters.size() != 2) {
            throw new RuntimeException("function literal parameters wrong. want 2, got=" + function.parameters.size());
        }

        testLiteralExpression(function.parameters.get(0),"x");
        testLiteralExpression(function.parameters.get(1),"y");

        if (function.body.statements.size() != 1) {
            throw new RuntimeException("function.body.statements has not 1 statements. got=" + function.body.statements.size());
        }

        ExpressionStatement bodyStmt = null;
        try {
            bodyStmt = (ExpressionStatement) function.body.statements.get(0);
        } catch (Exception e) {
            throw new RuntimeException("function body stmt is not ast.ExpressionStatement. got=" + function.body.statements.get(0).getClass());
        }

        testInfixExpression(bodyStmt.expression,"x","+","y");
    }

    @Test
    public void testFunctionParameterParsing() {
        class FunctionTest {
            String input;
            String[] expectParams;

            public FunctionTest(String input, String[] expectParams) {
                this.input = input;
                this.expectParams = expectParams;
            }
        }
        FunctionTest[] tests = new FunctionTest[] {
                new FunctionTest("fn() {};",new String[0]),
                new FunctionTest("fn(x) {};",new String[] {"x"}),
                new FunctionTest("fn(x,y,z){};",new String[]{"x","y","z"})
        };
        for (FunctionTest tt : tests) {
            Parser p = new Parser(tt.input);
            Program program = p.parseProgram();
            checkParserErrors(p);

            ExpressionStatement stmt = (ExpressionStatement) program.statements.get(0);
            FunctionLiteral function = (FunctionLiteral) stmt.expression;
            if (function.parameters.size() != tt.expectParams.length) {
                throw  new RuntimeException("length parameters wrong. want " + tt.expectParams.length + ",got="+function.parameters.size());
            }
            for (int i = 0; i < tt.expectParams.length; i++) {
                testLiteralExpression(function.parameters.get(i),tt.expectParams[i]);
            }
        }
    }

    @Test
    public void testCallExpressionParsing() {
        Parser p = new Parser("add(1, 2 * 3, 4 + 5);");
        Program program = p.parseProgram();
        checkParserErrors(p);

        if (program.statements.size() != 1) {
            throw new RuntimeException("program has not enough statements. got=" + program.statements.size());
        }
        Statement stmt = program.statements.get(0);
        if (!(stmt instanceof ExpressionStatement)) {
            throw new RuntimeException("program.statements.get(0) is not ExpressionStatement. got=" + stmt.getClass());
        }

        CallExpression exp = null;
        try {
            exp = (CallExpression) ((ExpressionStatement) stmt).expression;
        } catch (Exception e) {
            throw new RuntimeException("stmt.Expression is not ast.CallExpression. got=" + ((ExpressionStatement) stmt).expression.getClass());
        }
        if (!testIdentifier(exp.function,"add")) {
            return;
        }
        if (exp.arguments.size() != 3) {
            throw new RuntimeException("wrong length of arguments. got=" + exp.arguments.size());
        }
        testLiteralExpression(exp.arguments.get(0),1);
        testInfixExpression(exp.arguments.get(1),2,"*",3);
        testInfixExpression(exp.arguments.get(2),4,"+",5);

    }

    private boolean testInfixExpression(Expression expression,Object left,String operator,Object right) {
        InfixExpression exp = null;
        try {
            exp = (InfixExpression)expression;
        } catch (Exception e) {
            throw new RuntimeException("exp not InfixExpression. got = " + expression.getClass());
        }

        if (!testLiteralExpression(exp.left,left)) {
            return false;
        }

        if (!exp.operator.equals(operator)) {
            throw new RuntimeException("exp.operator is not " + operator + ". got=" + exp.operator);
        }

        if (!testLiteralExpression(exp.right,right)) {
            return false;
        }
        return true;
    }

    private boolean testIntegerLiteral(Expression il,int value) {
        if (!(il instanceof IntegerLiteral)) {
            System.err.println("il not IntegerLiteral. got=" + il.getClass());
            return false;
        }

        IntegerLiteral integ = (IntegerLiteral) il;
        if (integ.value != value) {
            System.err.println("integ.value not " + value + ". got=" + integ.value);
            return false;
        }

        if (!integ.tokenLiteral().equals(String.valueOf(value))) {
            System.err.println("integ.tokenLiteral() not " + value + ". got=" + integ.tokenLiteral());
            return false;
        }

        return true;
    }

    private boolean testLiteralExpression(Expression exp, Object expected) {

        if(expected instanceof Integer) {
            return testIntegerLiteral(exp,(int)expected);
        } else if(expected instanceof String) {
            return testIdentifier(exp,expected.toString());
        } else if(expected instanceof Boolean) {
            return testBooleanLiteral(exp, (Boolean) expected);
        }
        System.err.println("type of exp not handled. got="+exp.getClass());
        return false;
    }

    private boolean testBooleanLiteral(Expression exp,boolean value) {
        BooleanLiteral bo = null;
        try {
            bo = (BooleanLiteral) exp;
        } catch (Exception e) {
            System.err.println("exp not BooleanLiteral. got="+exp.getClass());
            return false;
        }

        if (bo.value != value) {
            System.err.println("bo.value not " + value + ". got=" + bo.value);
        }

        if(!bo.tokenLiteral().equals(String.valueOf(value))) {
            System.err.println("bo.tokenLiteral() not " + value + ". got=" + bo.tokenLiteral());
            return false;
        }

        return true;
    }

    private boolean testIdentifier(Expression exp, String value) {
        Identifier ident = null;
        try {
            ident = (Identifier) exp;
        } catch (Exception e) {
            System.err.println("exp not Identifier. got=" + exp.getClass());
            return false;
        }

        if (!ident.tokenLiteral().equals(value)) {
            System.err.println("ident.tokenLiteral() not " + value + " got=" + ident.tokenLiteral());
            return false;
        }
        return true;
    }


}