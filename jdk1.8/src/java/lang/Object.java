/*
 * Copyright (c) 1994, 2012, Oracle and/or its affiliates. All rights reserved.
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
 * <h3>所有类的父类</h3>
 *
 * 是包括数组在内的所有类的父类，位于类继承结构上的根节点
 *
 * @author  unascribed
 * @see     java.lang.Class
 * @since   JDK1.0
 */
@SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
public class Object {

    /**
     * <h3>注册链接本类中的其他本地方法</h3>
     *
     * 调用本方法时，会将本类中其他的本地方法符号引用链接到本地方法区已加载的本地方法入口地址上。
     * 当本地方法更新时，可以重新调用本方法来更新所有类方法的链接地址。<br/><br/>
     *
     * 在没有调用本方法的情况下，在调用其他本地方法时，虚拟机会在本地方法区中定位并链接该方法到本类实例上。
     * 如果调用本方法，则可以一次性链接所有类中的本地方法，减少调用时链接操作消耗的时间。
     */
    private static native void registerNatives();

    static {
        registerNatives();
    }

    /**
     * <h3>获取运行时该对象所属的类</h3>
     *
     * @return 运行时对象所属的类
     */
    public final native Class<?> getClass();

    /**
     * <h3>判断另一个对象是否与本对象相等</h3>
     *
     * 默认比较两个对象是否指向同一个实例，即 a == b。如果类的每个实例只与自身相等，此时没有必要重写本方法，包括以下情景：
     * <li>类每个实例本质是唯一的或类并不需要实例化，如 Thread 和工具类。</li>
     * <li>类不需要提供“逻辑相等”的功能，如 Pattern，我们基本上并不需要去比较两个正则表达式是否逻辑相同。</li>
     * <li>超类已经覆盖了本方法，且超类的覆盖逻辑对本类是适用的，如 ArrayList 使用 List 的 equals 方法即可。</li>
     * <li>可以确定该类的 equals 方法无法被调用，如类是私有的或包级私有的，或者是用这种方法覆盖：
     * <blockquote>
     * <pre>
     * public boolean equals(Object o) {
     *     throw new AssertionError();
     * }</pre></blockquote>
     *
     * 当类对象存在<strong>逻辑相等</strong>概念的时候，需要重写本方法，如 String。重写本方法时需要涵盖能代表对象状态的所有域，
     * 这些域被称为状态元组，同时需要保证以下性质：
     * <li>自反性：x.equals(x) = true</li>
     * <li>对称性：x.equals(y) = y.equals(x)</li>
     * <li>传递性：x.equals(z) = (x.equals(y) && y.equals(z))</li>
     * <li>一致性：x.equals(y) ≡ x.equals(y)</li>
     * <li>非空性：x.equals(null) = false, x != null</li>
     * <br/>
     * 重写本方法的逻辑时必须涵盖本类所有的状态元组，生成对象的信息摘要也需要涵盖所有状态元组，因此重写本方法
     * 必须重写 hashCode 方法。
     *
     * @param   obj   the reference object with which to compare.
     * @return  {@code true} if this object is the same as the obj
     *          argument; {@code false} otherwise.
     * @see     java.util.HashMap
     */
    public boolean equals(Object obj) {
        return (this == obj);
    }

    /**
     * <h3>获取对象的哈希值</h3>
     *
     * 相当于将复杂的对象转化为整型的哈希摘要，计算哈希值所用到的信息必须与判断 equals() 所用到的状态元组一致。
     * 重写本方法须遵守以下规范：<br/>
     * <li>在一次程序运行期间，如果对象 equals() 方法中所比较的信息没有被修改，那么对该对象多次调用 hashCode()
     * 方法，必须返回相同的值。</li>
     * <li>如果两个对象根据 equals() 比较的结果是相同的，那本方法必须产生同样的整型结果</li>
     * <li>如果两个对象根据 equals() 比较的结果不同，本方法可以产生同样的结果，但应尽量避免这么做</li>
     *
     * @return  a hash code value for this object.
     * @see     java.lang.Object#equals(java.lang.Object)
     * @see     java.lang.System#identityHashCode
     */
    public native int hashCode();

