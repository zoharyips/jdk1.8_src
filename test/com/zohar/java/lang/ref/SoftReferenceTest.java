package com.zohar.java.lang.ref;

import org.junit.jupiter.api.Test;

import java.lang.ref.SoftReference;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * <h3>测试软引用</h3>
 *
 * @author zohar
 * @version 1.0
 * 2020/12/5 21:49
 */
class SoftReferenceTest {

    @Test
    public void test() {
        List<byte[]> memBlock10MList = new LinkedList<>();
        SoftReference<List<byte[]>> softRef = new SoftReference<>(memBlock10MList);
        memBlock10MList = null;
        System.gc();
        // 即使对象为空且 GC 也不会回收软引用，仅在 OOM 之前会回收
        System.out.println("list is null: " + (softRef.get() == null));
        while ((memBlock10MList = softRef.get()) != null) {
            memBlock10MList.add(new byte[1024 * 1024 * 10]);
            System.out.println("Memory list size: " + (memBlock10MList.size() * 10) + "MB");
            memBlock10MList = null;
        }
        System.out.println("List is collected by GC before OOM.");
    }
}