package com.koi.monkey.ast;

import com.koi.monkey.token.Token;

/**
 * @author whuang
 * @date 2019/10/27
 */
public class IntegerLiteral implements Expression {

    public Token token;
    public int value;

    @Override
    public void expressionNode() {

    }

    @Override
    public String tokenLiteral() {
        return this.token.literal;
    }

    @Override
    public String string() {
        return this.token.literal;
    }
}
