package com.koi.monkey.ast;

import com.koi.monkey.token.Token;

import java.util.concurrent.ExecutorService;

/**
 * @author whuang
 * @date 2019/10/19
 */
public class ReturnStatement implements Statement{

    public Token token;
    public Expression returnValue;


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
        if (this.returnValue != null) {
            sb.append(this.returnValue.string());
        }
        sb.append(";");
        return sb.toString();
    }
}
