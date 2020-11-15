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
 * <h3>类型转化异常</h3>
 *
 * 再将对象转化为该对象子类类型实例时，如果转化类型错误，则抛出此异常
 *
 * @author  unascribed
 * @since   JDK1.0
 */
public
class ClassCastException extends RuntimeException {
    private static final long serialVersionUID = -9223365651070458532L;

    /**
     * Constructs a <code>ClassCastException</code> with no detail message.
     */
    public ClassCastException() {
        super();
    }

    /**
     * Constructs a <code>ClassCastException</code> with the specified
     * detail message.
     *
     * @param   s   the detail message.
     */
    public ClassCastException(String s) {
        super(s);
    }
}
