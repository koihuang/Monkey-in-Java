package com.koi.monkey.parser;

import com.koi.monkey.ast.*;
import com.koi.monkey.lexer.Lexer;
import com.koi.monkey.token.Token;
import com.koi.monkey.token.TokenType;

import java.util.*;

/**
 * @author whuang
 * @date 2019/10/18
 */
public class Parser {


    public Lexer l;
    public Token curToken;
    public Token peekToken;
    public List<String> errors;

    // 运算符优先级
    private static final int LOWEST = 0;
    private static final int EQUALS = 1; // ==
    private static final int LESSGREATER = 2; // > or <
    private static final int SUM = 3; // +
    private static final int PRODUCT = 4; // *
    private static final int PREFIX = 5; // -X or !x
    private static final int CALL = 6; // myFunction(X)

    private static Map<String, Integer> precedences = new HashMap<>();

    static {
        precedences.put(TokenType.EQ, EQUALS);
        precedences.put(TokenType.NOT_EQ, EQUALS);
        precedences.put(TokenType.LT, LESSGREATER);
        precedences.put(TokenType.GT, LESSGREATER);
        precedences.put(TokenType.PLUS, SUM);
        precedences.put(TokenType.MINUS, SUM);
        precedences.put(TokenType.SLASH, PRODUCT);
        precedences.put(TokenType.ASTERISK, PRODUCT);
        precedences.put(TokenType.LPAREN, CALL);
    }

    private static Set<String> infixOperators = new HashSet<>();

    static {
        infixOperators.add(TokenType.PLUS);
        infixOperators.add(TokenType.MINUS);
        infixOperators.add(TokenType.SLASH);
        infixOperators.add(TokenType.ASTERISK);
        infixOperators.add(TokenType.EQ);
        infixOperators.add(TokenType.NOT_EQ);
        infixOperators.add(TokenType.LT);
        infixOperators.add(TokenType.GT);
        infixOperators.add(TokenType.LPAREN);
    }

    public Parser() {
    }

    public Parser(String input) {
        this.l = new Lexer(input);
        errors = new ArrayList<>();
        // Read two tokens, so curToken and peekToken are both set
        this.nextToken();
        this.nextToken();
    }

    public void peekError(String tokenType) {
        errors.add("expected next token to be " + tokenType + ", got " + this.peekToken.tokenType + " instead");
    }

    public void nextToken() {
        this.curToken = this.peekToken;
        this.peekToken = this.l.nextToken();
    }

    public Program parseProgram() {
        Program program = new Program();
        while (!this.curToken.tokenType.equals(TokenType.EOF)) {
            Statement stmt = parseStatement();
            if (stmt != null) {
                program.statements.add(stmt);
            }
            this.nextToken();
        }
        return program;
    }

    private Statement parseStatement() {
        switch (this.curToken.tokenType) {
            case TokenType.LET:
                return parseLetStatement();
            case TokenType.RETURN:
                return parseReturnStatement();
            default:
                return parseExpressionStement();
        }
    }

    private ExpressionStatement parseExpressionStement() {

        ExpressionStatement stmt = new ExpressionStatement();
        stmt.token = this.curToken;
        stmt.expression = parseExpression(LOWEST);
        if (peekTokenIs(TokenType.SEMICOLON)) {
            nextToken();
        }
        return stmt;
    }

    private Expression parseExpression(int precedence) {
        Expression leftExp = prefixParse(this.curToken.tokenType);

        while (!peekTokenIs(TokenType.SEMICOLON) && precedence < peekPrecedence()) {
            if (!infixOperators.contains(this.peekToken.tokenType)) {
                return leftExp;
            }
            nextToken();
            leftExp = parseInfixExpression(leftExp);
        }

        return leftExp;
    }

    private Expression prefixParse(String tokenType) {
        switch (tokenType) {
            case TokenType.IDENT:
                return parseIdentifier();
            case TokenType.INT:
                return parseIntegerLiteral();
            case TokenType.BANG:
            case TokenType.MINUS:
                return parsePrefixExpression();
            case TokenType.TRUE:
            case TokenType.FALSE:
                return parseBooleanLiteral();
            case TokenType.LPAREN:
                return parseGroupedExpression();
            case TokenType.IF:
                return parseIfExpression();
            case TokenType.FUNCTION:
                return parseFunctionLiteral();
            default:
                noPrefixParseFnError(tokenType);
                return null;
        }
    }

    private Expression parseFunctionLiteral() {
        FunctionLiteral lit = new FunctionLiteral();
        lit.token = this.curToken;

        if (!expectPeek(TokenType.LPAREN)) {
            return null;
        }

        lit.parameters = parseFunctionParameters();

        if (!expectPeek(TokenType.LBRACE)) {
            return null;
        }
        lit.body = parseBlockStatement();
        return lit;
    }

    private List<Identifier> parseFunctionParameters() {
        List<Identifier> identifiers = new ArrayList<>();
        if (peekTokenIs(TokenType.RPAREN)) {
            nextToken();
            return identifiers;
        }
        nextToken();
        Identifier ident = new Identifier(this.curToken, this.curToken.literal);
        identifiers.add(ident);

        while (peekTokenIs(TokenType.COMMA)) {
            nextToken();
            nextToken();
            ident = new Identifier(this.curToken, this.curToken.literal);
            identifiers.add(ident);
        }
        if (!expectPeek(TokenType.RPAREN)) {
            return null;
        }
        return identifiers;
    }


