/*
 * Copyright (c) 1994, 2016, Oracle and/or its affiliates. All rights reserved.
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

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.AccessControlContext;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.LockSupport;
import sun.nio.ch.Interruptible;
import java.nio.channels.InterruptibleChannel;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.Selector;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.security.util.SecurityConstants;


/**
 * A <i>thread</i> is a thread of execution in a program. The Java
 * Virtual Machine allows an application to have multiple threads of
 * execution running concurrently.
 * <p>
 * Every thread has a priority. Threads with higher priority are
 * executed in preference to threads with lower priority. Each thread
 * may or may not also be marked as a daemon. When code running in
 * some thread creates a new <code>Thread</code> object, the new
 * thread has its priority initially set equal to the priority of the
 * creating thread, and is a daemon thread if and only if the
 * creating thread is a daemon.
 * <p>
 * When a Java Virtual Machine starts up, there is usually a single
 * non-daemon thread (which typically calls the method named
 * <code>main</code> of some designated class). The Java Virtual
 * Machine continues to execute threads until either of the following
 * occurs:
 * <ul>
 * <li>The <code>exit</code> method of class <code>Runtime</code> has been
 *     called and the security manager has permitted the exit operation
 *     to take place.
 * <li>All threads that are not daemon threads have died, either by
 *     returning from the call to the <code>run</code> method or by
 *     throwing an exception that propagates beyond the <code>run</code>
 *     method.
 * </ul>
 * <p>
 * There are two ways to create a new thread of execution. One is to
 * declare a class to be a subclass of <code>Thread</code>. This
 * subclass should override the <code>run</code> method of class
 * <code>Thread</code>. An instance of the subclass can then be
 * allocated and started. For example, a thread that computes primes
 * larger than a stated value could be written as follows:
 * <hr><blockquote><pre>
 *     class PrimeThread extends Thread {
 *         long minPrime;
 *         PrimeThread(long minPrime) {
 *             this.minPrime = minPrime;
 *         }
 *
 *         public void run() {
 *             // compute primes larger than minPrime
 *             &nbsp;.&nbsp;.&nbsp;.
 *         }
 *     }
 * </pre></blockquote><hr>
 * <p>
 * The following code would then create a thread and start it running:
 * <blockquote><pre>
 *     PrimeThread p = new PrimeThread(143);
 *     p.start();
 * </pre></blockquote>
 * <p>
 * The other way to create a thread is to declare a class that
 * implements the <code>Runnable</code> interface. That class then
 * implements the <code>run</code> method. An instance of the class can
 * then be allocated, passed as an argument when creating
 * <code>Thread</code>, and started. The same example in this other
 * style looks like the following:
 * <hr><blockquote><pre>
 *     class PrimeRun implements Runnable {
 *         long minPrime;
 *         PrimeRun(long minPrime) {
 *             this.minPrime = minPrime;
 *         }
 *
 *         public void run() {
 *             // compute primes larger than minPrime
 *             &nbsp;.&nbsp;.&nbsp;.
 *         }
 *     }
 * </pre></blockquote><hr>
 * <p>
 * The following code would then create a thread and start it running:
 * <blockquote><pre>
 *     PrimeRun p = new PrimeRun(143);
 *     new Thread(p).start();
 * </pre></blockquote>
 * <p>
 * Every thread has a name for identification purposes. More than
 * one thread may have the same name. If a name is not specified when
 * a thread is created, a new name is generated for it.
 * <p>
 * Unless otherwise noted, passing a {@code null} argument to a constructor
 * or method in this class will cause a {@link NullPointerException} to be
 * thrown.
 *
 * @author  unascribed
 * @see     Runnable
 * @see     Runtime#exit(int)
 * @see     #run()
 * @see     #stop()
 * @since   JDK1.0
 */
public
class Thread implements Runnable {

    /**
     * <h3>注册本类中所有原生方法</h3>
     */
    private static native void registerNatives();
    static {
        registerNatives();
    }

    /**
     * 线程名称
     */
    private volatile String name;

    /**
     * 线程优先级
     */
    private int            priority;

    /**
     * ??
     */
    @SuppressWarnings({"FieldMayBeFinal", "unused"})
    private Thread         threadQ;

    /**
     * ??
     */
    @SuppressWarnings({"FieldMayBeFinal", "unused"})
    private long           eetop;

    /**
     * 是否单步执行
     */
    @SuppressWarnings({"FieldMayBeFinal", "unused"})
    private boolean     single_step;

    /**
     * 是否是守护线程
     */
    private boolean     daemon = false;

    /**
     * JVM state
     */
    @SuppressWarnings({"FieldMayBeFinal", "unused"})
    private boolean     stillborn = false;

    /**
     * 执行目标主体
     */
    private Runnable target;

    /**
     * 内部线程组
     */
    private ThreadGroup group;

    /**
     * 上下文类加载器
     */
    private ClassLoader contextClassLoader;

    /* The inherited AccessControlContext of this thread */
    @SuppressWarnings({"FieldMayBeFinal", "unused"})
    private AccessControlContext inheritedAccessControlContext;

    /**
     * 全局线程编号：用于自动编号匿名线程
     */
    private static int threadInitNumber;

    /**
     * <h3>获取当前全局线程编号并加一</h3>
     *
     * @return 当前全局线程编号
     */
    private static synchronized int nextThreadNum() {
        return threadInitNumber++;
    }

    /**
     * 本线程的 ThreadLocal 变量列表
     */
    ThreadLocal.ThreadLocalMap threadLocals = null;

    /*
     * InheritableThreadLocal values pertaining to this thread. This map is
     * maintained by the InheritableThreadLocal class.
     */
    ThreadLocal.ThreadLocalMap inheritableThreadLocals = null;

    /**
     * 本线程设置的栈大小，如果为 0 表示未设置，对于该值虚拟机所采取的行动取决于虚拟机的具体实现，有的虚拟机会忽略它
     */
    @SuppressWarnings({"FieldMayBeFinal", "unused", "FieldCanBeLocal"})
    private long stackSize;

    /*
     * JVM-private state that persists after native thread termination.
     */
    @SuppressWarnings({"FieldMayBeFinal", "unused"})
    private long nativeParkEventPointer;

    /**
     * 线程 ID
     */
    private long tid;

    /**
     * 全局线程序列号
     */
    private static long threadSeqNumber;

    /* Java thread status for tools,
     * initialized to indicate thread 'not yet started'
     */

    /**
     * 线程状态
     */
    @SuppressWarnings({"FieldMayBeFinal", "unused"})
    private volatile int threadStatus = 0;

    /**
     * <h3>全局线程序列号加一并放回</h3>
     *
     * @return 加一后的线程序列号
     */
    private static synchronized long nextThreadID() {
        return ++threadSeqNumber;
    }

    /**
     * The argument supplied to the current call to
     * java.util.concurrent.locks.LockSupport.park.
     * Set by (private) java.util.concurrent.locks.LockSupport.setBlocker
     * Accessed using java.util.concurrent.locks.LockSupport.getBlocker
     */
    volatile Object parkBlocker;

    /**
     * The object in which this thread is blocked in an interruptible I/O
     * operation, if any.  The blocker's interrupt method should be invoked
     * after setting this thread's interrupt status.
     */
    private volatile Interruptible blocker;
    private final Object blockerLock = new Object();

    /**
     * Set the blocker field; invoked via sun.misc.SharedSecrets from java.nio code
     */
    void blockedOn(Interruptible b) {
        synchronized (blockerLock) {
            blocker = b;
        }
    }

    /**
     * 最低优先级
     */
    public final static int MIN_PRIORITY = 1;

    /**
     * 默认优先级
     */
    public final static int NORM_PRIORITY = 5;

    /**
     * 最高优先级
     */
    public final static int MAX_PRIORITY = 10;

