package com.koi.monkey.ast;

import com.koi.monkey.token.Token;

/**
 * @author whuang
 * @date 2019/10/27
 */
public class IfExpression implements Expression{
    public Token token;
    public Expression condition;
    public BlockStatement consequence;
    public BlockStatement alternative;

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
        sb.append("if");
        sb.append(this.condition.string());
        sb.append(" ");
        sb.append(this.consequence.string());
        if (this.alternative != null) {
            sb.append("else ");
            sb.append(this.alternative.string());
        }
        return sb.toString();
    }
}
