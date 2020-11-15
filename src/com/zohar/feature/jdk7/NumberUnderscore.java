package com.zohar.feature.jdk7;

/**
 * <h3>数字字面量下划线演示类</h3>
 * Java 7 开始，在数字字面量中使用下划线已经合法了，用于提高数字的可读性
 *
 * @author zohar
 * @version 1.0
 * 2020/11/15 21:03
 */
public class NumberUnderscore {

    public static void main(String[] args) {
        System.out.println(2_147_483_647);
        System.out.println(-2_147_483_648);
        System.out.println(9_223_372_036_854_775_807L);
        System.out.println(-9_223_372_036_854_775_808L);
        System.out.println(3.141_592_653_571_8);
    }

}
