package com.koi.monkey.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * @author whuang
 * @date 2019/10/18
 */
public class Program implements Node {

    public List<Statement> statements = new ArrayList<>();

    @Override
    public String tokenLiteral() {
        if (this.statements.size() > 0) {
            return this.statements.get(0).tokenLiteral();
        } else {
            return "";
        }
    }

    @Override
    public String string() {
        StringBuilder sb = new StringBuilder();
        for (Statement statement : statements) {
            sb.append(statement.string());
        }
        return sb.toString();
    }
}
