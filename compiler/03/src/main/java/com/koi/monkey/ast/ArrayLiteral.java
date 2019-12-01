package com.koi.monkey.ast;

import com.koi.monkey.token.Token;

import java.util.List;

/**
 * @author whuang
 * @date 2019/11/8
 */
public class ArrayLiteral implements Expression {
    public Token token;
    public List<Expression> elements;

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
        sb.append("[");
        for (int i = 0; i < elements.size(); i++) {
            sb.append(elements.get(i).string());
            if(i<elements.size() -1){
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
