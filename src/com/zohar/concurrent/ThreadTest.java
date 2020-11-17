package com.zohar.concurrent;

import java.util.Arrays;

/**
 * <h3>Thread 测试类</h3>
 *
 * @author zohar
 * @version 1.0
 * 2020/11/17 23:07
 */
public class ThreadTest {
    public static void main(String[] args) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        Arrays.stream(stackTrace).forEach(System.out::println);
    }
}
