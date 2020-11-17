package com.zohar.java.lang;

import com.zohar.util.PrintUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <h3>Runnable 测试类</h3>
 *
 * @author zohar
 * @version 1.0
 * 2020/11/17 23:56
 */
class RunnableTest {

    @Test
    void run() throws InterruptedException {
        PrintUtil.printMethodName();
        System.out.println(Thread.currentThread().getName());
        Runnable runnable = () -> System.out.println(Thread.currentThread().getName());
        // 直接调用 Runnable 的 run 方法，属于同步执行
        runnable.run();
        // 将 Runnable 实例传入 Thread 才能异步执行
        new Thread(runnable).start();
        Runnable sleepRunnable = () -> {
            try {
                Thread.sleep(1000);
                System.out.println(Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        new Thread(sleepRunnable).start();
        // 不会阻塞
        System.out.println("Test whether block or not");
        // 主线程等待其他线程执行完，因为 Junit 在主线程执行完就结束程序
        Thread.sleep(1100);
    }
}