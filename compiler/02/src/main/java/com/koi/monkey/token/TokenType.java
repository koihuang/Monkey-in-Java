package com.koi.monkey.token;

/**
 * @author whuang
 * @date 2019/10/17
 */
public class  TokenType {
    public static final String ILLEGAL = "ILLEGAL";
    public static final String EOF = "EOF";

    // Identifiers + Literals
    public static final String IDENT = "IDENT"; //标识符,一般是变量名称
    public static final String INT = "INT"; // 数字类型

    // Operators
    public static final String ASSIGN = "=";
    public static final String PLUS = "+";
    public static final String MINUS = "-";
    public static final String BANG = "!";
    public static final String ASTERISK = "*";
    public static final String SLASH = "/";

    public static final String LT = "<";
    public static final String GT = ">";

    public static final String EQ = "==";
    public static final String NOT_EQ = "!=";

    // Delimiters
    public static final String COMMA = ",";
    public static final String SEMICOLON = ";";

    public static final String LPAREN = "(";
    public static final String RPAREN = ")";
    public static final String LBRACE = "{";
    public static final String RBRACE = "}";

    public static final String LBRACKET = "["  ;
    public static final String RBRACKET = "]";
    public static final String COLON = ":";

    // Keywords
    public static final String FUNCTION = "FUNCTION";
    public static final String LET = "LET";
    public static final String TRUE = "TRUE";
    public static final String FALSE = "ELSE";
    public static final String IF = "IF";
    public static final String ELSE = "ELSE";
    public static final String RETURN = "RETURN";
    public static final String STRING = "STRING";
}
