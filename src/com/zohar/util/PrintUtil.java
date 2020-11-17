package com.zohar.util;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * <h3>打印工具类</h3>
 *
 * @author zohar
 * @version 1.0
 * 2020/11/17 22:25
 */
public class PrintUtil {

    /**
     * 打印当前方法名
     */
    public static void printMethodName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (stackTrace.length > 3) {
            String isRunning = " is Running: ";
            //noinspection StringBufferMayBeStringBuilder
            StringBuffer divider = new StringBuffer();
            for (int i = 0; i < stackTrace[2].toString().length() + isRunning.length(); i++) {
                divider.append("=");
            }
            String dividerStr = divider.toString();
            System.out.println(dividerStr);
            System.out.println(stackTrace[2] + isRunning);
            System.out.println(dividerStr);
        }
    }
}
