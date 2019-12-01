package com.koi.monkey;

/**
 * @author whuang
 * @date 2019/11/6
 */
public class Test {
    public static void main(String[] args) {
        test();
    }

    public static void test(Integer...args) {
        System.out.println(args.length);
    }
}
