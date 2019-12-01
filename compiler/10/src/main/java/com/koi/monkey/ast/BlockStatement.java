package com.koi.monkey.ast;

import com.koi.monkey.token.Token;

import java.util.List;

/**
 * @author whuang
 * @date 2019/10/27
 */
public class BlockStatement implements Statement{
    public Token token;
    public List<Statement> statements;

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
        for (Statement s : this.statements) {
            sb.append(s.string());
        }
        return sb.toString();
    }
}
