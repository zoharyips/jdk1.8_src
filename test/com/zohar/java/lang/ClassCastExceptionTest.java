package com.zohar.java.lang;

import org.junit.jupiter.api.Test;

/**
 * <h3>类型转化异常测试类</h3>
 *
 * @author zohar
 * @version 1.0
 * 2020/11/16 0:50
 */
class ClassCastExceptionTest {


    /**
     * null 可以转化为任何类型且不会抛出异常
     */
    @Test
    void nullCastTest() {
        Cat cat = (Cat) null;
    }

    private static class Cat {}

}