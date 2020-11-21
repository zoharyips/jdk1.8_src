/*
 * Copyright (c) 1997, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java.security;

/**
 * <h3>守卫接口</h3>
 *
 * 用于说明本接口实例是是一个守卫，要求实例必须具备 checkGuard 行为，
 * 用于守护某一个实例对象。<br/><br/>
 *
 * checkGuard 方法仅有一个 Object 类型参数 object，用于检查该 object
 * 是否允许被访问，当调用 GuardedObject 的 getObject() 方法时调用本方法。

 * @see GuardedObject
 *
 * @author Roland Schemers
 * @author Li Gong
 */

public interface Guard {

    /**
     * <h3>判断对象是否可使用</h3>
     *
     * 判断是否可以获取该 object 对象，如果可以则静默返回，
     * 若不可以则抛出异常。
     *
     * @param object 被守卫保护的对象
     *
     * @exception SecurityException 若守卫不允许访问该对象
     *
     */
    void checkGuard(Object object) throws SecurityException;
}
