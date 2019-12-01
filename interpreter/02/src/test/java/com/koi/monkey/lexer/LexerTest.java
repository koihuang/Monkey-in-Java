package com.koi.monkey.lexer;

import com.koi.monkey.token.Token;
import com.koi.monkey.token.TokenType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author whuang
 * @date 2019/10/17
 */
public class LexerTest {
    @Test
    public void testNextToken1() {
        String input = "=+(){},;";

        String[][] tests = {
                {TokenType.ASSIGN,"="},
                {TokenType.PLUS,"+"},
                {TokenType.LPAREN,"("},
                {TokenType.RPAREN,")"},
                {TokenType.LBRACE,"{"},
                {TokenType.RBRACE,"}"},
                {TokenType.COMMA,","},
                {TokenType.SEMICOLON,";"},
                {TokenType.EOF,""}
        } ;
        Lexer l = new Lexer(input);
        for (int i = 0; i < tests.length; i++) {
            String[] tt = tests[i];
            Token tok = l.nextToken();
            if (!tok.tokenType.equals(tt[0])) {
                throw new RuntimeException("tests["+i+"] - tokenType wrong. expected = " + tt[0] +", got = " + tok.tokenType);
            }

            if (!tok.literal.equals(tt[1])) {
                throw new RuntimeException("tests["+i+"] - literal wrong. expected = " + tt[1] +", got = " + tok.literal);
            }
        }
    }

    @Test
    public void testNextToken2() {
        String input = "let five = 5;let ten = 10;let add = fn(x, y) {  x + y;};let result = add(five, ten);";
        String[][] tests = {
                {TokenType.LET,"let"},
                {TokenType.IDENT,"five"},
                {TokenType.ASSIGN,"="},
                {TokenType.INT,"5"},
                {TokenType.SEMICOLON,";"},
                {TokenType.LET,"let"},
                {TokenType.IDENT,"ten"},
                {TokenType.ASSIGN,"="},
                {TokenType.INT,"10"},
                {TokenType.SEMICOLON,";"},
                {TokenType.LET,"let"},
                {TokenType.IDENT,"add"},
                {TokenType.ASSIGN,"="},
                {TokenType.FUNCTION,"fn"},
                {TokenType.LPAREN,"("},
                {TokenType.IDENT,"x"},
                {TokenType.COMMA,","},
                {TokenType.IDENT,"y"},
                {TokenType.RPAREN,")"},
                {TokenType.LBRACE,"{"},
                {TokenType.IDENT,"x"},
                {TokenType.PLUS,"+"},
                {TokenType.IDENT,"y"},
                {TokenType.SEMICOLON,";"},
                {TokenType.RBRACE,"}"},
                {TokenType.SEMICOLON,";"},
                {TokenType.LET,"let"},
                {TokenType.IDENT,"result"},
                {TokenType.ASSIGN,"="},
                {TokenType.IDENT,"add"},
                {TokenType.LPAREN,"("},
                {TokenType.IDENT,"five"},
                {TokenType.COMMA,","},
                {TokenType.IDENT,"ten"},
                {TokenType.RPAREN,")"},
                {TokenType.SEMICOLON,";"},
                {TokenType.EOF,""}
        } ;
        Lexer l = new Lexer(input);
        for (int i = 0; i < tests.length; i++) {
            String[] tt = tests[i];
            Token tok = l.nextToken();
            if (!tok.tokenType.equals(tt[0])) {
                System.out.println("tt[0] = " + tt[0]);
                System.out.println("tt[1] = " + tt[1]);
                throw new RuntimeException("tests["+i+"] - tokenType wrong. expected = " + tt[0] +", got = " + tok.tokenType);
            }

            if (!tok.literal.equals(tt[1])) {
                throw new RuntimeException("tests["+i+"] - literal wrong. expected = " + tt[1] +", got = " + tok.literal);
            }
        }
    }


