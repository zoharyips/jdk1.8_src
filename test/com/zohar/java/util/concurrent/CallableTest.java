package com.zohar.java.util.concurrent;

import com.zohar.util.PrintUtil;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <h3>Callable 测试类</h3>
 *
 * @author zohar
 * @version 1.0
 * 2020/11/17 23:54
 */
class CallableTest {

    @Test
    void call() throws Exception {
        PrintUtil.printMethodName();
        Callable<String> callable = () -> Thread.currentThread().getName();
        // 直接调用 Callable 的 call 方法属于串行同步执行
        System.out.println(callable.call());
        System.out.println(Thread.currentThread().getName());
        // 丢入线程池即可并行执行
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> future = executorService.submit(callable);
        System.out.println(future.get());

        Callable<String> sleepCallable = () -> {
            try {
                Thread.sleep(1000);
                System.out.println(Thread.currentThread().getName());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return Thread.currentThread().getName();
        };
        // 如果不调用 future 的 get 操作就不会阻塞
        Future<String> submit = executorService.submit(sleepCallable);
        System.out.println(submit.isDone());
        System.out.println("Test whether block or not");
        Thread.sleep(1000);
    }
}