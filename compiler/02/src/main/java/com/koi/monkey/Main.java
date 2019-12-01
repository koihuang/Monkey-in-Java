package com.koi.monkey;

import com.koi.monkey.repl.Repl;

/**
 * @author whuang
 * @date 2019/10/17
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("Hello ! this is the Monkey programing language !");
        System.out.println("Feel free to type in commands ");
        Repl.start();
    }
}
