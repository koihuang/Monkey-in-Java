package com.koi.monkey.ast;

import com.koi.monkey.token.Token;

import java.util.List;

/**
 * @author whuang
 * @date 2019/10/27
 */
public class CallExpression implements Expression{
    public Token token; // The '/' token
    public Expression function; // Identifier or FunctionLiteral
    public List<Expression> arguments;

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
        sb.append(this.function.string());
        sb.append("(");
        for (int i = 0; i < this.arguments.size(); i++) {
            sb.append(this.arguments.get(i).string());
            if(i < this.arguments.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append(")");
        return sb.toString();
    }
}
