package com.zohar.java.lang.ref;

import org.junit.jupiter.api.Test;

import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.reflect.Field;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <h3>测试虚引用</h3>
 *
 * @author zohar
 * @version 1.0
 * 2020/11/30 22:02
 */
class PhantomReferenceTest {

    @Test
    void get() throws InterruptedException {
        Integer obj = 2000;
        ReferenceQueue<Object> queue = new ReferenceQueue<>();
        PhantomReference<Object> phantomReference = new PhantomReference<>(obj, queue);

        System.out.println("obj:" + obj);
        System.out.println("queue:" + queue.poll());
        System.out.println("reference:" + phantomReference);
        System.out.println("reference.get():" + phantomReference.get());


        SomeThing someThing = new SomeThing();
        PhantomReference<Object> sthReference = new PhantomReference<>(someThing, queue);

        System.out.println("sth obj:" + someThing);
        System.out.println("sth queue:" + queue.poll());
        System.out.println("sth reference:" + sthReference);
        System.out.println("sth reference.get():" + sthReference.get());

        obj = null;
        someThing = null;
        System.gc();
        Thread.sleep(3000);
        System.out.println("obj:" + obj);
        System.out.println("queue:" + queue.poll());
        System.out.println("reference:" + phantomReference);
        System.out.println("reference.get():" + phantomReference.get());
        System.out.println("sth obj:" + someThing);
        System.out.println("sth queue:" + queue.poll());
        System.out.println("sth reference:" + sthReference);
        System.out.println("sth reference.get():" + sthReference.get());

        System.out.println("---- Second time GC ----");
        System.gc();
        Thread.sleep(3000);
        System.out.println("sth obj:" + someThing);
        System.out.println("sth queue:" + queue.poll());
        System.out.println("sth reference:" + sthReference);
        System.out.println("sth reference.get():" + sthReference.get());

    }

    private static class SomeThing {
        @Override
        protected void finalize() {
            System.out.println("I'm being collected.");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Object obj = new Object();
        ReferenceQueue queue = new ReferenceQueue();
        PhantomReference reference = new PhantomReference(obj, queue);
        System.out.println("obj:" + obj);
        System.out.println("queue:" + queue.poll());
        System.out.println("reference:" + reference);
        System.out.println("reference.get():" + reference.get()); //为null，其余和虚引用一样
        obj = null;
        //不要使用下一行代码，因为下一行代码会导致reference对象被GC回收，导致queue的poll()返回空
//        reference = null; //这句话会导致下面的queue.poll()返回空，即reference对象都被回收了！！！
        System.gc();
        Thread.sleep(2000);
        System.out.println("-----after gc-----------");
        System.out.println("obj:" + obj);
        System.out.println("queue:" + queue.poll());
        System.out.println("reference:" + reference);
        System.out.println("reference.get():" + reference.get());
    }
}