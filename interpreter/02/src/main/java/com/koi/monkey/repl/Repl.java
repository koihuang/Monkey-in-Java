package com.koi.monkey.repl;

import com.koi.monkey.ast.Program;
import com.koi.monkey.lexer.Lexer;
import com.koi.monkey.parser.Parser;
import com.koi.monkey.token.Token;
import com.koi.monkey.token.TokenType;

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
                System.out.println(program.string());
                System.out.print(">>");
            }

        }

    }

}
