package com.koi.monkey.ast;

import com.koi.monkey.token.Token;

/**
 * @author whuang
 * @date 2019/11/7
 */
public class StringLiteral implements Expression {

    public Token token;
    public String value;

    public StringLiteral() {
    }

    public StringLiteral(Token token, String value) {
        this.token = token;
        this.value = value;
    }

    @Override
    public void expressionNode() {

    }

    @Override
    public String tokenLiteral() {
        return token.literal;
    }

    @Override
    public String string() {
        return token.literal;
    }
}