    @Test
    public void testNextToken3() {
        String input = "let five = 5;let ten = 10;let add = fn(x, y) {  x + y;};let result = add(five, ten);!-/*5;5 < 10 > 5;";
        String[][] tests = {
                {TokenType.LET,"let"},
                {TokenType.IDENT,"five"},
                {TokenType.ASSIGN,"="},
                {TokenType.INT,"5"},
                {TokenType.SEMICOLON,";"},
                {TokenType.LET,"let"},
                {TokenType.IDENT,"ten"},
                {TokenType.ASSIGN,"="},
                {TokenType.INT,"10"},
                {TokenType.SEMICOLON,";"},
                {TokenType.LET,"let"},
                {TokenType.IDENT,"add"},
                {TokenType.ASSIGN,"="},
                {TokenType.FUNCTION,"fn"},
                {TokenType.LPAREN,"("},
                {TokenType.IDENT,"x"},
                {TokenType.COMMA,","},
                {TokenType.IDENT,"y"},
                {TokenType.RPAREN,")"},
                {TokenType.LBRACE,"{"},
                {TokenType.IDENT,"x"},
                {TokenType.PLUS,"+"},
                {TokenType.IDENT,"y"},
                {TokenType.SEMICOLON,";"},
                {TokenType.RBRACE,"}"},
                {TokenType.SEMICOLON,";"},
                {TokenType.LET,"let"},
                {TokenType.IDENT,"result"},
                {TokenType.ASSIGN,"="},
                {TokenType.IDENT,"add"},
                {TokenType.LPAREN,"("},
                {TokenType.IDENT,"five"},
                {TokenType.COMMA,","},
                {TokenType.IDENT,"ten"},
                {TokenType.RPAREN,")"},
                {TokenType.SEMICOLON,";"},
//                !-/*5;5 < 10 > 5;
                {TokenType.BANG,"!"},
                {TokenType.MINUS,"-"},
                {TokenType.SLASH,"/"},
                {TokenType.ASTERISK,"*"},
                {TokenType.INT,"5"},
                {TokenType.SEMICOLON,";"},
                {TokenType.INT,"5"},
                {TokenType.LT,"<"},
                {TokenType.INT,"10"},
                {TokenType.GT,">"},
                {TokenType.INT,"5"},
                {TokenType.SEMICOLON,";"},
                {TokenType.EOF,""}
        } ;
        Lexer l = new Lexer(input);
        for (int i = 0; i < tests.length; i++) {
            String[] tt = tests[i];
            Token tok = l.nextToken();
            if (!tok.tokenType.equals(tt[0])) {
                System.out.println("tt[0] = " + tt[0]);
                System.out.println("tt[1] = " + tt[1]);
                throw new RuntimeException("tests["+i+"] - tokenType wrong. expected = " + tt[0] +", got = " + tok.tokenType);
            }

            if (!tok.literal.equals(tt[1])) {
                throw new RuntimeException("tests["+i+"] - literal wrong. expected = " + tt[1] +", got = " + tok.literal);
            }
        }
    }

    @Test
    public void testNextToken4() {
        String input = "let five = 5;let ten = 10;let add = fn(x, y) {  x + y;};let result = add(five, ten);!-/*5;5 < 10 > 5;if (5 < 10) {    return true;} else {    return false;}";
        String[][] tests = {
                {TokenType.LET,"let"},
                {TokenType.IDENT,"five"},
                {TokenType.ASSIGN,"="},
                {TokenType.INT,"5"},
                {TokenType.SEMICOLON,";"},
                {TokenType.LET,"let"},
                {TokenType.IDENT,"ten"},
                {TokenType.ASSIGN,"="},
                {TokenType.INT,"10"},
                {TokenType.SEMICOLON,";"},
                {TokenType.LET,"let"},
                {TokenType.IDENT,"add"},
                {TokenType.ASSIGN,"="},
                {TokenType.FUNCTION,"fn"},
                {TokenType.LPAREN,"("},
                {TokenType.IDENT,"x"},
                {TokenType.COMMA,","},
                {TokenType.IDENT,"y"},
                {TokenType.RPAREN,")"},
                {TokenType.LBRACE,"{"},
                {TokenType.IDENT,"x"},
                {TokenType.PLUS,"+"},
                {TokenType.IDENT,"y"},
                {TokenType.SEMICOLON,";"},
                {TokenType.RBRACE,"}"},
                {TokenType.SEMICOLON,";"},
                {TokenType.LET,"let"},
                {TokenType.IDENT,"result"},
                {TokenType.ASSIGN,"="},
                {TokenType.IDENT,"add"},
                {TokenType.LPAREN,"("},
                {TokenType.IDENT,"five"},
                {TokenType.COMMA,","},
                {TokenType.IDENT,"ten"},
                {TokenType.RPAREN,")"},
                {TokenType.SEMICOLON,";"},
//                !-/*5;5 < 10 > 5;
                {TokenType.BANG,"!"},
                {TokenType.MINUS,"-"},
                {TokenType.SLASH,"/"},
                {TokenType.ASTERISK,"*"},
                {TokenType.INT,"5"},
                {TokenType.SEMICOLON,";"},
                {TokenType.INT,"5"},
                {TokenType.LT,"<"},
                {TokenType.INT,"10"},
                {TokenType.GT,">"},
                {TokenType.INT,"5"},
                {TokenType.SEMICOLON,";"},
                {TokenType.IF,"if"},
                {TokenType.LPAREN,"("},
                {TokenType.INT,"5"},
                {TokenType.LT,"<"},
                {TokenType.INT,"10"},
                {TokenType.RPAREN,")"},
                {TokenType.LBRACE,"{"},
                {TokenType.RETURN,"return"},
                {TokenType.TRUE,"true"},
                {TokenType.SEMICOLON,";"},
                {TokenType.RBRACE,"}"},
                {TokenType.ELSE,"else"},
                {TokenType.LBRACE,"{"},
                {TokenType.RETURN,"return"},
                {TokenType.FALSE,"false"},
                {TokenType.SEMICOLON,";"},
                {TokenType.RBRACE,"}"},
                {TokenType.EOF,""}
        } ;
        Lexer l = new Lexer(input);
        for (int i = 0; i < tests.length; i++) {
            String[] tt = tests[i];
            Token tok = l.nextToken();
            if (!tok.tokenType.equals(tt[0])) {
                System.out.println("tt[0] = " + tt[0]);
                System.out.println("tt[1] = " + tt[1]);
                throw new RuntimeException("tests["+i+"] - tokenType wrong. expected = " + tt[0] +", got = " + tok.tokenType);
            }

            if (!tok.literal.equals(tt[1])) {
                throw new RuntimeException("tests["+i+"] - literal wrong. expected = " + tt[1] +", got = " + tok.literal);
            }
        }
    }