    /**
     * <h3>创建并返回对象的拷贝</h3>
     *
     * 拷贝的过程取决于对象所属类的实现，拷贝是实例构造方式的另一种实现，这是一种非常规的构造方式，请谨慎地判断是否使用此方式创建对象，
     * 对于拷贝我们需要保证：
     * <ol>
     *     <li>拷贝对象与原对象类型相同</li>
     *     <li>拷贝对象与原对象独立，对拷贝对象的操作不会伤害原对象。</li>
     *     <li>拷贝对象的状态必须与原对象保持一致。</li>
     * </ol>
     *
     * 按照惯例，本方法应当调用 super.clone 方法来获取当前实例。如果在实现本方法时使用重新构造对象的方式
     * 进行克隆，会影响本类的子类 clone 方法的正常工作，使子类调用 super.clone 只能获得本类对象而无法获得
     * 相应的子类对象。<br/><br/>
     *
     * 按照惯例，克隆出的对象必须与原对象相互独立，但 clone 方法的默认实现是浅复制。对于原语类型而言任何复制方式
     * 都无关紧要，但对于引用类型而言，浅复制意味着克隆对象与原对象共享数据成员，这将导致引用泄露。因此，在使用到
     * clone 时，务必考虑重写本方法进行深复制以保证原对象的安全。<br/><br/>
     *
     * 对于不可变对象，请务必不要使用本方法，因为不可变对象没有必要进行拷贝。若对象拥有 final 域，请
     * 谨慎决定拷贝的方式或者放弃将该域设置为 final，甚至放弃对该对象的拷贝。
     * 因为使用 super.clone 返回的是对象的浅拷贝，而 final 域无法重新修改，因此拷贝对象和原对象共享
     * final 域成员，这会造成引用泄露。但如果使用重新构造的方式实现克隆，却会使得子类无法正常使用
     * super.clone。这个问题的本质是<strong>Cloneable 架构与引用可变对象的 final 域的正常用法
     * 是不兼容的</strong>。因此我们应该避免为拥有 final 域的对象进行克隆操作。
     *
     * 如果重写本方法存在诸多问题且必须提供拷贝手段，那么可以考虑实现一个拷贝构造器或拷贝工厂方法而放弃重写本方法：
     * <blockquote>
     * <pre>
     * public MyObject(MyObject o){};
     * public static MyObject newInstance(MyObject o){};
     * public static TreeSet newSet(HashSet set){};
     * </pre></blockquote>
     *
     * @return 当前实例的拷贝
     * @throws CloneNotSupportedException 若未实现 Cloneable 接口将会抛出本异常。
     *                                    如果实现本方法，同时将本方法的可见性提至 public，
     *                                    请在此方法中处理异常而非抛出异常，因为公有方法应尽量少地抛出受检异常。
     * @see java.lang.Cloneable
     */
    protected native Object clone() throws CloneNotSupportedException;

    /**
     * <h3>返回对象的字符串表示</h3>
     *
     * 通常，本方法会返回对象的文本表示，返回的结果应当是简明扼要的。当对象被直接传递给 println、
     * printf、字符串串联操作等时，会自动调用本方法将对象转化为字符串表示。<br/><br/>
     *
     * 本方法的默认实现是返回对象的类名和哈希值的十六位表示，如 object@1a2b3c4d。因此推荐所有
     * 类都重写本方法，这样可以使得代码更易于调试。
     *
     * @return 对象的字符串表示
     */
    public String toString() {
        return getClass().getName() + "@" + Integer.toHexString(hashCode());
    }

    /**
     * <h3>唤醒在本对象的锁上休眠的某个线程</h3>
     *
     * 线程通过调用本对象的 wait 方法进入阻塞睡眠且等待唤醒，如果有多个线程正在，则随机选择一个线程唤醒。尽管调用 notify 方法，在当前线程放弃对当前对象的锁之前，
     * 其他线程也无法获取本对象的锁。<br/><br/>
     *
     * 线程可以通过以下方法获取对象的锁：
     * <ol>
     *     <li>执行本对象的同步方法</li>
     *     <li>执行在此对象上同步的代码块</li>
     *     <li>对于类类型对象，执行该类上的同步静态方法</li>
     * </ol>
     *
     * @throws IllegalMonitorStateException 如果当前线程没有获得此对象的锁，将抛出此异常。
     * @see java.lang.Object#notifyAll()
     * @see java.lang.Object#wait()
     */
    public final native void notify();

    /**
     * <h3>唤醒在本对象的锁上休眠的所有线程</h3>
     *
     * 线程通过调用本对象的 wait 方法进入阻塞睡眠且等待唤醒。尽管调用 notifyAll 方法，在当前线程放弃对当前对象的锁之前，其他线程也无法获取本对象的锁。<br/><br/>
     *
     * 线程可以通过以下方法获取对象的锁：
     * <ol>
     *     <li>执行本对象的同步方法</li>
     *     <li>执行在此对象上同步的代码块</li>
     *     <li>对于类类型对象，执行该类上的同步静态方法</li>
     * </ol>
     *
     * @throws IllegalMonitorStateException 如果当前线程没有获得此对象的锁，将抛出此异常
     * @see java.lang.Object#notify()
     * @see java.lang.Object#wait()
     */
    public final native void notifyAll();

