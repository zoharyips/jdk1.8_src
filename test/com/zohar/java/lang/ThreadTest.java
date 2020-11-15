package com.zohar.java.lang;

import org.junit.jupiter.api.Test;

/**
 * <h3>Thread 测试类</h3>
 * Simple tests for Thread class
 *
 * @author zohar
 * @version 1.0
 * 2020/11/15 15:55
 */
public class ThreadTest {

    private static final Object lock = new Object();

    /**
     * 测试睡眠时的中断处理
     */
    @Test
    void interruptTest() {
        Thread mainThread = Thread.currentThread();
        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mainThread.interrupt();
        }).start();
        synchronized (lock) {
            System.out.println("Main Thread ready to wait, now is " + System.currentTimeMillis());
            try {
                lock.wait(2000);
                System.out.println("Main Thread wait timeout, now is " + System.currentTimeMillis());
            } catch (InterruptedException e) {
                System.out.println("Main Thread was interrupted, now is " + System.currentTimeMillis());
            }
        }
        System.out.println("Main Thread release the lock");
    }
}
