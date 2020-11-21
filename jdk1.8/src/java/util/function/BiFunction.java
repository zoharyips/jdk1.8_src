/*
 * Copyright (c) 2010, 2013, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package java.util.function;

import java.util.Objects;

/**
 * <h3>双参函数</h3>
 *
 * 表示接受两个参数并生成结果的函数。这就是 {@link Function} 的二元特化。
 *
 * @param <T> 第一个参数的类型
 * @param <U> 第二个参数的类型
 * @param <R> 返回结果类型
 *
 * @see Function
 * @since 1.8
 */
@FunctionalInterface
public interface BiFunction<T, U, R> {

    /**
     * <h3>应用本函数</h3>
     *
     * @param t 第一个参数
     * @param u 第二个参数
     * @return 返回值
     */
    R apply(T t, U u);

    /**
     * <h3>执行后调用指定函数</h3>
     *
     * @param <V> after 函数的返回值类型，也是本双参函数的返回值类型
     * @param after 应用本函数之后要执行的 after 函数
     * @return 一个执行完本函数之后会调用 after 函数的复合函数
     * @throws NullPointerException 当 after 函数为空时
     */
    default <V> BiFunction<T, U, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T t, U u) -> after.apply(apply(t, u));
    }
}
