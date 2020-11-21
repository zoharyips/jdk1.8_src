package com.zohar.java.lang;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

/**
 * <h3></h3>
 *
 * @author zohar
 * @version 1.0
 * 2020/11/21 13:38
 */
class ThreadLocalTest {

    private static final ThreadLocal<MyObject> threadLocalOne = new ThreadLocal<>();
    private static final ThreadLocal<Object> threadLocalTwo = new ThreadLocal<>();

    private static class MyObject {
        int value = 10;

        @Override
        public String toString() {
            return super.toString() + "[value=" + value + "]";
        }
    }

    @Test
    public void test() throws InterruptedException {

        for (int i = 0; i < 3; i++) {
            final int finI = i;
            new Thread(() -> {
                threadLocalOne.set(new MyObject());
                System.out.println("Parent Thread: " + threadLocalOne.get());
                threadLocalOne.get().value = finI;

                new Thread(() -> System.out.println("SubThread: " + threadLocalOne.get())).start();
            }).start();
        }

        Thread.sleep(500);
    }

}