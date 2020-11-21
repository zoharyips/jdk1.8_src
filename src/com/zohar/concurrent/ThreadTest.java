package com.zohar.concurrent;

import com.zohar.util.PrintUtil;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;

/**
 * <h3>Thread 测试类</h3>
 *
 * @author zohar
 * @version 1.0
 * 2020/11/17 23:07
 */
public class ThreadTest {

    /**
     * 启动线程，线程在运行时再次启动将抛出 {@link IllegalThreadStateException}
     *
     * @throws InterruptedException 中断
     */
    @Test
    public void startTwice() throws InterruptedException {
        PrintUtil.printMethodName();
        Thread thread = new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println(new Date());
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        System.out.println("Start thread first time: ");
        thread.start();
        System.out.println("Start thread second time: ");
        thread.start();
        Thread.sleep(6000);
    }

    /**
     * 启动线程，线程在运行后再次启动也会抛出 {@link IllegalThreadStateException}
     *
     * @throws InterruptedException 中断
     */
    @Test
    public void repeatStart() throws InterruptedException {
        PrintUtil.printMethodName();
        Thread thread = new Thread(() -> System.out.println(new Date()));
        System.out.println("Start thread first time: ");
        thread.start();
        Thread.sleep(1000);
        System.out.println("Restart thread: ");
        thread.start();
    }

