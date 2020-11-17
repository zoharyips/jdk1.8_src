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

    private static final MainTest INSTANCE = new MainTest();

    @Test
    void methodForTest() {
        System.out.println("HELLO Method for test");
    }

    public static void main(String[] args) {

        new MyThread().start();
        new MyThread().start();
        new MyThread().start();

    }

    private static class MyThread extends Thread {
        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                INSTANCE.work(this.getName(), i);
            }
        }
    }

    private void work(String threadName, int times) {
        System.out.println(threadName + " start working: " + System.currentTimeMillis() + " => " + times);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(threadName + " work finished: " + System.currentTimeMillis() + " => " + times);
    }
}