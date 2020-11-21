/*
 * Copyright (c) 1994, 2013, Oracle and/or its affiliates. All rights reserved.
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

/**
 * <h3>可运行接口</h3>
 *
 * 用于说明本接口实例是可运行的，和要求对象必须具备 run 行为。<br/><br/>
 *
 * 一个实现本接口的对象可以不必继承 Thread 类就可以使自身能够运行，只需要将自身作为 Thread
 * 实例的参数就行。除非用户希望自定义或增强 Thread 类的功能，否则都应该仅继承本接口来实现多线程运行。<br/><br/>
 *
 * 本接口添加函数式接口注解，声明本接口是一个函数式接口，
 *
 * @author  Arthur van Hoff
 * @see     java.lang.Thread
 * @see     java.util.concurrent.Callable
 * @since   JDK1.0
 */
@FunctionalInterface
public interface Runnable {

    /**
     * <h3>对象独立执行的方法</h3>
     *
     * 当用户实现本接口，投入并启动一个新线程后，将在新线程执行本方法。
     *
     * @see     java.lang.Thread#run()
     */
    void run();
}
