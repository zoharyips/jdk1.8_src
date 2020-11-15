package com.zohar.java.lang;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

/**
 * <h3>Object 的测试类</h3>
 * 用于测试 Object 的一些方法
 *
 * @author zohar
 * @version 1.0
 * 2020/11/15 10:14
 */
public class ObjectTest {

    @Test
    void cloneTest() {

        SingleFinalFieldModel object = new SingleFinalFieldModel(new int[]{42}, new int[]{42, 42});
        SingleFinalFieldModel clone = object.clone();
        System.out.println("Object: " + object.toString());
        System.out.println("Clone: " + clone.toString());
        System.out.println("Compare normal field: "
                + Arrays.toString(object.normalField)
                + " == "
                + Arrays.toString(clone.normalField)
                + " => "
                + (object.normalField == clone.normalField));
        System.out.println("Compare final field: "
                + Arrays.toString(object.finalField)
                + " == "
                + Arrays.toString(clone.finalField)
                + " => "
                + (object.finalField == clone.finalField));
    }

    /**
     * 主线程在 lock 进行睡眠指定时间之后，其他线程不去唤醒，看主线程是否会苏醒并竞争锁
     *
     * @throws InterruptedException 中断处理异常
     */
    @Test
    void waitTestTimeout() throws InterruptedException {
        Object lock = new Object();

        new Thread(() -> {
            try {
                /* 睡一会保证主线程能拿到锁 */
                Thread.sleep(100);
                synchronized (lock) {
                    Thread.sleep(3000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        synchronized (lock) {
            System.out.println("Main Thread release the lock and sleep for two seconds.");
            lock.wait(2000);
            System.out.println("Main Thread grain the lock again.");
        }
    }

    /**
     * 主线程获得多个锁时，在调用某个锁的 wait 方法会不会释放其他锁
     */
    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    @Test
    void waitTestRelease() {
        Object lock1 = new Object();
        Object lock2 = new Object();

        new Thread(() -> {
            try {
                /* 睡一会，先让主线程拿到锁 */
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (lock1) {
                System.out.println("Other Thread grain the lock1");
                System.out.println("Other Thread attempt to weak up Main Thread");
                lock1.notifyAll();
            }
            System.out.println("Other Thread release the lock1");
        }).start();

        /* 第三者，调停死锁：睡 5 秒，5 秒后向主线程发出中断，让主线程苏醒并释放 */
        Thread mainThread = Thread.currentThread();
        new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Monitor Thread weak up and try to send a interrupt to Main Thread");
            mainThread.interrupt();
        }).start();

        synchronized (lock1) {
            synchronized (lock2) {
                System.out.println("Main Thread wait on lock2");
                try {
                    lock2.wait();
                } catch (InterruptedException e) {
                    System.out.println("Main Thread was weak up by interrupt and try to wait on lock1");
                    try {
                        lock1.wait();
                        System.out.println("Main Thread was weak up by notify on lock1");
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
                System.out.println("Main Thread grain the lock2 again");
            }
            System.out.println("Main Thread release the lock2");
        }
    }

    private static class SingleFinalFieldModel implements Cloneable {

        public final int[] finalField;
        public int[] normalField;

        /**
         * <h3>重写 clone 方法</h3>
         * <qoute>重写方法的可见性可以扩大不能缩小，且支持协变返回类型，即返回类型可改写成子类类型。</qoute>
         *
         * @return 克隆对象
         */
        @Override
        @SuppressWarnings("MethodDoesntCallSuperMethod")
        public SingleFinalFieldModel clone() {
//            SingleFinalFieldModel clone = super.clone();
//            clone().finalField = finalField.clone();
//            clone().normalField = normalField.clone();
            return new SingleFinalFieldModel(finalField.clone(), normalField.clone());
        }

        public SingleFinalFieldModel(int[] finalField, int[] normalField) {
            this.finalField = finalField;
            this.normalField = normalField;
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