    @Test
    public void testNextToken5() {
        String input = "let five = 5;let ten = 10;let add = fn(x, y) {  x + y;};let result = add(five, ten);!-/*5;5 < 10 > 5;if (5 < 10) {    return true;} else {    return false;}10 == 10;10 != 9;";
        String[][] tests = {
                {TokenType.LET,"let"},
                {TokenType.IDENT,"five"},
                {TokenType.ASSIGN,"="},
                {TokenType.INT,"5"},
                {TokenType.SEMICOLON,";"},
                {TokenType.LET,"let"},
                {TokenType.IDENT,"ten"},
                {TokenType.ASSIGN,"="},
                {TokenType.INT,"10"},
                {TokenType.SEMICOLON,";"},
                {TokenType.LET,"let"},
                {TokenType.IDENT,"add"},
                {TokenType.ASSIGN,"="},
                {TokenType.FUNCTION,"fn"},
                {TokenType.LPAREN,"("},
                {TokenType.IDENT,"x"},
                {TokenType.COMMA,","},
                {TokenType.IDENT,"y"},
                {TokenType.RPAREN,")"},
                {TokenType.LBRACE,"{"},
                {TokenType.IDENT,"x"},
                {TokenType.PLUS,"+"},
                {TokenType.IDENT,"y"},
                {TokenType.SEMICOLON,";"},
                {TokenType.RBRACE,"}"},
                {TokenType.SEMICOLON,";"},
                {TokenType.LET,"let"},
                {TokenType.IDENT,"result"},
                {TokenType.ASSIGN,"="},
                {TokenType.IDENT,"add"},
                {TokenType.LPAREN,"("},
                {TokenType.IDENT,"five"},
                {TokenType.COMMA,","},
                {TokenType.IDENT,"ten"},
                {TokenType.RPAREN,")"},
                {TokenType.SEMICOLON,";"},
                {TokenType.BANG,"!"},
                {TokenType.MINUS,"-"},
                {TokenType.SLASH,"/"},
                {TokenType.ASTERISK,"*"},
                {TokenType.INT,"5"},
                {TokenType.SEMICOLON,";"},
                {TokenType.INT,"5"},
                {TokenType.LT,"<"},
                {TokenType.INT,"10"},
                {TokenType.GT,">"},
                {TokenType.INT,"5"},
                {TokenType.SEMICOLON,";"},
                {TokenType.IF,"if"},
                {TokenType.LPAREN,"("},
                {TokenType.INT,"5"},
                {TokenType.LT,"<"},
                {TokenType.INT,"10"},
                {TokenType.RPAREN,")"},
                {TokenType.LBRACE,"{"},
                {TokenType.RETURN,"return"},
                {TokenType.TRUE,"true"},
                {TokenType.SEMICOLON,";"},
                {TokenType.RBRACE,"}"},
                {TokenType.ELSE,"else"},
                {TokenType.LBRACE,"{"},
                {TokenType.RETURN,"return"},
                {TokenType.FALSE,"false"},
                {TokenType.SEMICOLON,";"},
                {TokenType.RBRACE,"}"},
                {TokenType.INT,"10"},
                {TokenType.EQ,"=="},
                {TokenType.INT,"10"},
                {TokenType.SEMICOLON,";"},
                {TokenType.INT,"10"},
                {TokenType.NOT_EQ,"!="},
                {TokenType.INT,"9"},
                {TokenType.SEMICOLON,";"},
                {TokenType.EOF,""}
        } ;
        Lexer l = new Lexer(input);
        for (int i = 0; i < tests.length; i++) {
            String[] tt = tests[i];
            Token tok = l.nextToken();
            if (!tok.tokenType.equals(tt[0])) {
                System.out.println("tt[0] = " + tt[0]);
                System.out.println("tt[1] = " + tt[1]);
                throw new RuntimeException("tests["+i+"] - tokenType wrong. expected = " + tt[0] +", got = " + tok.tokenType);
            }

            if (!tok.literal.equals(tt[1])) {
                throw new RuntimeException("tests["+i+"] - literal wrong. expected = " + tt[1] +", got = " + tok.literal);
            }
        }
    }
}