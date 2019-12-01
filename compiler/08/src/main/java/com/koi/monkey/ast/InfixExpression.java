package com.koi.monkey.ast;

import com.koi.monkey.token.Token;

/**
 * @author whuang
 * @date 2019/10/27
 */
public class InfixExpression implements  Expression{
    public Token token;
    public Expression left;
    public String operator;
    public Expression right;

    @Override
    public void expressionNode() {

    }

    @Override
    public String tokenLiteral() {
        return this.token.literal;
    }

    @Override
    public String string() {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        sb.append(this.left.string());
        sb.append(" " + this.operator + " ");
        sb.append(this.right.string());
        sb.append(")");
        return sb.toString();
    }

}
