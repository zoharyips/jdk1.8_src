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
 * <h3>守护器</h3>
 *
 * 守护器用户守护某个对象。<br/><br/>
 *
 * 守护器封装了两个实例，一个是被守护的对象，一个是对象的守卫。
 * 之后调用 getObject 方法才可以访问被守护对象，getObject 会调用守卫的
 * checkGuard 询问守卫是否可以获取被守护对象，如果不可以，将抛出异常。
 *
 * @see Guard
 * @see Permission
 *
 * @author Roland Schemers
 * @author Li Gong
 */

public class GuardedObject implements java.io.Serializable {

    private static final long serialVersionUID = -5240450096227834308L;

    @SuppressWarnings("FieldMayBeFinal")
    private Object object; // the object we are guarding
    @SuppressWarnings("FieldMayBeFinal")
    private Guard guard;   // the guard

    /**
     * <h3>创建守护器</h3>
     *
     * 如果设置守卫为 null，则守护器形同虚设。
     *
     * @param object 被守护对象
     * @param guard 守卫
     */
    public GuardedObject(Object object, Guard guard) {
        this.guard = guard;
        this.object = object;
    }

    /**
     * <h3>获取被守护对象</h3>
     *
     * 将调用守卫的 checkGuard 方法判断是否允许获取该对象
     *
     * @return 被守护对象
     *
     * @exception SecurityException 若守卫拒绝获取该对象
     */
    public Object getObject() throws SecurityException {
        if (guard != null)
            guard.checkGuard(object);

        return object;
    }

    /**
     * Writes this object out to a stream (i.e., serializes it).
     * We check the guard if there is one.
     */
    private void writeObject(java.io.ObjectOutputStream oos) throws java.io.IOException {
        if (guard != null)
            guard.checkGuard(object);

        oos.defaultWriteObject();
    }
}
