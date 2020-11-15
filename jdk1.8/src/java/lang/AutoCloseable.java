/*
 * Copyright (c) 2009, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java.lang;

import java.io.Closeable;

/**
 * <h3>AutoCloseable</h3>
 *
 * 使用 try-with-resources 语法可以在 try-catch 代码块内容执行完之后自动调用 resources
 * 的 close 方法来释放 resources 占有的资源。
 *
 * @apiNote 接口强制规范了实现类的行为：close()，用于释放资源类占有的资源。
 *
 * @author Josh Bloch
 * @since 1.7
 */
public interface AutoCloseable {
    /**
     * <h3>释放资源类的资源</h3>
     *
     * try-with-resources 声明中的资源类对象会在代码执行结束后自动调用本方法。<br/><br/>
     *
     * 虽然接口规定方法抛出异常的类型为 Exception，但建议子类对异常进行细化，
     * 甚至取消抛出异常以表示该方法执行的健壮性。<br/>
     * 强烈建议实现者在抛出异常之前，释放所有资源，同时将对象中的资源标志更改为已释放。<br/><br/>
     *
     * 本方法抛出的异常并不会覆盖 try-with-resources 代码块中抛出的异常，
     * 二者都将被保留在 StackTraces 中，同时本异常会被注明为被抑制的异常，
     * 通过调用 getSuppressed 方法可以访问到它。<br/><br/>
     *
     * 同时希望实现者不要再此方法中抛出 InterruptedException，
     * 中断异常发生的时候如果被抑制，运行时环境可能会出现错误的行为。<br/><br/>
     *
     * 与 java.io.Closeable 中的 close 方法不同的是，本接口并没有规定本方法是幂等的。
     * 也就是说，多次调用本接口可能会出现明显的副作用。但还是推荐实现者尽量把本接口实现成幂等的。
     *
     * @throws Exception 但资源关闭发生异常，必要时会抛出异常
     * @see Throwable#getSuppressed()
     * @see Closeable#close()
     */
    void close() throws Exception;
}
