package com.koi.monkey.ast;

import com.koi.monkey.token.Token;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author whuang
 * @date 2019/10/27
 */
public class FunctionLiteral implements Expression{
    public Token token; // The 'fn' token
    public List<Identifier> parameters;
    public BlockStatement body;

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
        sb.append(this.tokenLiteral());
        sb.append("(");
        for (int i = 0; i < this.parameters.size(); i++) {
            sb.append(this.parameters.get(i).string());
            if(i < this.parameters.size() - 1) {
                sb.append( ",");
            }
        }
        sb.append(")");
        sb.append(this.body.string());
        return sb.toString();
    }
}