    /**
     * <h3>使线程让出此对象的锁并在本对象的锁上进行指定时间的睡眠</h3>
     *
     * 当线程调用本对象此方法时，会释放本对象的锁并进入睡眠，在其他线程调用 notify 或 notifyAll
     * 方法和到达指定睡眠时间之前，本线程都将保持阻塞状态。如果其他线程调用当前线程的 interrupt 方法，
     * 会给当前线程发出一个中断，此时将抛出 InterruptedException 触发本线程中断处理程序，但此时线程
     * 依旧需要在拿回本对象的锁之后才能执行中断处理程序。<br/><br/>
     *
     * 调用本方法之前必须获得本对象的锁，且调用本方法时线程会马上释放锁同时进入睡眠，在被唤醒
     * 的时候线程会进入就绪队列，在抢夺得锁的时候才能进入运行。<br/><br/>
     *
     * 一个线程也可以在没有被通知、中断或者超时的情况下被唤醒，即所谓的伪唤醒。虽然这种情况极少发生，但
     * 应用程序必须测试线程的唤醒条件，并在条件不满足时继续触发等待来防止出现这种情况。像：
     * <blockquote>
     * <pre>
     *     synchronized (obj) {
     *         while (&lt;condition does not hold&gt;)
     *             obj.wait(timeout);
     *         ... // Perform action appropriate to condition
     *     }</pre></blockquote>
     *
     * 当线程调用本对象的 wait 方法时，如果线程还持有其他对象的锁，此时线程只会释放此对象的锁，不会释放
     * 其他对象的锁
     *
     * @param timeout 线程等待的最长时间，超过该时间线程进入就绪队列参与锁的竞争。但参数是 0 时，表示
     *                不设置超时时间，线程只能依靠其他线程唤醒。
     * @throws  IllegalArgumentException 若 timeout 设置为负数将抛出此异常
     * @throws  IllegalMonitorStateException 若当前线程并未持有此对象的锁将抛出此异常
     * @throws  InterruptedException 如果其他线程在 wait 期间调用本线程的 interrupt 方法向
     *                               本线程发出中断，则会在 wait 方法中抛出此异常让本线程退出
     *                               睡眠状态来执行中断处理，但如果拿不回当前对象的锁，线程依旧
     *                               会被阻塞着。
     * @see java.lang.Object#notify()
     * @see java.lang.Object#notifyAll()
     * @see Thread#interrupt()
     */
    public final native void wait(long timeout) throws InterruptedException;

    /**
     * <h3>使线程让出此对象的锁并在本对象的锁上进行指定时间的睡眠</h3>
     *
     * 只要设置了纳秒参数并且在 0 到 1 纳秒之间，就调用 wait(timeout + 1) 进行睡眠，
     * 即无法细化到纳秒维度执行睡眠，纳秒的作用仅仅只是睡多一毫秒罢了。<br/><br/>
     *
     * 当线程调用本对象此方法时，会释放本对象的锁并进入睡眠，在其他线程调用 notify 或 notifyAll
     * 方法和到达指定睡眠时间之前，本线程都将保持阻塞状态。如果其他线程调用当前线程的 interrupt 方法，
     * 会给当前线程发出一个中断，此时将抛出 InterruptedException 触发本线程中断处理程序，但此时线程
     * 依旧需要在拿回本对象的锁之后才能执行中断处理程序。<br/><br/>
     *
     * 调用本方法之前必须获得本对象的锁，且调用本方法时线程会马上释放锁同时进入睡眠，在被唤醒
     * 的时候线程会进入就绪队列，在抢夺得锁的时候才能进入运行。<br/><br/>
     *
     * 一个线程也可以在没有被通知、中断或者超时的情况下被唤醒，即所谓的伪唤醒。虽然这种情况极少发生，但
     * 应用程序必须测试线程的唤醒条件，并在条件不满足时继续触发等待来防止出现这种情况。像：
     * <blockquote>
     * <pre>
     *     synchronized (obj) {
     *         while (&lt;condition does not hold&gt;)
     *             obj.wait(timeout);
     *         ... // Perform action appropriate to condition
     *     }</pre></blockquote>
     *
     * 当线程调用本对象的 wait 方法时，如果线程还持有其他对象的锁，此时线程只会释放此对象的锁，不会释放
     * 其他对象的锁
     *
     * @param timeout 线程等待的最长时间，超过该时间线程进入就绪队列参与锁的竞争。但参数是 0 时，表示
     *                不设置超时时间，线程只能依靠其他线程唤醒。
     * @param nanos 额外的纳秒，介于 0-999999，但只要大于 0 无论多少作用都是让 timeout++。
     * @throws  IllegalArgumentException      if the value of timeout is
     *                      negative or the value of nanos is
     *                      not in the range 0-999999.
     * @throws  IllegalMonitorStateException  if the current thread is not
     *               the owner of this object's monitor.
     * @throws  InterruptedException if any thread interrupted the
     *             current thread before or while the current thread
     *             was waiting for a notification.  The <i>interrupted
     *             status</i> of the current thread is cleared when
     *             this exception is thrown.
     */
    public final void wait(long timeout, int nanos) throws InterruptedException {
        if (timeout < 0) {
            throw new IllegalArgumentException("timeout value is negative");
        }

        if (nanos < 0 || nanos > 999999) {
            throw new IllegalArgumentException("nanosecond timeout value out of range");
        }

        if (nanos > 0) {
            timeout++;
        }

        wait(timeout);
    }