    /**
     * 通过反射强制将线程状态改为未执行，也无法启动线程
     *
     * @throws InterruptedException 中断异常
     * @throws NoSuchFieldException 无该数据域异常
     * @throws IllegalAccessException 非法访问异常
     */
    @Test
    public void forceRestartThread() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        PrintUtil.printMethodName();
        Thread thread = new Thread(() -> System.out.println(new Date()));
        System.out.println("Start thread first time: ");
        thread.start();
        Thread.sleep(1000);
        Field threadStatus = Thread.class.getDeclaredField("threadStatus");
        threadStatus.setAccessible(true);
        threadStatus.set(thread, 0);
        System.out.println("Restart thread: ");
        thread.start();
    }

    /**
     * 测试在线程 yield 时是否会释放锁。
     *
     */
    @Test
    public void testLockWhenYield() throws InterruptedException {

        final Object lock = new Object();

        new Thread(() -> {
            synchronized (lock) {
                System.out.println("Thread one got the lock");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Thread one planning to yield");
                Thread.yield();
                System.out.println("Thread one yield finished.");
            }
        }).start();

        new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Thread two weak up and is going to get the lock");
            synchronized (lock) {
                System.out.println("Thread two got the lock");
            }
        }).start();

        Thread.sleep(3000);
    }

    /**
     * 线程 Interrupt 自身，没事发生。
     *
     * @throws InterruptedException 中断异常
     */
    @Test
    public void interruptItself() throws InterruptedException {
        new Thread() {
            @Override
            public void run() {
                try {
                    System.out.println("Thread is running...");
                    System.out.println("Thread is trying to interrupt itself.");
                    this.interrupt();
                    System.out.println("The thread is running normally.");
                } catch (Exception e) {
                    //noinspection ConstantConditions
                    if (e instanceof InterruptedException) {
                        System.out.println("Thread have catch the interruptedException.");
                    } else {
                        System.out.println("The thread have catch the other exception.");
                        e.printStackTrace();
                    }
                }
            }
        }.start();

        Thread.sleep(100);
    }

    /**
     * 线程可以叫其他线程唤醒自己
     */
    @Test
    public void askOtherToInterruptItself() {
        Thread thread = Thread.currentThread();
        new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Interrupt the parent thread.");
            thread.interrupt();
        }).start();
        try {
            Thread.sleep(2000);
            System.out.println("The parent thread is weak up by itself");
        } catch (InterruptedException e) {
            System.out.println("The parent thread is weak up by its child");
        }
    }

    /**
     * 同时向一个线程发生中断没有任何异常
     */
    @Test
    public void interruptTogether() {
        Thread thread = Thread.currentThread();
        new Thread(() -> {
            for (int i = 0; i < 1000000; i++) {
                thread.interrupt();
            }
            System.out.println("Interrupt parent finished");
        }).start();

        for (int i = 0; i < 10000000; i++) {
            thread.interrupt();
        }
        System.out.println("Interrupt myself finished");
    }

    /**
     * 测试 interrupt 方法消耗的时间
     */
    @Test
    public void interruptCostTime() {
        long l = System.nanoTime();
        //noinspection StatementWithEmptyBody
        for (int i = 0; i < 1000000; i++) {}
        long forLoopCost = System.nanoTime() - l;
        Thread thread = Thread.currentThread();
        l = System.nanoTime();
        for (int i = 0; i < 1000000; i++) {
            thread.interrupt();
        }
        long interruptLoopCost = System.nanoTime() - l;
        System.out.println("for loop cost time: " + forLoopCost);
        System.out.println("interrupt loop cost time: " + interruptLoopCost);
        System.out.println("ratio: " + (double)interruptLoopCost / (double)forLoopCost);
    }

    @Test
    public void isInterruptedTest() {
        Thread thread = Thread.currentThread();
        new Thread(() -> {
            for (int i = 0; i < 1000000; i++) {
                thread.interrupt();
            }
        }).start();
        //noinspection StatementWithEmptyBody,LoopConditionNotUpdatedInsideLoop
        while (!thread.isInterrupted()) {}
        System.out.println("I'm interrupted!");
    }

    /**
     * 测试若线程被中断但无法执行时的状态：
     * 状态为：false
     */
    @Test
    public void testInterruptedBlock() {
        final Object lock = new Object();
        Thread thread = Thread.currentThread();
        new Thread(() -> {
            System.out.println("Trying to interrupt main thread.");
            synchronized (lock) {
                thread.interrupt();
                System.out.println("Interrupted, going to sleep.");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Weak up, main thread interrupted status: " + thread.isInterrupted());
            }
        }).start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            synchronized (lock) {
                System.out.println("Main thread was interrupted!");
            }
        }
    }

    /**
     * 程序在执行中断处理程序的时候，其 interrupt status 为 false
     * @throws InterruptedException 中断异常
     */
    @Test
    public void testInterruptStatusPeriod() throws InterruptedException {
        Thread thread = Thread.currentThread();
        new Thread(() -> {
            /* 为了让主线程先睡着 */
            try {
                Thread.sleep(100);
                System.out.println("Before interrupt status: " + thread.isInterrupted());
                thread.interrupt();
                /* 为了确保主线程在中断处理程序里面睡着 */
                Thread.sleep(100);
                System.out.println("While interrupt status: " + thread.isInterrupted());
                /* 为了确保主线程退出中断程序 */
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("After interrupt status: " + thread.isInterrupted());

        }).start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            System.out.println("Main Thread enter the interrupt handle process.");
            System.out.println("Myself status: " + thread.isInterrupted());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
        }
        System.out.println("Main Thread exit the interrupt handle process.");
        Thread.sleep(2000);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testSuspendAndResume() {
        Thread thread = Thread.currentThread();
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Weak up the main thread.");
            thread.resume();
            System.out.println("Weak up finished.");
        }).start();
        thread.suspend();
    }

    /**
     * 测试 suspend 和 resume 造成死锁
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testDeadLockOnSuspendAndResume() {
        final Object lock = new Object();
        Thread thread = Thread.currentThread();
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Weak up the main thread, trying to get the lock");
            synchronized (lock) {
                thread.resume();
            }
            System.out.println("Weak up finished.");
        }).start();
        synchronized (lock) {
            thread.suspend();
        }
    }

    @Test
    public void getThreadGroupTest() throws InterruptedException {
        Thread thread = Thread.currentThread();
        ThreadGroup threadGroup = thread.getThreadGroup();
        System.out.println(threadGroup);
        Thread thread1 = new Thread(() -> System.out.println("Good good study, day day up!"));
        thread1.start();
        Thread.sleep(100);
        System.out.println(thread1.getThreadGroup());
        Thread thread2 = new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread2.start();
        Thread.sleep(100);
        System.out.println(thread2.getThreadGroup());
        //noinspection deprecation
        thread2.stop();
        System.out.println(thread2.getThreadGroup());
    }

    @Test
    public void enumerateTest() {
        Thread[] threads = new Thread[10];
        Thread.enumerate(threads);
        Arrays.stream(threads).forEach(System.out::println);
    }

    @Test
    public void joinTest() throws InterruptedException {
        Thread thread1 = new Thread(() -> {
            System.out.println("Thread started.");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Thread ended.");
        });
        thread1.start();
        System.out.println("Main thread prepare to join as most 3s");
        thread1.join(3000);
        System.out.println("Main thread continue to run.");
        Thread.sleep(3000);
    }

    @Test
    public void isAliveTest() throws InterruptedException {
        Thread thread = Thread.currentThread();
        new Thread(() -> {
            for (int i = 0; i < 9; i++) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(thread.isAlive());
            }
        }).start();
        System.out.println("Main thread start to sleep");
        Thread.sleep(1000);
        System.out.println("Main thread sleep finished");
    }

    /**
     * 测试永久让出 CPU：
     * 并非永久让出。当 join 线程执行结束时，调用 join 的线程可以继续执行
     */
    @Test
    public void joinForever() throws InterruptedException {
        Thread thread1 = new Thread(() -> {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("HELLO WORLD");
        });
        thread1.start();
        thread1.join(0);
        System.out.println("Join finished");
    }

    @Test
    public void dumpStackTest() {
        Thread.dumpStack();
    }
}
