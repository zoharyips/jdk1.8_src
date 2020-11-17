package com.zohar.grammer;

import com.zohar.util.PrintUtil;
import sun.applet.AppletResourceLoader;

import java.util.Comparator;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.DoubleToIntFunction;

/**
 * <h3>Lambda 表达式测试类</h3>
 *
 * @author zohar
 * @version 1.0
 * 2020/11/17 22:43
 */
public class Lambda {
    public static void main(String[] args) throws Exception {
        firstStep();
    }

    /**
     * <h3>使用 () 定义 Lambda 表达式</h3>
     * {@code ()} 中的内容为函数参数，{@code ->} 后的内容为函数返回值
     */
    private static void firstStep() throws Exception {
        PrintUtil.printMethodName();
        // Callable
        Callable<?> getNull = () -> null;
        System.out.println(getNull.call());
        Callable<Integer> getZero = () -> 0;
        System.out.println(getZero.call());
        // Consumer 为消费者，可以接收一个参数进行处理
        Consumer<String> printSth = System.out::println;
        printSth.accept("HELLO LAMBDA");
    }

}
