package com.koi.monkey.repl;

import com.koi.monkey.lexer.Lexer;
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
                Lexer l = new Lexer(line);
                Token tok;
                do{
                    tok = l.nextToken();
                    System.out.println(tok);
                } while (!tok.tokenType.equals(TokenType.EOF)) ;
                System.out.print(">>");
            }
        }

    }

}