    /**
     * <h3>获取当前线程</h3>
     *
     * @return 执行当前代码的线程实例
     */
    public static native Thread currentThread();

    /**
     * <h3>让出 CPU</h3>
     *
     * 想调度器表示当前线程要让出 CPU 时间片，调度器是可以忽略这个信号的。
     * 线程主动让出 CPU 时间时并不会释放占有的锁<br/><br/>
     *
     * yield 是一种启发式尝试，旨在防止线程过度使用 CPU。应当尽量少使用这个方法，
     * 这个方法适用于测试和 Debug 时重现线程之间的执行顺序，但不适合真正的进程调度。
     */
    public static native void yield();

    /**
     * <h3>线程睡眠指定时间</h3>
     *
     * 让执行此方法的线程进行指定时间的睡眠，具体时间和精度取决于操作系统和调度器的实现。
     * 线程睡眠时不会释放所占有的锁
     *
     * @param  millis 睡眠毫秒数
     *
     * @throws  IllegalArgumentException 若参数为负数
     *
     * @throws  InterruptedException 当任何线程向当前线程发起中断时，
     *                               当前线程的状态会被声明为 interrupted status
     */
    public static native void sleep(long millis) throws InterruptedException;

    /**
     * <h3>线程睡眠指定时间</h3>
     *
     * 让执行此方法的线程进行指定时间的睡眠，具体时间和精度取决于操作系统和调度器的实现。
     * 线程睡眠时不会释放所占有的锁<br/><br/>
     *
     * 若 millis 为 0，只要 nanos 合法，其作用仅是将 millis 置为 1。
     * 若 millis 不为 0，只有 nanos 合法且大于 500000 时，millis 才会加一
     *
     * @param  millis 睡眠毫秒数
     * @param  nanos 睡眠纳秒数，0-999999 时 millis 加一
     * @throws  IllegalArgumentException 若 millis 为负数或 nanos 在 0-999999 范围之外
     * @throws  InterruptedException 当任何线程向当前线程发起中断时，
     *                               当前线程的状态会被声明为 interrupted status
     */
    public static void sleep(long millis, int nanos) throws InterruptedException {
        if (millis < 0) {
            throw new IllegalArgumentException("timeout value is negative");
        }

        if (nanos < 0 || nanos > 999999) {
            throw new IllegalArgumentException("nanosecond timeout value out of range");
        }

        if (nanos >= 500000 || (nanos != 0 && millis == 0)) {
            millis++;
        }

        sleep(millis);
    }

    /**
     * Initializes a Thread with the current AccessControlContext.
     * @see #init(ThreadGroup,Runnable,String,long,AccessControlContext,boolean)
     */
    private void init(ThreadGroup g, Runnable target, String name,
                      long stackSize) {
        init(g, target, name, stackSize, null, true);
    }

    /**
     * <h3>初始化线程</h3>
     *
     * @param g 线程组
     * @param target 线程执行主体
     * @param name 线程名称
     * @param stackSize 新线程的栈大小限制，设置为 0 则忽略此限制
     * @param acc 线程的访问控制上下文，若为空则调用 AccessController.getContext() 获取
     * @param inheritThreadLocals 若为 true，从原线程中继承 inheritable thread-locals 数据到当前线程的 inheritable thread-locals 中
     */
    private void init(ThreadGroup g, Runnable target, String name,
                      long stackSize, AccessControlContext acc,
                      boolean inheritThreadLocals) {
        if (name == null) {
            throw new NullPointerException("name cannot be null");
        }

        this.name = name;

        Thread parent = currentThread();
        SecurityManager security = System.getSecurityManager();
        if (g == null) {
            /* Determine if it's an applet or not */

            /* 如果有安全管理器，就问安全管理器有没有建议使用的线程组 */
            if (security != null) {
                g = security.getThreadGroup();
            }

            /* 如果安全管理器没什么建议，那就使用当前线程的线程组 */
            if (g == null) {
                g = parent.getThreadGroup();
            }
        }

        /* 无论线程组是否是传入的，都应该判断是否有权限访问 */
        g.checkAccess();

        /*
         * Do we have the required permissions?
         */
        if (security != null) {
            if (isCCLOverridden(getClass())) {
                security.checkPermission(SUBCLASS_IMPLEMENTATION_PERMISSION);
            }
        }

        g.addUnstarted();

        this.group = g;
        this.daemon = parent.isDaemon();
        this.priority = parent.getPriority();
        if (security == null || isCCLOverridden(parent.getClass()))
            this.contextClassLoader = parent.getContextClassLoader();
        else
            this.contextClassLoader = parent.contextClassLoader;
        this.inheritedAccessControlContext =
                acc != null ? acc : AccessController.getContext();
        this.target = target;
        setPriority(priority);
        if (inheritThreadLocals && parent.inheritableThreadLocals != null)
            this.inheritableThreadLocals = ThreadLocal.createInheritedMap(parent.inheritableThreadLocals);
        /* Stash the specified stack size in case the VM cares */
        this.stackSize = stackSize;

        /* Set thread ID */
        tid = nextThreadID();
    }

    /**
     * <h3>禁止克隆</h3>
     *
     * 不允许克隆线程，请使用 new 构建线程。
     *
     * @throws  CloneNotSupportedException 永远
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    /**
     * <h3>构建空线程</h3>
     *
     * 调用 init 方法初始化线程。<br/>
     * 相当于使用 {@code new Thread(null, null, null)} 调用
     * {@linkplain #Thread(ThreadGroup,Runnable,String) Thread}
     *
     * @apiNote 由于没有指定运行主体，线程运行时不做任何事。
     */
    public Thread() {
        init(null, null, "Thread-" + nextThreadNum(), 0);
    }

    /**
     * <h3>使用指定运行主体构建新线程</h3>
     *
     * 调用 init 方法初始化线程。<br/>
     * 相当于使用 {@code new Thread(null, target, null)} 调用
     * {@linkplain #Thread(ThreadGroup,Runnable,String) Thread}
     *
     * @param  target 运行主体，线程执行时将调用该对象的 run 方法。若为 null，线程执行时不做任何事。
     */
    public Thread(Runnable target) {
        init(null, target, "Thread-" + nextThreadNum(), 0);
    }

    /**
     * <h3>使用指定运行主体和访问控制上下文构建线程</h3>
     *
     * 包级私有方法，调用 init 方法初始化线程。<br/>
     * 使用指定 Runnable 对象最为线程运行主体。<br/>
     * 使用指定访问控制上下文作为线程的访问控制上下文。<br/>
     * 默认将当前线程名称设置为 ”Thread-“ + 全局编号，栈大小设置为 0；<br/>
     *
     * @param  target 运行主体，线程执行时将调用该对象的 run 方法。若为 null，线程执行时不做任何事。
     * @param acc 访问控制上下文
     */
    Thread(Runnable target, AccessControlContext acc) {
        init(null, target, "Thread-" + nextThreadNum(), 0, acc, false);
    }

    /**
     * <h3>使用指定线程组和运行主体构建线程</h3>
     *
     * 调用 init 方法初始化线程。<br/>
     * 相当于使用 {@code new Thread(group, target, null)} 调用
     * {@linkplain #Thread(ThreadGroup,Runnable,String) Thread}
     *
     * @param  group 线程组，若为 null 且系统安全管理器非空，则从安全管理器获取线程组，
     *               否则设置为当前线程的线程组。
     * @param  target 运行主体，线程执行时将调用该对象的 run 方法。若为 null，线程执行时不做任何事。
     *
     * @throws  SecurityException 若当前线程无法在该线程组内创建
     */
    public Thread(ThreadGroup group, Runnable target) {
        init(group, target, "Thread-" + nextThreadNum(), 0);
    }

