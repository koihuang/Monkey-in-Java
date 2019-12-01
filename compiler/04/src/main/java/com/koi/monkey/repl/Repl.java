package com.koi.monkey.repl;

import com.koi.monkey.ast.Program;
import com.koi.monkey.compiler.Compil;
import com.koi.monkey.evaluator.Evaluator;
import com.koi.monkey.lexer.Lexer;
import com.koi.monkey.object.Environment;
import com.koi.monkey.object.Obj;
import com.koi.monkey.parser.Parser;
import com.koi.monkey.token.Token;
import com.koi.monkey.token.TokenType;
import com.koi.monkey.vm.Vm;

import java.util.HashMap;
import java.util.Scanner;

/**
 * @author whuang
 * @date 2019/10/17
 */
public class Repl {

    public static void start() {
        Scanner scan = new Scanner(System.in);
        System.out.print(">>");

        while (true) {
            if (scan.hasNextLine()) {
                String line = scan.nextLine();
                Parser p = new Parser(line);
                Program program = p.parseProgram();
                if (p.errors.size() != 0) {
                    System.out.println("Woops! We ran into some monkey business here!");
                    System.out.println(" parser errors:");
                    for (String error : p.errors) {
                        System.out.println(error);
                    }
                    continue;
                }

                Compil comp = new Compil();
                comp.compile(program);
                Vm machine = new Vm(comp.bytecode());
                machine.run();
                Obj lastPoppedStackElem = machine.lastPoppedStackElem();
                System.out.println(lastPoppedStackElem.inspect());
                System.out.print(">>");
            }

        }

    }

}
