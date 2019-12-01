package com.koi.monkey.object;

import com.koi.monkey.ast.BlockStatement;
import com.koi.monkey.ast.Identifier;

import java.util.List;

/**
 * @author whuang
 * @date 2019/11/6
 */
public class Function implements Obj{
    public List<Identifier> parameters;
    public BlockStatement body;
    public Environment env;

    public Function() {
    }

    public Function(List<Identifier> parameters, BlockStatement body, Environment env) {
        this.parameters = parameters;
        this.body = body;
        this.env = env;
    }

    @Override
    public String type() {
        return ObjType.FUNCTION_OBJ;
    }

    @Override
    public String inspect() {
        StringBuilder sb = new StringBuilder();
        sb.append("fn(");
        for (int i = 0; i < parameters.size(); i++) {
            sb.append(parameters.get(i).string());
            if(i<parameters.size() - 1) {
                sb.append(",");
            }
        }
        sb.append(") {\n");
        sb.append(body.string());
        sb.append("\n}");
        return sb.toString();
    }
}
