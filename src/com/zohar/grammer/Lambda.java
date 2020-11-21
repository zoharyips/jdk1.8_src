package com.zohar.grammer;

import com.zohar.util.PrintUtil;
import javafx.util.Builder;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.*;

/**
 * <h3>Lambda 表达式测试类</h3>
 *
 * @author zohar
 * @version 1.0
 * 2020/11/17 22:43
 */
public class Lambda {

    private static final String HELLO_WORD = "HELLO LAMBDA!";

    public static void main(String[] args) throws Throwable {
        lambda();
    }

    /**
     * <h3>使用 () 定义 Lambda 表达式</h3>
     *
     * {@code ()} 中的内容为函数参数，{@code ->} 后的内容为函数返回值
     */
    private static void lambda() throws Throwable {
        PrintUtil.printMethodName();
        // Runnable 作为可运行者，不接受任何参数，不存在返回值
        Runnable runnable = () -> System.out.println("Runnable: " + HELLO_WORD);
        runnable.run();

        // Callable 作为可调用者，不接收任何参数，但存在返回值
        Callable<String> getZero = () -> "Callable: " + HELLO_WORD;
        System.out.println(getZero.call());

        // Supplier 作为提供者，不接受任何参数，存在返回值
        Supplier<String> getTure = () -> "Supplier: " + HELLO_WORD;
        System.out.println(getTure.get());

        // Builder 作为构建者，不接受任何参数，存在返回值
        Builder<String> builder = () -> "Builder: " + HELLO_WORD;
        System.out.println(builder.build());

        // Consumer 作为消费者，可以接收一个参数进行处理
        Consumer<String> printSth = System.out::println;
        printSth.accept("Consumer: " + HELLO_WORD);

        // BiConsumer 作为二阶消费者，可以接收两个不同类型参数进行处理
        BiConsumer<String, String> biConsumer = (a, b) -> System.out.println(a + b);
        biConsumer.accept("BiConsumer: ", HELLO_WORD);

        // Function 作为一阶函数，可以接收某类型参数，并返回另一类型参数
        Function<String, String> function = (a) -> a;
        System.out.println(function.apply("Function: " + HELLO_WORD));

        // BiFunction 作为二阶函数，可以接收两个不同类型参数，并返回第三种类型的值
        BiFunction<String, String, String> biFunction = (a, b) -> a + b;
        System.out.println(biFunction.apply("BiFunction: ", HELLO_WORD));
    }

    /**
     * <h3>方法引用</h3>
     */
    private static void methodReference() {
        PrintUtil.printMethodName();

        List<String> names = Arrays.asList(
                "Alan Turing",
                "Richard Stallman",
                "Edsger Dijkstra",
                "Claude Elwood Shannon",
                "Donald Knuth",
                "Andrew S. Tanenbaum",
                "Niklaus Wirth",
                "Tim Berners-Lee",
                "Marvin Minsky",
                "John Backus",
                "Edgar F. Codd",
                "Ken Thompson",
                "Linus Torvalds",
                "Jérôme Lalande"
        );

        Function<String, String> function = a -> "This is " + a;
        names.forEach(name -> System.out.println(function.apply(name)));

        // 静态方法引用

    }


}
