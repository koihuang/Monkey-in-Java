package com.koi.monkey.token;

import java.util.HashMap;
import java.util.Map;

/**
 * @author whuang
 * @date 2019/10/17
 */
public class Token {

    public String tokenType;
    public String literal;

    private static Map<String,String> keywords = new HashMap<>();

    static {
        keywords.put("fn",TokenType.FUNCTION);
        keywords.put("let",TokenType.LET);
        keywords.put("true",TokenType.TRUE);
        keywords.put("false",TokenType.FALSE);
        keywords.put("if",TokenType.IF);
        keywords.put("else",TokenType.ELSE);
        keywords.put("return",TokenType.RETURN);
    }
    public Token(){}

    public Token(String tokenType,String literal) {
        this.tokenType = tokenType;
        this.literal = literal;
    }

    public Token(String tokenType,byte ch) {
        this.tokenType = tokenType;
        this.literal = String.valueOf((char)ch);
    }

    public static String lookupIdent(String ident) {
        if(keywords.containsKey(ident)) {
            return keywords.get(ident);
        } else {
            return TokenType.IDENT;
        }
    }

    @Override
    public String toString() {
        return "Token{" +
                "tokenType='" + tokenType + '\'' +
                ", literal='" + literal + '\'' +
                '}';
    }
}
