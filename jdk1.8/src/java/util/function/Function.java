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
 * <h3>单参函数</h3>
 *
 * 代表一个拥有一个参数和返回值的函数
 *
 * @param <T> 参数类型
 * @param <R> 返回值类型
 *
 * @since 1.8
 */
@FunctionalInterface
public interface Function<T, R> {

    /**
     * <h3>应用本函数</h3>
     *
     * @param t 函数参数
     * @return 函数返回值
     */
    R apply(T t);

    /**
     * <h3>返回一个执行本函数之前会执行指定函数的函数</h3>
     *
     * @param <V> before 函数的返回值类型，也是本函数的参数类型
     * @param before 执行本函数前执行的函数
     * @return 一个执行本函数之前会执行 before 函数的复合函数
     * @throws NullPointerException before 函数为空时
     *
     * @see #andThen(Function)
     */
    default <V> Function<V, R> compose(Function<? super V, ? extends T> before) {
        Objects.requireNonNull(before);
        return (V v) -> apply(before.apply(v));
    }

    /**
     * <h3>返回一个执行本函数之后会执行指定函数的 function</h3>
     *
     * @param <V> after 函数的返回值类型，也是本函数的返回值类型
     * @param after 执行本函数之后执行的函数
     * @return 一个执行完函数之后会调用 after 函数的复合函数
     * @throws NullPointerException after 函数为空时
     *
     * @see #compose(Function)
     */
    default <V> Function<T, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return (T t) -> after.apply(apply(t));
    }

    /**
     * <h3>创造一个永远返回输入参数的函数</h3>
     *
     * @param <T> 返回 function 的入参和返回类型
     * @return 一个永远返回入参的函数
     */
    static <T> Function<T, T> identity() {
        return t -> t;
    }
}