    /**
     * <h3>创建指定名称的线程</h3>
     *
     * 调用 init 方法初始化线程。<br/>
     * 相当于使用 {@code new Thread(null, null, name)} 调用
     * {@linkplain #Thread(ThreadGroup,Runnable,String) Thread}
     *
     * @param   name 线程名称
     */
    public Thread(String name) {
        init(null, null, name, 0);
    }

    /**
     * <h3>使用指定线程组和线程名构建新线程</h3>
     *
     * 调用 init 方法初始化线程。<br/>
     * 相当于使用 {@code new Thread(group, null, name)} 调用
     * {@linkplain #Thread(ThreadGroup,Runnable,String) Thread}
     *
     * @param  group 线程组，若为 null 且系统安全管理器非空，则从安全管理器获取线程组，
     *               否则设置为当前线程的线程组。
     * @param  name 线程名
     *
     * @throws  SecurityException 若当前线程无法在该线程组中创建
     */
    public Thread(ThreadGroup group, String name) {
        init(group, null, name, 0);
    }

    /**
     * <h3>使用指定运行主体和线程名构建新线程</h3>
     *
     * 调用 init 方法初始化线程。<br/>
     * 相当于使用 {@code new Thread(null, target, name)} 调用
     * {@linkplain #Thread(ThreadGroup,Runnable,String) Thread}
     *
     * @param  target 运行主体，线程执行时将调用该对象的 run 方法。若为 null，线程执行时不做任何事。
     * @param  name 线程名
     */
    public Thread(Runnable target, String name) {
        init(null, target, name, 0);
    }

    /**
     * <h3>使用特定线程组、运行主体和线程名构建线程</h3>
     *
     * 线程在创建的时候会使用线程组的 checkAccess 方法判断该线程是否可以加入该线程组。<br/><br/>
     *
     * 当子类直接或间接重写了 getContextClassLoader 或 setContextClassLoader 方法时，
     * 在调用构造函数的时候，会调用 checkPermission 方法检查运行时权限：
     * enableContextClassLoaderOverride。<br/><br/>
     *
     * 新线程的优先级和父线程的优先级一致。只有在父线程是守护线程的时候，创建的线程会默认初始化为守护线程。<br/><br/>
     *
     * @param  group 线程组，若为 null 且系统安全管理器非空，则从安全管理器获取线程组，
     *               否则设置为当前线程的线程组。
     *
     * @param  target 运行主体，线程执行时将调用该对象的 run 方法。若为 null，线程执行时不做任何事。
     * @param  name 线程名
     * @throws  SecurityException 若当前线程无法在该线程组中创建
     */
    public Thread(ThreadGroup group, Runnable target, String name) {
        init(group, target, name, 0);
    }

    /**
     * <h3>根据线程组、执行主体、线程名和栈大小构建线程</h3>
     *
     * 调用 init 方法初始化线程。<br/>
     * 与 {@linkplain #Thread(ThreadGroup, Runnable, String) Thread(group, target, name)}
     * 完全相同，但是多了一个 stack 线程栈大小。stack 是线程运行时栈空间大小的近似字节数。
     * 该参数的实际效果高度取决于运行平台。<br/><br/>
     *
     * @apiNote 推荐 Java 平台开发者在使用文档中清楚的注明 stackSize 参数在该平台的作用
     *
     * @param  group 线程组，若为 null 且系统安全管理器非空，则从安全管理器获取线程组，
     *               否则设置为当前线程的线程组。
     * @param  target 运行主体，线程执行时将调用该对象的 run 方法。若为 null，线程执行时不做任何事。
     * @param  name 线程名
     * @param  stackSize 栈大小，若为零则表示无限制
     * @throws  SecurityException 若当前线程无法在该线程组中创建
     * @since 1.4
     */
    public Thread(ThreadGroup group, Runnable target, String name, long stackSize) {
        init(group, target, name, stackSize);
    }

    /**
     * <h3>启动此线程</h3>
     *
     * 用于启动本线程实例，JVM 将启动新的线程并调用本实例的 run 方法执行。
     *
     * 启动线程之后，此线程与原线程并发执行。重新启动已启动的线程是非法的，无论线程是否执行完毕。
     *
     * @exception  IllegalThreadStateException 如果此线程已被启动过
     * @see        #run()
     * @see        #stop()
     */
    public synchronized void start() {
        /*
         * This method is not invoked for the main method thread or "system"
         * group threads created/set up by the VM. Any new functionality added
         * to this method in the future may have to also be added to the VM.
         *
         * A zero status value corresponds to state "NEW".
         */
        if (threadStatus != 0)
            throw new IllegalThreadStateException();

        /* Notify the group that this thread is about to be started
         * so that it can be added to the group's list of threads
         * and the group's unstarted count can be decremented. */
        group.add(this);

        boolean started = false;
        try {
            start0();
            started = true;
        } finally {
            try {
                if (!started) {
                    group.threadStartFailed(this);
                }
            } catch (Throwable ignore) {
                /* do nothing. If start0 threw a Throwable then
                  it will be passed up the call stack */
            }
        }
    }

    private native void start0();

    /**
     * <h3>线程运行的主体方法</h3>
     *
     * 调用运行主体中的 run 方法运行。若没有运行主体，则不做任何事。
     *
     * @see     #start()
     * @see     #stop()
     * @see     #Thread(ThreadGroup, Runnable, String)
     */
    @Override
    public void run() {
        if (target != null) {
            target.run();
        }
    }

    /**
     * <h3>退出该线程</h3>
     *
     * 该方法由系统调用，在线程完全退出之前，给线程一个释放所有资源的机会。
     */
    @SuppressWarnings("unused")
    private void exit() {
        if (group != null) {
            group.threadTerminated(this);
            group = null;
        }
        /* Aggressively null out all reference fields: see bug 4006245 */
        target = null;
        /* Speed the release of some of these resources */
        threadLocals = null;
        inheritableThreadLocals = null;
        inheritedAccessControlContext = null;
        blocker = null;
        uncaughtExceptionHandler = null;
    }

