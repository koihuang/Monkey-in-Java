package com.koi.monkey.ast;

import com.koi.monkey.token.Token;

/**
 * @author whuang
 * @date 2019/11/8
 */
public class IndexExpression implements Expression {

    public Token token;
    public Expression left;
    public Expression index;

    @Override
    public void expressionNode() {

    }

    @Override
    public String tokenLiteral() {
        return token.literal;
    }

    @Override
    public String string() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(left.string());
        sb.append("[");
        sb.append(index.string());
        sb.append("])");
        return sb.toString();
    }
}
