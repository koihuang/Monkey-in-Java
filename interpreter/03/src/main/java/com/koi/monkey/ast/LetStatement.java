package com.koi.monkey.ast;

import com.koi.monkey.token.Token;

/**
 * @author whuang
 * @date 2019/10/18
 */
public class LetStatement implements Statement {

    public Token token;
    public Identifier name;
    public Expression value;
    @Override
    public void statementNode() {

    }

    @Override
    public String tokenLiteral() {
        return this.token.literal;
    }

    @Override
    public String string() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.tokenLiteral() + " ");
        sb.append(this.name.string());
        sb.append(" = ");

        if (this.value != null) {
            sb.append(this.value.string());
        }

        sb.append(";");
        return sb.toString();
    }
}