    /**
     * <h3>强制线程停止执行</h3>
     *
     * Forces the thread to stop executing.
     * <p>
     * If there is a security manager installed, its <code>checkAccess</code>
     * method is called with <code>this</code>
     * as its argument. This may result in a
     * <code>SecurityException</code> being raised (in the current thread).
     * <p>
     * If this thread is different from the current thread (that is, the current
     * thread is trying to stop a thread other than itself), the
     * security manager's <code>checkPermission</code> method (with a
     * <code>RuntimePermission("stopThread")</code> argument) is called in
     * addition.
     * Again, this may result in throwing a
     * <code>SecurityException</code> (in the current thread).
     * <p>
     * The thread represented by this thread is forced to stop whatever
     * it is doing abnormally and to throw a newly created
     * <code>ThreadDeath</code> object as an exception.
     * <p>
     * It is permitted to stop a thread that has not yet been started.
     * If the thread is eventually started, it immediately terminates.
     * <p>
     * An application should not normally try to catch
     * <code>ThreadDeath</code> unless it must do some extraordinary
     * cleanup operation (note that the throwing of
     * <code>ThreadDeath</code> causes <code>finally</code> clauses of
     * <code>try</code> statements to be executed before the thread
     * officially dies).  If a <code>catch</code> clause catches a
     * <code>ThreadDeath</code> object, it is important to rethrow the
     * object so that the thread actually dies.
     * <p>
     * The top-level error handler that reacts to otherwise uncaught
     * exceptions does not print out a message or otherwise notify the
     * application if the uncaught exception is an instance of
     * <code>ThreadDeath</code>.
     *
     * @exception  SecurityException  if the current thread cannot
     *               modify this thread.
     * @see        #interrupt()
     * @see        #checkAccess()
     * @see        #run()
     * @see        #start()
     * @see        ThreadDeath
     * @see        ThreadGroup#uncaughtException(Thread,Throwable)
     * @see        SecurityManager#checkAccess(Thread)
     * @see        SecurityManager#checkPermission
     * @deprecated This method is inherently unsafe.  Stopping a thread with
     *       Thread.stop causes it to unlock all of the monitors that it
     *       has locked (as a natural consequence of the unchecked
     *       <code>ThreadDeath</code> exception propagating up the stack).  If
     *       any of the objects previously protected by these monitors were in
     *       an inconsistent state, the damaged objects become visible to
     *       other threads, potentially resulting in arbitrary behavior.  Many
     *       uses of <code>stop</code> should be replaced by code that simply
     *       modifies some variable to indicate that the target thread should
     *       stop running.  The target thread should check this variable
     *       regularly, and return from its run method in an orderly fashion
     *       if the variable indicates that it is to stop running.  If the
     *       target thread waits for long periods (on a condition variable,
     *       for example), the <code>interrupt</code> method should be used to
     *       interrupt the wait.
     *       For more information, see
     *       <a href="{@docRoot}/../technotes/guides/concurrency/threadPrimitiveDeprecation.html">Why
     *       are Thread.stop, Thread.suspend and Thread.resume Deprecated?</a>.
     */
    @Deprecated
    public final void stop() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            checkAccess();
            if (this != Thread.currentThread()) {
                security.checkPermission(SecurityConstants.STOP_THREAD_PERMISSION);
            }
        }
        // A zero status value corresponds to "NEW", it can't change to
        // not-NEW because we hold the lock.
        if (threadStatus != 0) {
            resume(); // Wake up thread if it was suspended; no-op otherwise
        }

        // The VM can handle all thread states
        stop0(new ThreadDeath());
    }

    /**
     * Throws {@code UnsupportedOperationException}.
     *
     * @param obj ignored
     *
     * @deprecated This method was originally designed to force a thread to stop
     *        and throw a given {@code Throwable} as an exception. It was
     *        inherently unsafe (see {@link #stop()} for details), and furthermore
     *        could be used to generate exceptions that the target thread was
     *        not prepared to handle.
     *        For more information, see
     *        <a href="{@docRoot}/../technotes/guides/concurrency/threadPrimitiveDeprecation.html">Why
     *        are Thread.stop, Thread.suspend and Thread.resume Deprecated?</a>.
     */
    @Deprecated
    @SuppressWarnings("unused")
    public final synchronized void stop(Throwable obj) {
        throw new UnsupportedOperationException();
    }

    /**
     * <h3>对此线程发出中断</h3>
     *
     * 线程中断自身的时候，不会发生任何事情只会消耗时间。其他线程向此线程发出中断时，
     * 会调用 checkAccess 方法检查当前调用线程的权限，鉴权失败将抛出异常。<br/><br/>
     *
     * 如果此线程因为调用 wait、join、sleep 而阻塞，在调用该线程此方法时，会设置此线程为中断状态，
     * 此线程将会接收到一个 {@link InterruptedException}。<br/><br/>
     *
     * 如果此线程因为使用 {@link InterruptibleChannel} 而阻塞，在调用该线程此方法时，
     * 将会关闭该 Channel，设置此线程为中断状态，同时线程接收到一个 {@link ClosedByInterruptException}。<br/><br/>
     *
     * 如果此线程因为使用 {@link Selector} 而阻塞，在调用该线程此方法时，会设置此线程为中断状态，
     * 同时会立刻从选择操作中返回，大概率会返回一个非零值，就像调用了 selector 中的 wakeup 方法一样。
     *
     * 向一个已经凋亡的线程发送中断将不会产生任何效果。
     *
     * @apiNote 即使是发出中断，如果中断处理程序请求锁，同时锁被其他线程抢占了，目标线程也无法响应中断
     *
     * <p> Interrupting a thread that is not alive need not have any effect.
     *
     * @throws  SecurityException 若当前线程无法修改该线程
     *
     * @revised 6.0
     * @spec JSR-51
     */
    @SuppressWarnings("JavaDoc")
    public void interrupt() {
        if (this != Thread.currentThread())
            checkAccess();

        synchronized (blockerLock) {
            Interruptible b = blocker;
            if (b != null) {
                interrupt0();           // Just to set the interrupt flag
                b.interrupt(this);
                return;
            }
        }
        interrupt0();
    }

    /**
     * <h3>判断当前执行线程是否被中断</h3>
     *
     * 在获取中断状态的同时会重置其中断状态。
     *
     * 当线程被中断但无法执行时，会表现为本方法返回 false。
     *
     * @return  若线程被中断，返回 true
     * @see #isInterrupted()
     * @revised 6.0
     */
    @SuppressWarnings("JavaDoc")
    public static boolean interrupted() {
        return currentThread().isInterrupted(true);
    }

    /**
     * <h3>判断此线程是否被中断</h3>
     *
     * 当线程被中断但无法执行时，会表现为本方法返回 false。
     *
     * @return 若线程被中断，返回 true
     * @see     #interrupted()
     * @revised 6.0
     */
    @SuppressWarnings("JavaDoc")
    public boolean isInterrupted() {
        return isInterrupted(false);
    }

    /**
     * <h3>测试本线程是否被中断</h3>
     *
     * @param ClearInterrupted 重置中断状态
     * @return 是否正在被中断
     */
    private native boolean isInterrupted(boolean ClearInterrupted);

    /**
     * Throws {@link NoSuchMethodError}.
     *
     * @deprecated This method was originally designed to destroy this
     *     thread without any cleanup. Any monitors it held would have
     *     remained locked. However, the method was never implemented.
     *     If if were to be implemented, it would be deadlock-prone in
     *     much the manner of {@link #suspend}. If the target thread held
     *     a lock protecting a critical system resource when it was
     *     destroyed, no thread could ever access this resource again.
     *     If another thread ever attempted to lock this resource, deadlock
     *     would result. Such deadlocks typically manifest themselves as
     *     "frozen" processes. For more information, see
     *     <a href="{@docRoot}/../technotes/guides/concurrency/threadPrimitiveDeprecation.html">
     *     Why are Thread.stop, Thread.suspend and Thread.resume Deprecated?</a>.
     * @throws NoSuchMethodError always
     */
    @Deprecated
    public void destroy() {
        throw new NoSuchMethodError();
    }

    /**
     * <h3>判断该线程是否存活</h3>
     *
     * 线程在启动之后，直到销毁之前都是存活状态
     *
     * @return 若线程存活，返回 true
     */
    public final native boolean isAlive();

    /**
     * <h3>暂停该线程</h3>
     *
     * 若该线程正在执行，停止该线程直到线程被重新运行（resume）。
     * 会调用 checkAccess 方法检查当前调用线程的权限，鉴权失败将抛出异常。
     *
     * @exception SecurityException 若当前线程没有权限进行操作
     * @see #checkAccess
     * @deprecated 由于该方法容易造成死锁，该方法已被废弃。当调用本方法时，不会释放当前线程所持有的锁。
     *             当负责唤醒的线程需要该锁时，将造成死锁。<br/>
     *             若要实现类似的线程调度工作，请使用 Object 的 wait 和 notify 组合。
     */
    @Deprecated
    public final void suspend() {
        checkAccess();
        suspend0();
    }

    /**
     * <h3>重新运行该线程</h3>
     *
     * 若该线程被暂停（suspend），重新启用该线程的执行。
     * 会调用 checkAccess 方法检查当前调用线程的权限，鉴权失败将抛出异常。
     *
     * @exception SecurityException 若当前线程无权操作
     * @see        #checkAccess
     * @see        #suspend()
     * @deprecated 本方法与 suspend 组合使用，由于二者容易造成死锁，故已废弃。<br/>
     *             若要实现类似的线程调度工作，请使用 Object 的 wait 和 notify 组合。
     */
    @Deprecated
    public final void resume() {
        checkAccess();
        resume0();
    }

    /**
     * <h3>设置线程优先级</h3>
     *
     * 将线程优先级设置为参数中的数值，若参数值大于线程组的最大优先级，
     * 则设置为线程组中的最大优先级。
     * 会调用 checkAccess 方法检查当前调用线程的权限，鉴权失败将抛出异常。
     *
     * @param newPriority 新优先级
     * @exception  IllegalArgumentException 若参数小于 MIN_PRIORITY 或大于 MAX_PRIORITY
     * @exception  SecurityException 若当前线程无权操作
     * @see        #getPriority
     * @see        #checkAccess()
     * @see        #getThreadGroup()
     * @see        #MAX_PRIORITY
     * @see        #MIN_PRIORITY
     * @see        ThreadGroup#getMaxPriority()
     */
    public final void setPriority(int newPriority) {
        ThreadGroup g;
        checkAccess();
        if (newPriority > MAX_PRIORITY || newPriority < MIN_PRIORITY) {
            throw new IllegalArgumentException();
        }
        if((g = getThreadGroup()) != null) {
            if (newPriority > g.getMaxPriority()) {
                newPriority = g.getMaxPriority();
            }
            setPriority0(priority = newPriority);
        }
    }

    /**
     * <h3>获取线程优先级</h3>
     *
     * @return  该线程优先级
     * @see     #setPriority
     */
    public final int getPriority() {
        return priority;
    }

    /**
     * <h3>设置线程名</h3>
     *
     * 会调用 checkAccess 方法检查当前调用线程的权限，鉴权失败将抛出异常。
     *
     * @param      name 新线程名
     * @exception  SecurityException 若当前线程无权操作
     * @see        #getName
     * @see        #checkAccess()
     */
    public final synchronized void setName(String name) {
        checkAccess();
        //noinspection ConstantConditions
        if (name == null) {
            throw new NullPointerException("name cannot be null");
        }

        this.name = name;
        if (threadStatus != 0) {
            setNativeName(name);
        }
    }

    /**
     * <h3>获取线程名</h3>
     *
     * @return  线程名
     * @see     #setName(String)
     */
    public final String getName() {
        return name;
    }

    /**
     * <h3>获取所属线程组</h3>
     *
     * @return 所属线程组，若线程已销毁则返回 null
     */
    public final ThreadGroup getThreadGroup() {
        return group;
    }

    /**
     * <h3>返回所属线程组中活动的线程数量</h3>
     *
     * 返回所属线程组，及线程组中子孙线程组中所有活动线程的数量。该值是一个近似值，
     * 因为在计算过程中线程可能被动态的销毁和添加。
     *
     * @apiNote 此方法主要用于 debug 和监测。
     *
     * @return 所属线程组，包括旗下子孙线程组中所有活动线程的近似数量
     */
    public static int activeCount() {
        return currentThread().getThreadGroup().activeCount();
    }

    /**
     * <h3>复制当前线程组中所有活动线程到指定数组中</h3>
     *
     * 复制当前线程所属线程组及其旗下子孙线程组中所有活动线程到参数数组中。通过调用
     * {@link ThreadGroup#enumerate(Thread[])} 实现。<br/><br/>
     *
     * {@link Thread#activeCount()} 返回的线程数量是一个近似值，如果将该值作为 threadArr
     * 的参数可能会造成数组长度过小导致丢失超出的那部分线程。如果调用本方法的要求较为严格，
     * 请保证 threadArr 的大小远大于 activeCount 的返回值。<br/><br/>
     *
     * @apiNote 本方法适用于 debug 和监测
     *
     * @param  threadArr 线程数组
     *
     * @return 添加到 threadArr 中线程的数量
     *
     * @throws  SecurityException 若当前线程无权操作
     */
    public static int enumerate(Thread[] threadArr) {
        return currentThread().getThreadGroup().enumerate(threadArr);
    }

    /**
     * <h3>获取该线程中栈帧的数量</h3>
     *
     * 被调用的线程实例必须暂停。
     *
     * @return     线程中栈帧的数量
     * @exception  IllegalThreadStateException 若该线程未暂停
     * @deprecated 由于 {@link #suspend()} 被废弃，因此本方法也被废弃
     */
    @Deprecated
    public native int countStackFrames();

    /**
     * <h3>将指定时间的时间片让给此线程实例</h3>
     *
     * 若 millis 为 0 表示永远让出，直到此线程实例销毁。
     * 本方法通过循环判断此线程实例是否活动而持续让调用线程在此线程上 wait 实现，
     * 因此在此线程实例销毁或者指定时间到达之前，如果调用线程被唤醒，仍会继续使用 wait 进行等待。
     * 当线程被销毁时，会调用该线程实例的 notifyAll 方法唤醒所有在其上等待的线程。
     *
     * @param  millis 睡眠毫秒数
     * @throws  IllegalArgumentException 若插入时间为负数
     * @throws  InterruptedException 若其他进程向调用线程发送中断
     */
    public final synchronized void join(long millis) throws InterruptedException {
        long base = System.currentTimeMillis();
        long now = 0;

        if (millis < 0) {
            throw new IllegalArgumentException("timeout value is negative");
        }

        if (millis == 0) {
            // 若 millis 为 0，等待直至本线程挂掉
            while (isAlive()) {
                wait(0);
            }
        } else {
            while (isAlive()) {
                long delay = millis - now;
                if (delay <= 0) {
                    break;
                }
                wait(delay);
                now = System.currentTimeMillis() - base;
            }
        }
    }

    /**
     * <h3>将指定时间的时间片让给此线程</h3>
     *
     * 若 millis 和 nanos 都为 0 表示永远让出，直到此线程实例销毁。
     * 本方法通过循环判断此线程实例是否活动而持续让调用线程在此线程上 wait 实现，
     * 因此在此线程实例销毁或者指定时间到达之前，如果调用线程被唤醒，仍会继续使用 wait 进行等待。
     * 当线程被销毁时，会调用该线程实例的 notifyAll 方法唤醒所有在其上等待的线程。<br/><br/>
     *
     * 若 millis 为 0，只要 nanos 合法，其作用仅是将 millis 置为 1。
     * 若 millis 不为 0，只有 nanos 合法且大于 500000 时，millis 才会加一
     *
     * @param  millis 睡眠毫秒数
     * @param  nanos 纳秒数，基本无意义，范围 0-999999
     * @throws  IllegalArgumentException 当 millis 为负数或 nanos 超出范围
     * @throws  InterruptedException 当其他线程向调用线程发出中断
     */
    public final synchronized void join(long millis, int nanos) throws InterruptedException {

        if (millis < 0) {
            throw new IllegalArgumentException("timeout value is negative");
        }

        if (nanos < 0 || nanos > 999999) {
            throw new IllegalArgumentException("nanosecond timeout value out of range");
        }

        // 和 sleep 一样的套路，而 Object.wait() 确实只要合法就加一
        if (nanos >= 500000 || (nanos != 0 && millis == 0)) {
            millis++;
        }

        join(millis);
    }

    /**
     * <h3>将 CPU 时间片让给此线程直到此线程销毁</h3>
     *
     * @throws  InterruptedException 当其他线程向调用线程发出中断
     */
    public final void join() throws InterruptedException {
        join(0);
    }

    /**
     * <h3>打印栈路径</h3>
     *
     * 使用标准错误输出流打印栈路径
     *
     * @apiNote 仅在 debug 时使用
     * @see     Throwable#printStackTrace()
     */
    public static void dumpStack() {
        new Exception("Stack trace").printStackTrace();
    }

    /**
     * <3>设置此线程是否为守护线程</h3>
     *
     * 若所有执行线程仅剩守护线程，JVM 将退出。
     *
     * @apiNote 此方法必须在线程执行之前设置才能生效。
     * @param  on 是否为守护线程
     * @throws  IllegalThreadStateException 若此线程是活动线程
     * @throws  SecurityException 若调用线程无权限执行
     */
    public final void setDaemon(boolean on) {
        checkAccess();
        if (isAlive()) {
            throw new IllegalThreadStateException();
        }
        daemon = on;
    }

    /**
     * <h3>判断此线程是否为守护线程</h3>
     *
     * @return 若此线程为守护线程，返回 true
     * @see     #setDaemon(boolean)
     */
    public final boolean isDaemon() {
        return daemon;
    }

    /**
     * <h3>判断当前线程是否有权限执行对此线程的操作</h3>
     *
     * 若设置了安全管理器，就会调用该安全管理器的 checkAccess 方法来判断权限。
     * 否则直接通过。
     *
     * @exception  SecurityException 若当前线程无此线程的权限
     * @see        SecurityManager#checkAccess(Thread)
     */
    public final void checkAccess() {
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkAccess(this);
        }
    }

    /**
     * <h3>返回此线程实例的字符串表示</h3>
     *
     * 包括线程名、线程优先级和线程组名
     *
     * @return 此线程的字符串表示
     */
    @Override
    public String toString() {
        ThreadGroup group = getThreadGroup();
        if (group != null) {
            return "Thread[" + getName() + "," + getPriority() + "," + group.getName() + "]";
        } else {
            return "Thread[" + getName() + "," + getPriority() + "," + "" + "]";
        }
    }

    /**
     * Returns the context ClassLoader for this Thread. The context
     * ClassLoader is provided by the creator of the thread for use
     * by code running in this thread when loading classes and resources.
     * If not {@linkplain #setContextClassLoader set}, the default is the
     * ClassLoader context of the parent Thread. The context ClassLoader of the
     * primordial thread is typically set to the class loader used to load the
     * application.
     *
     * <p>If a security manager is present, and the invoker's class loader is not
     * {@code null} and is not the same as or an ancestor of the context class
     * loader, then this method invokes the security manager's {@link
     * SecurityManager#checkPermission(java.security.Permission) checkPermission}
     * method with a {@link RuntimePermission RuntimePermission}{@code
     * ("getClassLoader")} permission to verify that retrieval of the context
     * class loader is permitted.
     *
     * @return  the context ClassLoader for this Thread, or {@code null}
     *          indicating the system class loader (or, failing that, the
     *          bootstrap class loader)
     *
     * @throws  SecurityException
     *          if the current thread cannot get the context ClassLoader
     *
     * @since 1.2
     */
    @CallerSensitive
    public ClassLoader getContextClassLoader() {
        if (contextClassLoader == null)
            return null;
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            ClassLoader.checkClassLoaderPermission(contextClassLoader, Reflection.getCallerClass());
        }
        return contextClassLoader;
    }

    /**
     * Sets the context ClassLoader for this Thread. The context
     * ClassLoader can be set when a thread is created, and allows
     * the creator of the thread to provide the appropriate class loader,
     * through {@code getContextClassLoader}, to code running in the thread
     * when loading classes and resources.
     *
     * <p>If a security manager is present, its {@link
     * SecurityManager#checkPermission(java.security.Permission) checkPermission}
     * method is invoked with a {@link RuntimePermission RuntimePermission}{@code
     * ("setContextClassLoader")} permission to see if setting the context
     * ClassLoader is permitted.
     *
     * @param  cl
     *         the context ClassLoader for this Thread, or null  indicating the
     *         system class loader (or, failing that, the bootstrap class loader)
     *
     * @throws  SecurityException
     *          if the current thread cannot set the context ClassLoader
     *
     * @since 1.2
     */
    public void setContextClassLoader(ClassLoader cl) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new RuntimePermission("setContextClassLoader"));
        }
        contextClassLoader = cl;
    }

    /**
     * Returns <tt>true</tt> if and only if the current thread holds the
     * monitor lock on the specified object.
     *
     * <p>This method is designed to allow a program to assert that
     * the current thread already holds a specified lock:
     * <pre>
     *     assert Thread.holdsLock(obj);
     * </pre>
     *
     * @param  obj the object on which to test lock ownership
     * @throws NullPointerException if obj is <tt>null</tt>
     * @return <tt>true</tt> if the current thread holds the monitor lock on
     *         the specified object.
     * @since 1.4
     */
    public static native boolean holdsLock(Object obj);

    private static final StackTraceElement[] EMPTY_STACK_TRACE
        = new StackTraceElement[0];

    /**
     * Returns an array of stack trace elements representing the stack dump
     * of this thread.  This method will return a zero-length array if
     * this thread has not started, has started but has not yet been
     * scheduled to run by the system, or has terminated.
     * If the returned array is of non-zero length then the first element of
     * the array represents the top of the stack, which is the most recent
     * method invocation in the sequence.  The last element of the array
     * represents the bottom of the stack, which is the least recent method
     * invocation in the sequence.
     *
     * <p>If there is a security manager, and this thread is not
     * the current thread, then the security manager's
     * <tt>checkPermission</tt> method is called with a
     * <tt>RuntimePermission("getStackTrace")</tt> permission
     * to see if it's ok to get the stack trace.
     *
     * <p>Some virtual machines may, under some circumstances, omit one
     * or more stack frames from the stack trace.  In the extreme case,
     * a virtual machine that has no stack trace information concerning
     * this thread is permitted to return a zero-length array from this
     * method.
     *
     * @return an array of <tt>StackTraceElement</tt>,
     * each represents one stack frame.
     *
     * @throws SecurityException
     *        if a security manager exists and its
     *        <tt>checkPermission</tt> method doesn't allow
     *        getting the stack trace of thread.
     * @see SecurityManager#checkPermission
     * @see RuntimePermission
     * @see Throwable#getStackTrace
     *
     * @since 1.5
     */
    public StackTraceElement[] getStackTrace() {
        if (this != Thread.currentThread()) {
            // check for getStackTrace permission
            SecurityManager security = System.getSecurityManager();
            if (security != null) {
                security.checkPermission(
                    SecurityConstants.GET_STACK_TRACE_PERMISSION);
            }
            // optimization so we do not call into the vm for threads that
            // have not yet started or have terminated
            if (!isAlive()) {
                return EMPTY_STACK_TRACE;
            }
            StackTraceElement[][] stackTraceArray = dumpThreads(new Thread[] {this});
            StackTraceElement[] stackTrace = stackTraceArray[0];
            // a thread that was alive during the previous isAlive call may have
            // since terminated, therefore not having a stacktrace.
            if (stackTrace == null) {
                stackTrace = EMPTY_STACK_TRACE;
            }
            return stackTrace;
        } else {
            // Don't need JVM help for current thread
            return (new Exception()).getStackTrace();
        }
    }

    /**
     * Returns a map of stack traces for all live threads.
     * The map keys are threads and each map value is an array of
     * <tt>StackTraceElement</tt> that represents the stack dump
     * of the corresponding <tt>Thread</tt>.
     * The returned stack traces are in the format specified for
     * the {@link #getStackTrace getStackTrace} method.
     *
     * <p>The threads may be executing while this method is called.
     * The stack trace of each thread only represents a snapshot and
     * each stack trace may be obtained at different time.  A zero-length
     * array will be returned in the map value if the virtual machine has
     * no stack trace information about a thread.
     *
     * <p>If there is a security manager, then the security manager's
     * <tt>checkPermission</tt> method is called with a
     * <tt>RuntimePermission("getStackTrace")</tt> permission as well as
     * <tt>RuntimePermission("modifyThreadGroup")</tt> permission
     * to see if it is ok to get the stack trace of all threads.
     *
     * @return a <tt>Map</tt> from <tt>Thread</tt> to an array of
     * <tt>StackTraceElement</tt> that represents the stack trace of
     * the corresponding thread.
     *
     * @throws SecurityException
     *        if a security manager exists and its
     *        <tt>checkPermission</tt> method doesn't allow
     *        getting the stack trace of thread.
     * @see #getStackTrace
     * @see SecurityManager#checkPermission
     * @see RuntimePermission
     * @see Throwable#getStackTrace
     *
     * @since 1.5
     */
    public static Map<Thread, StackTraceElement[]> getAllStackTraces() {
        // check for getStackTrace permission
        SecurityManager security = System.getSecurityManager();
        if (security != null) {
            security.checkPermission(
                SecurityConstants.GET_STACK_TRACE_PERMISSION);
            security.checkPermission(
                SecurityConstants.MODIFY_THREADGROUP_PERMISSION);
        }

        // Get a snapshot of the list of all threads
        Thread[] threads = getThreads();
        StackTraceElement[][] traces = dumpThreads(threads);
        Map<Thread, StackTraceElement[]> m = new HashMap<>(threads.length);
        for (int i = 0; i < threads.length; i++) {
            StackTraceElement[] stackTrace = traces[i];
            if (stackTrace != null) {
                m.put(threads[i], stackTrace);
            }
            // else terminated so we don't put it in the map
        }
        return m;
    }


    private static final RuntimePermission SUBCLASS_IMPLEMENTATION_PERMISSION =
            new RuntimePermission("enableContextClassLoaderOverride");

    /** cache of subclass security audit results */
    /* Replace with ConcurrentReferenceHashMap when/if it appears in a future
     * release */
    private static class Caches {
        /** cache of subclass security audit results */
        static final ConcurrentMap<WeakClassKey,Boolean> subclassAudits =
            new ConcurrentHashMap<>();

        /** queue for WeakReferences to audited subclasses */
        static final ReferenceQueue<Class<?>> subclassAuditsQueue =
            new ReferenceQueue<>();
    }

    /**
     * Verifies that this (possibly subclass) instance can be constructed
     * without violating security constraints: the subclass must not override
     * security-sensitive non-final methods, or else the
     * "enableContextClassLoaderOverride" RuntimePermission is checked.
     */
    private static boolean isCCLOverridden(Class<?> cl) {
        if (cl == Thread.class)
            return false;

        processQueue(Caches.subclassAuditsQueue, Caches.subclassAudits);
        WeakClassKey key = new WeakClassKey(cl, Caches.subclassAuditsQueue);
        Boolean result = Caches.subclassAudits.get(key);
        if (result == null) {
            result = Boolean.valueOf(auditSubclass(cl));
            Caches.subclassAudits.putIfAbsent(key, result);
        }

        return result.booleanValue();
    }

    /**
     * Performs reflective checks on given subclass to verify that it doesn't
     * override security-sensitive non-final methods.  Returns true if the
     * subclass overrides any of the methods, false otherwise.
     */
    private static boolean auditSubclass(final Class<?> subcl) {
        Boolean result = AccessController.doPrivileged(
            new PrivilegedAction<Boolean>() {
                public Boolean run() {
                    for (Class<?> cl = subcl;
                         cl != Thread.class;
                         cl = cl.getSuperclass())
                    {
                        try {
                            cl.getDeclaredMethod("getContextClassLoader", new Class<?>[0]);
                            return Boolean.TRUE;
                        } catch (NoSuchMethodException ex) {
                        }
                        try {
                            Class<?>[] params = {ClassLoader.class};
                            cl.getDeclaredMethod("setContextClassLoader", params);
                            return Boolean.TRUE;
                        } catch (NoSuchMethodException ex) {
                        }
                    }
                    return Boolean.FALSE;
                }
            }
        );
        return result.booleanValue();
    }

    private native static StackTraceElement[][] dumpThreads(Thread[] threads);
    private native static Thread[] getThreads();

    /**
     * Returns the identifier of this Thread.  The thread ID is a positive
     * <tt>long</tt> number generated when this thread was created.
     * The thread ID is unique and remains unchanged during its lifetime.
     * When a thread is terminated, this thread ID may be reused.
     *
     * @return this thread's ID.
     * @since 1.5
     */
    public long getId() {
        return tid;
    }

    /**
     * A thread state.  A thread can be in one of the following states:
     * <ul>
     * <li>{@link #NEW}<br>
     *     A thread that has not yet started is in this state.
     *     </li>
     * <li>{@link #RUNNABLE}<br>
     *     A thread executing in the Java virtual machine is in this state.
     *     </li>
     * <li>{@link #BLOCKED}<br>
     *     A thread that is blocked waiting for a monitor lock
     *     is in this state.
     *     </li>
     * <li>{@link #WAITING}<br>
     *     A thread that is waiting indefinitely for another thread to
     *     perform a particular action is in this state.
     *     </li>
     * <li>{@link #TIMED_WAITING}<br>
     *     A thread that is waiting for another thread to perform an action
     *     for up to a specified waiting time is in this state.
     *     </li>
     * <li>{@link #TERMINATED}<br>
     *     A thread that has exited is in this state.
     *     </li>
     * </ul>
     *
     * <p>
     * A thread can be in only one state at a given point in time.
     * These states are virtual machine states which do not reflect
     * any operating system thread states.
     *
     * @since   1.5
     * @see #getState
     */
    public enum State {
        /**
         * Thread state for a thread which has not yet started.
         */
        NEW,

        /**
         * Thread state for a runnable thread.  A thread in the runnable
         * state is executing in the Java virtual machine but it may
         * be waiting for other resources from the operating system
         * such as processor.
         */
        RUNNABLE,

        /**
         * Thread state for a thread blocked waiting for a monitor lock.
         * A thread in the blocked state is waiting for a monitor lock
         * to enter a synchronized block/method or
         * reenter a synchronized block/method after calling
         * {@link Object#wait() Object.wait}.
         */
        BLOCKED,

        /**
         * Thread state for a waiting thread.
         * A thread is in the waiting state due to calling one of the
         * following methods:
         * <ul>
         *   <li>{@link Object#wait() Object.wait} with no timeout</li>
         *   <li>{@link #join() Thread.join} with no timeout</li>
         *   <li>{@link LockSupport#park() LockSupport.park}</li>
         * </ul>
         *
         * <p>A thread in the waiting state is waiting for another thread to
         * perform a particular action.
         *
         * For example, a thread that has called <tt>Object.wait()</tt>
         * on an object is waiting for another thread to call
         * <tt>Object.notify()</tt> or <tt>Object.notifyAll()</tt> on
         * that object. A thread that has called <tt>Thread.join()</tt>
         * is waiting for a specified thread to terminate.
         */
        WAITING,

        /**
         * Thread state for a waiting thread with a specified waiting time.
         * A thread is in the timed waiting state due to calling one of
         * the following methods with a specified positive waiting time:
         * <ul>
         *   <li>{@link #sleep Thread.sleep}</li>
         *   <li>{@link Object#wait(long) Object.wait} with timeout</li>
         *   <li>{@link #join(long) Thread.join} with timeout</li>
         *   <li>{@link LockSupport#parkNanos LockSupport.parkNanos}</li>
         *   <li>{@link LockSupport#parkUntil LockSupport.parkUntil}</li>
         * </ul>
         */
        TIMED_WAITING,

        /**
         * Thread state for a terminated thread.
         * The thread has completed execution.
         */
        TERMINATED;
    }

    /**
     * Returns the state of this thread.
     * This method is designed for use in monitoring of the system state,
     * not for synchronization control.
     *
     * @return this thread's state.
     * @since 1.5
     */
    public State getState() {
        // get current thread state
        return sun.misc.VM.toThreadState(threadStatus);
    }

    // Added in JSR-166

    /**
     * Interface for handlers invoked when a <tt>Thread</tt> abruptly
     * terminates due to an uncaught exception.
     * <p>When a thread is about to terminate due to an uncaught exception
     * the Java Virtual Machine will query the thread for its
     * <tt>UncaughtExceptionHandler</tt> using
     * {@link #getUncaughtExceptionHandler} and will invoke the handler's
     * <tt>uncaughtException</tt> method, passing the thread and the
     * exception as arguments.
     * If a thread has not had its <tt>UncaughtExceptionHandler</tt>
     * explicitly set, then its <tt>ThreadGroup</tt> object acts as its
     * <tt>UncaughtExceptionHandler</tt>. If the <tt>ThreadGroup</tt> object
     * has no
     * special requirements for dealing with the exception, it can forward
     * the invocation to the {@linkplain #getDefaultUncaughtExceptionHandler
     * default uncaught exception handler}.
     *
     * @see #setDefaultUncaughtExceptionHandler
     * @see #setUncaughtExceptionHandler
     * @see ThreadGroup#uncaughtException
     * @since 1.5
     */
    @FunctionalInterface
    public interface UncaughtExceptionHandler {
        /**
         * Method invoked when the given thread terminates due to the
         * given uncaught exception.
         * <p>Any exception thrown by this method will be ignored by the
         * Java Virtual Machine.
         * @param t the thread
         * @param e the exception
         */
        void uncaughtException(Thread t, Throwable e);
    }

    // null unless explicitly set
    private volatile UncaughtExceptionHandler uncaughtExceptionHandler;

    // null unless explicitly set
    private static volatile UncaughtExceptionHandler defaultUncaughtExceptionHandler;

    /**
     * Set the default handler invoked when a thread abruptly terminates
     * due to an uncaught exception, and no other handler has been defined
     * for that thread.
     *
     * <p>Uncaught exception handling is controlled first by the thread, then
     * by the thread's {@link ThreadGroup} object and finally by the default
     * uncaught exception handler. If the thread does not have an explicit
     * uncaught exception handler set, and the thread's thread group
     * (including parent thread groups)  does not specialize its
     * <tt>uncaughtException</tt> method, then the default handler's
     * <tt>uncaughtException</tt> method will be invoked.
     * <p>By setting the default uncaught exception handler, an application
     * can change the way in which uncaught exceptions are handled (such as
     * logging to a specific device, or file) for those threads that would
     * already accept whatever &quot;default&quot; behavior the system
     * provided.
     *
     * <p>Note that the default uncaught exception handler should not usually
     * defer to the thread's <tt>ThreadGroup</tt> object, as that could cause
     * infinite recursion.
     *
     * @param eh the object to use as the default uncaught exception handler.
     * If <tt>null</tt> then there is no default handler.
     *
     * @throws SecurityException if a security manager is present and it
     *         denies <tt>{@link RuntimePermission}
     *         (&quot;setDefaultUncaughtExceptionHandler&quot;)</tt>
     *
     * @see #setUncaughtExceptionHandler
     * @see #getUncaughtExceptionHandler
     * @see ThreadGroup#uncaughtException
     * @since 1.5
     */
    public static void setDefaultUncaughtExceptionHandler(UncaughtExceptionHandler eh) {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(
                new RuntimePermission("setDefaultUncaughtExceptionHandler")
                    );
        }

         defaultUncaughtExceptionHandler = eh;
     }

    /**
     * Returns the default handler invoked when a thread abruptly terminates
     * due to an uncaught exception. If the returned value is <tt>null</tt>,
     * there is no default.
     * @since 1.5
     * @see #setDefaultUncaughtExceptionHandler
     * @return the default uncaught exception handler for all threads
     */
    public static UncaughtExceptionHandler getDefaultUncaughtExceptionHandler(){
        return defaultUncaughtExceptionHandler;
    }

    /**
     * Returns the handler invoked when this thread abruptly terminates
     * due to an uncaught exception. If this thread has not had an
     * uncaught exception handler explicitly set then this thread's
     * <tt>ThreadGroup</tt> object is returned, unless this thread
     * has terminated, in which case <tt>null</tt> is returned.
     * @since 1.5
     * @return the uncaught exception handler for this thread
     */
    public UncaughtExceptionHandler getUncaughtExceptionHandler() {
        return uncaughtExceptionHandler != null ?
            uncaughtExceptionHandler : group;
    }

    /**
     * Set the handler invoked when this thread abruptly terminates
     * due to an uncaught exception.
     * <p>A thread can take full control of how it responds to uncaught
     * exceptions by having its uncaught exception handler explicitly set.
     * If no such handler is set then the thread's <tt>ThreadGroup</tt>
     * object acts as its handler.
     * @param eh the object to use as this thread's uncaught exception
     * handler. If <tt>null</tt> then this thread has no explicit handler.
     * @throws  SecurityException  if the current thread is not allowed to
     *          modify this thread.
     * @see #setDefaultUncaughtExceptionHandler
     * @see ThreadGroup#uncaughtException
     * @since 1.5
     */
    public void setUncaughtExceptionHandler(UncaughtExceptionHandler eh) {
        checkAccess();
        uncaughtExceptionHandler = eh;
    }

    /**
     * Dispatch an uncaught exception to the handler. This method is
     * intended to be called only by the JVM.
     */
    private void dispatchUncaughtException(Throwable e) {
        getUncaughtExceptionHandler().uncaughtException(this, e);
    }

    /**
     * Removes from the specified map any keys that have been enqueued
     * on the specified reference queue.
     */
    static void processQueue(ReferenceQueue<Class<?>> queue,
                             ConcurrentMap<? extends
                             WeakReference<Class<?>>, ?> map)
    {
        Reference<? extends Class<?>> ref;
        while((ref = queue.poll()) != null) {
            map.remove(ref);
        }
    }

    /**
     *  Weak key for Class objects.
     **/
    static class WeakClassKey extends WeakReference<Class<?>> {
        /**
         * saved value of the referent's identity hash code, to maintain
         * a consistent hash code after the referent has been cleared
         */
        private final int hash;

        /**
         * Create a new WeakClassKey to the given object, registered
         * with a queue.
         */
        WeakClassKey(Class<?> cl, ReferenceQueue<Class<?>> refQueue) {
            super(cl, refQueue);
            hash = System.identityHashCode(cl);
        }

        /**
         * Returns the identity hash code of the original referent.
         */
        @Override
        public int hashCode() {
            return hash;
        }

        /**
         * Returns true if the given object is this identical
         * WeakClassKey instance, or, if this object's referent has not
         * been cleared, if the given object is another WeakClassKey
         * instance with the identical non-null referent as this one.
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == this)
                return true;

            if (obj instanceof WeakClassKey) {
                Object referent = get();
                return (referent != null) &&
                       (referent == ((WeakClassKey) obj).get());
            } else {
                return false;
            }
        }
    }


    // The following three initially uninitialized fields are exclusively
    // managed by class java.util.concurrent.ThreadLocalRandom. These
    // fields are used to build the high-performance PRNGs in the
    // concurrent code, and we can not risk accidental false sharing.
    // Hence, the fields are isolated with @Contended.

    /** The current seed for a ThreadLocalRandom */
    @sun.misc.Contended("tlr")
    long threadLocalRandomSeed;

    /** Probe hash value; nonzero if threadLocalRandomSeed initialized */
    @sun.misc.Contended("tlr")
    int threadLocalRandomProbe;

    /** Secondary seed isolated from public ThreadLocalRandom sequence */
    @sun.misc.Contended("tlr")
    int threadLocalRandomSecondarySeed;

    /* Some private helper methods */
    private native void setPriority0(int newPriority);
    private native void stop0(Object o);
    private native void suspend0();
    private native void resume0();
    private native void interrupt0();
    private native void setNativeName(String name);
}
