package com.zohar;

import org.junit.jupiter.api.Test;

import java.util.Date;

/**
 * <br/>
 *
 * @author zohar
 * @version 1.0
 * 2020/11/15 8:28
 */
class MainTest {

    public static void main(String[] args) {

        MyInterface.test();

        System.out.println(MYClass.test);

    }

    interface MyInterface {

        String test = "GOOD";
        String NAME = "HELLOO";
        static void test() {
            System.out.println("Static Method In Interface");
        }
        default void test2() {
            System.out.println("GOOD");
        }

    }

    static abstract class AbsClassOne {

        abstract void test();
    }

    static abstract class AbsClassTwo extends AbsClassOne {

    }

    static class MYClass implements MyInterface {

    }
}