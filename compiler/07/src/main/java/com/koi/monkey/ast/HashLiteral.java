package com.koi.monkey.ast;

import com.koi.monkey.token.Token;

import java.util.Map;

/**
 * @author whuang
 * @date 2019/11/19
 */
public class HashLiteral implements Expression {

    public Token token;
    public Map<Expression,Expression> pairs;

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
        sb.append("{");
        for (Map.Entry<Expression, Expression> pair : pairs.entrySet()) {
            sb.append(pair.getKey());
            sb.append(":");
            sb.append(pair.getValue());
            sb.append(",");
        }
        sb.subSequence(0,sb.length()-1);
        sb.append("}");
        return sb.toString();
    }
}
