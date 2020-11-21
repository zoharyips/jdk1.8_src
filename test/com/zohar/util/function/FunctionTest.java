package com.zohar.util.function;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.function.Function;

/**
 * <h3></h3>
 *
 * @author zohar
 * @version 1.0
 * 2020/11/21 9:28
 */
class FunctionTest {

    @Test
    void compose() {
        Function<String, String> function = a -> "Hello! " + a;
        Function<String, String> headFunc = a -> "I'm " + a;
        String zoharStr = function.compose(headFunc).apply("zohar");
        System.out.println(zoharStr);
    }

    @Test
    void andThen() {
        Function<String, String> function = a -> "Hello! I'm " + a;
        Function<String, String> tailFunc = a -> a + " --on " + new Date();
        String zoharStr = function.andThen(tailFunc).apply("zohar");
        System.out.println(zoharStr);
    }

    @Test
    void identity() {
        Function<Object, Object> identity = Function.identity();
        System.out.println(identity.apply(42));
    }

    @Test
    void test() {
        Function<Object, Object> identity = Function.identity();
        Function<String, String> function = a -> "Hello! " + a;
        Function<Object, String> headFunc = a -> "I'm " + a;
        Function<String, String> tailFunc = a -> a + " --on " + new Date();
        String zoharStr = function.compose(headFunc).andThen(tailFunc).apply(identity.apply("zohar"));
        System.out.println(zoharStr);
    }
}