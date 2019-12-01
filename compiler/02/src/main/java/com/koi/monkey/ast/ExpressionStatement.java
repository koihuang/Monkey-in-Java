package com.koi.monkey.ast;

import com.koi.monkey.token.Token;

/**
 * @author whuang
 * @date 2019/10/27
 */
public class ExpressionStatement implements Statement{
   public Token token;
   public Expression expression;

    @Override
    public void statementNode() {

    }

    @Override
    public String tokenLiteral() {
        return token.literal;
    }

    @Override
    public String string() {
        if (this.expression != null) {
            return this.expression.string();
        }
        return "";
    }
}
