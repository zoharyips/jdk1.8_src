package com.zohar;

/**
 * <h3>类初始化测试</h3>
 *
 * @author zohar
 * @version 1.0
 * 2020/11/21 10:52
 */

public class ClassInit {

    public static void main(String[] args){
        f1();
    }

    static ClassInit classInit = new ClassInit();

    static {
        System.out.println("1");
    }

    {
        System.out.println("2");
    }

    ClassInit(){
        System.out.println("3");
        System.out.println("a=" + a + ", b=" + b);
    }

    public static void f1(){
        System.out.println("4");
    }

    int a = 100;
    static int b = 200;
}