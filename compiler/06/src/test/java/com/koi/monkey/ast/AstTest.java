package com.koi.monkey.ast;

import com.koi.monkey.token.Token;
import com.koi.monkey.token.TokenType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author whuang
 * @date 2019/10/27
 */
public class AstTest {
    @Test
    public void testString() {
        Token token = new Token(TokenType.LET,"let");
        Identifier name = new Identifier(new Token(TokenType.IDENT,"myVar"),"myVar");
        Identifier value = new Identifier(new Token(TokenType.IDENT,"anotherVar"),"anotherVar");
        LetStatement ls = new LetStatement();
        ls.token = token;
        ls.name = name;
        ls.value = value;
        List<Statement> statements = new ArrayList<>();
        statements.add(ls);
        Program program = new Program();
        program.statements = statements;
        if (!program.string().equals("let myVar = anotherVar;")) {
            throw new RuntimeException("program.string() wrong. got=" + program.string());
        }
    }
}