    private Expression parseIfExpression() {
        IfExpression expression = new IfExpression();
        expression.token = this.curToken;

        if (!expectPeek(TokenType.LPAREN)) {
            return null;
        }

        nextToken();

        expression.condition = parseExpression(LOWEST);

        if (!expectPeek(TokenType.RPAREN)) {
            return null;
        }

        if (!expectPeek(TokenType.LBRACE)) {
            return null;
        }
        expression.consequence = parseBlockStatement();
        if (peekTokenIs(TokenType.ELSE)) {
            nextToken();
            if (!expectPeek(TokenType.LBRACE)) {
                return null;
            }
            expression.alternative = parseBlockStatement();
        }
        return expression;
    }

    private BlockStatement parseBlockStatement() {
        BlockStatement block = new BlockStatement();
        block.token = this.curToken;
        block.statements = new ArrayList<>();
        nextToken();
        while (!curTokenIs(TokenType.RBRACE)) {
            Statement stmt = parseStatement();
            if (stmt != null) {
                block.statements.add(stmt);
            }
            nextToken();
        }
        return block;
    }

    private Expression parseBooleanLiteral() {
        return new BooleanLiteral(this.curToken, this.curTokenIs(TokenType.TRUE));
    }


    private Expression parsePrefixExpression() {
        PrefixExpression expression = new PrefixExpression();
        expression.token = this.curToken;
        expression.operator = this.curToken.literal;
        nextToken();
        expression.right = parseExpression(PREFIX);
        return expression;
    }

    private void noPrefixParseFnError(String tokenType) {
        this.errors.add("no prefix parse function for " + tokenType + " found");
    }

    private Expression parseInfixExpression(Expression left) {
        //if call Expression
        if (this.curTokenIs(TokenType.LPAREN)) {
            return parseCallExpression(left);
        }
        // other infix Expression
        InfixExpression expression = new InfixExpression();
        expression.token = this.curToken;
        expression.operator = this.curToken.literal;
        expression.left = left;
        int precedence = this.curPrecedence();
        nextToken();
        expression.right = parseExpression(precedence);
        return expression;
    }

    private Expression parseCallExpression(Expression function) {
        CallExpression exp = new CallExpression();
        exp.token = this.curToken;
        exp.function = function;
        exp.arguments = parseCallArguments();
        return exp;
    }

    private List<Expression> parseCallArguments() {
        List<Expression> args = new ArrayList<>();
        if (peekTokenIs(TokenType.RPAREN)) {
            nextToken();
            return args;
        }
        nextToken();
        args.add(parseExpression(LOWEST));
        while (peekTokenIs(TokenType.COMMA)) {
            nextToken();
            nextToken();
            args.add(parseExpression(LOWEST));
        }
        if (!expectPeek(TokenType.RPAREN)) {
            return null;
        }
        return args;
    }

    public Expression parseGroupedExpression() {
        nextToken();
        Expression exp = parseExpression(LOWEST);
        if (!expectPeek(TokenType.RPAREN)) {
            return null;
        }
        return exp;
    }

    private Expression parseIntegerLiteral() {
        IntegerLiteral lit = new IntegerLiteral();
        lit.token = this.curToken;
        try {
            int value = Integer.parseInt(this.curToken.literal);
            lit.value = value;
        } catch (NumberFormatException e) {
            this.errors.add("could not parse " + this.curToken.literal + " as integer");
            return null;
        }

        return lit;
    }

    private Expression parseIdentifier() {
        return new Identifier(this.curToken, this.curToken.literal);
    }

    private ReturnStatement parseReturnStatement() {
        ReturnStatement stmt = new ReturnStatement();
        stmt.token = this.curToken;
        this.nextToken();
        stmt.returnValue = parseExpression(LOWEST);
        while (this.curTokenIs(TokenType.SEMICOLON)) {
            this.nextToken();
        }
        return stmt;
    }

    private Statement parseLetStatement() {
        LetStatement stmt = new LetStatement();
        stmt.token = this.curToken;

        if (!expectPeek(TokenType.IDENT)) {
            return null;
        }

        stmt.name = new Identifier(this.curToken, this.curToken.literal);

        if (!this.expectPeek(TokenType.ASSIGN)) {
            return null;
        }
        nextToken();
        stmt.value = parseExpression(LOWEST);
        if (!this.curTokenIs(TokenType.SEMICOLON)) {
            this.nextToken();
        }

        return stmt;
    }

    private boolean expectPeek(String tokenType) {
        if (peekTokenIs(tokenType)) {
            this.nextToken();
            return true;
        } else {
            this.peekError(tokenType);
            return false;
        }
    }

    private int peekPrecedence() {
        if (precedences.containsKey(this.peekToken.tokenType)) {
            return precedences.get(this.peekToken.tokenType);
        }
        return LOWEST;
    }

    private int curPrecedence() {
        if (precedences.containsKey(this.curToken.tokenType)) {
            return precedences.get(this.curToken.tokenType);
        }
        return LOWEST;
    }

    private boolean peekTokenIs(String tokenType) {
        return this.peekToken.tokenType.equals(tokenType);
    }

    private boolean curTokenIs(String tokenType) {
        return this.curToken.tokenType.equals(tokenType);
    }

}
