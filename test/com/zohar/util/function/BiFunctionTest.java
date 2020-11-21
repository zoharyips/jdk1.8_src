package com.zohar.util.function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <h3></h3>
 *
 * @author zohar
 * @version 1.0
 * 2020/11/21 8:55
 */
class BiFunctionTest {

    BiFunction<HashMap<String, String>, String, HashMap<String, String>> subManByValue;

    @BeforeEach
    void setUp() {
        subManByValue = (map, requiredValue) -> {
            HashMap<String, String> result = new HashMap<>();
            map.forEach((key, value) -> {
                if (Objects.equals(value, requiredValue)) {
                    result.put(key, value);
                }
            });
            return result;
        };
    }

    @Test
    void apply() {
        HashMap<String, String> map = new HashMap<String, String>() {{
            put("one",   "zohar");
            put("two",   "hello");
            put("three", "zohar");
            put("four",  "hi");
            put("five",  "zohar");
        }};

        HashMap<String, String> zoharValueMap = subManByValue.apply(map, "zohar");
        zoharValueMap.forEach((key, value) -> System.out.println(value + " => " + key));
    }

    @Test
    void andThen() {
        BiFunction<String, String, String> myBiFunction = (a, b) -> a + b;
        Function<String, String> myFunction1 = a -> "Hello " + a;
        Function<String, String> myFunction2 = a -> "I wanna say: " + a;
        System.out.println(myBiFunction.andThen(myFunction1).andThen(myFunction2).apply("wor", "ld"));
    }
}