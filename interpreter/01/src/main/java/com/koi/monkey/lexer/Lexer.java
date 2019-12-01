package com.koi.monkey.lexer;

import com.koi.monkey.token.Token;
import com.koi.monkey.token.TokenType;

/**
 * @author whuang
 * @date 2019/10/17
 */
public class Lexer {
    private String input;
    private int position;
    private int readPosition;
    private byte ch;

    public Lexer(String input) {
        this.input = input;
        this.readChar();
    }

    public Token nextToken() {

        skipWhiteSpace();

        Token tok = null;
        switch (this.ch) {
            case '=':
                if (peekChar() == '=') {
                    int chTmp = this.ch;
                    this.readChar();
                    tok = new Token(TokenType.EQ,"==");
                } else {
                    tok = new Token(TokenType.ASSIGN,this.ch);
                }
                break;
            case '-':
                tok = new Token(TokenType.MINUS,this.ch);
                break;
            case '!':
                if (peekChar() == '=') {
                    int chTmp = this.ch;
                    this.readChar();
                    tok = new Token(TokenType.NOT_EQ,"!=");
                } else {
                    tok = new Token(TokenType.BANG, this.ch);
                }
                break;
            case '/':
                tok = new Token(TokenType.SLASH,this.ch);
                break;
            case '*':
                tok = new Token(TokenType.ASTERISK,this.ch);
                break;
            case '<':
                tok = new Token(TokenType.LT,this.ch);
                break;
            case '>':
                tok = new Token(TokenType.GT,this.ch);
                break;
            case ';':
                tok = new Token(TokenType.SEMICOLON,this.ch);
                break;
            case '(':
                tok = new Token(TokenType.LPAREN,this.ch);
                break;
            case ')':
                tok = new Token(TokenType.RPAREN,this.ch);
                break;
            case ',':
                tok = new Token(TokenType.COMMA,this.ch);
                break;
            case '+':
                tok = new Token(TokenType.PLUS,this.ch);
                break;
            case '{':
                tok = new Token(TokenType.LBRACE,this.ch);
                break;
            case '}':
                tok = new Token(TokenType.RBRACE,this.ch);
                break;
            case 0:
                tok = new Token(TokenType.EOF,"");
                break;
            default:
                if(isLetter(this.ch)) {
                    tok = new Token();
                    tok.literal = readIdentifier();
                    tok.tokenType = Token.lookupIdent(tok.literal);
                    return tok;
                } else if(isDigit(this.ch)) {
                    tok = new Token();
                    tok.tokenType = TokenType.INT;
                    tok.literal = readNumber();
                    return tok;
                }else {
                    tok = new Token(TokenType.ILLEGAL,this.ch);
                }

        }
        this.readChar();
        return tok;
    }

    private String readNumber() {
        int positioTmp = this.position;
        while (isDigit(this.ch)) {
            this.readChar();
        }
        return input.substring(positioTmp,this.position);
    }

    private boolean isDigit(byte ch) {
        return '0' <= ch && ch <= '9';
    }

    private void skipWhiteSpace() {
        while (' ' == this.ch || this.ch == '\t' || this.ch == '\r' || this.ch == '\n' ) {
            this.readChar();
        }
    }

    private void readChar() {
        if (this.readPosition >= this.input.length()) {
            this.ch = 0;
        } else {
            this.ch = (byte) this.input.charAt(this.readPosition);
        }
        this.position = this.readPosition;
        this.readPosition++;
    }

    private String readIdentifier() {
        int positioTmp = this.position;
        while (isLetter(this.ch)) {
            this.readChar();
        }
        return input.substring(positioTmp,this.position);
    }

    private boolean isLetter(byte ch) {
        return ('a' <= ch && ch <= 'z') || ('A' <= ch && ch <= 'Z') || ('_' == ch);
    }

    private byte peekChar() {
        if (this.readPosition >= this.input.length()) {
            return 0;
        } else {
            return (byte) this.input.charAt(this.readPosition);
        }
    }
}