    /**
     * <h3>使线程让出此对象的锁并在本对象的锁上进行睡眠</h3>
     *
     * 调用 wait(0) 进行永久性睡眠。<br/><br/>
     *
     * 当线程调用本对象此方法时，会释放本对象的锁并进入睡眠，在其他线程调用 notify 或 notifyAll
     * 方法和到达指定睡眠时间之前，本线程都将保持阻塞状态。如果其他线程调用当前线程的 interrupt 方法，
     * 会给当前线程发出一个中断，此时将抛出 InterruptedException 触发本线程中断处理程序，但此时线程
     * 依旧需要在拿回本对象的锁之后才能执行中断处理程序。<br/><br/>
     *
     * 调用本方法之前必须获得本对象的锁，且调用本方法时线程会马上释放锁同时进入睡眠，在被唤醒
     * 的时候线程会进入就绪队列，在抢夺得锁的时候才能进入运行。<br/><br/>
     *
     * 一个线程也可以在没有被通知、中断或者超时的情况下被唤醒，即所谓的伪唤醒。虽然这种情况极少发生，但
     * 应用程序必须测试线程的唤醒条件，并在条件不满足时继续触发等待来防止出现这种情况。像：
     * <blockquote>
     * <pre>
     *     synchronized (obj) {
     *         while (&lt;condition does not hold&gt;)
     *             obj.wait(timeout);
     *         ... // Perform action appropriate to condition
     *     }</pre></blockquote>
     *
     * 当线程调用本对象的 wait 方法时，如果线程还持有其他对象的锁，此时线程只会释放此对象的锁，不会释放
     * 其他对象的锁
     *
     * @throws  IllegalArgumentException 若 timeout 设置为负数将抛出此异常
     * @throws  IllegalMonitorStateException 若当前线程并未持有此对象的锁将抛出此异常
     * @throws  InterruptedException 如果其他线程在 wait 期间调用本线程的 interrupt 方法向
     *                               本线程发出中断，则会在 wait 方法中抛出此异常让本线程退出
     *                               睡眠状态来执行中断处理，但如果拿不回当前对象的锁，线程依旧
     *                               会被阻塞着。
     * @see java.lang.Object#notify()
     * @see java.lang.Object#notifyAll()
     * @see Thread#interrupt()
     */
    public final void wait() throws InterruptedException {
        wait(0);
    }

    /**
     * <h3>在对象销毁前执行一些操作</h3>
     *
     * GC 在对本对象进行回收时会执行本方法，用于做一些善后的工作。但本方法执行优先级极低，因此无法保证会被及时执行。
     * 因此请不要把本方法当作为与 C++ 析构函数类似的方法。如果要实现对对象所占用资源的释放，
     * 必须在使用完毕时使用 try-finally 或 try-with-resource 进行立刻释放，而不能依赖这个执行不确定的方法进行释放。<br/><br/>
     *
     * 同时，永远不要在本方法中尝试挽救本对象，因为本方法的执行时间是不确定的，而且这么做违反了程序设计原则。<br/><br/>
     *
     * 本方法存在非常严重的性能损失，且存在终结方法攻击的风险，因此最好的办法是不重写本方法，或者是仅仅将本方法置为
     * final 方法，以防止恶意子类使用终结方法攻击。<br/><br/>
     *
     * 本方法依旧有两个合法用途：
     * <ol>
     *     <li>充当安全网释放对象占用的资源，防止用户忘记调用 close 方法，虽然本方法可能在 OOM 时还没被调用过。</li>
     *     <li>
     *         用于关闭本地对等体，本队对等体是使用或拥有本对象的一个 native 对象，GC 无法直到它的存在，
     *         可以通过本方法来释放那些必须终止资源的本地对等体
     *     </li>
     * </ol>
     *
     * @throws Throwable 任何实现代码可能抛出的异常
     * @see java.lang.ref.WeakReference
     * @see java.lang.ref.PhantomReference
     */
    @SuppressWarnings("RedundantThrows")
    protected void finalize() throws Throwable { }
}
