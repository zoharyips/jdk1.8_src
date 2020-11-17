package com.zohar.grammer;

import com.zohar.util.PrintUtil;

import java.util.Arrays;

/**
 * <h3>反射测试类</h3>
 *
 * @author zohar
 * @version 1.0
 * 2020/11/17 21:59
 */
public class Reflect {

    public static void main(String[] args) {
//        PrintUtil.printMethodName(Reflect::testParentField);
    }


    private static class ParentClass {
        private final long paPrivateField;

        public ParentClass() {
            this.paPrivateField = System.currentTimeMillis();
        }

        public long getPaPrivateField() {
            return paPrivateField;
        }
    }

    private static class ChildClass extends ParentClass {}

    /**
     * <h3>测试能否获取父类数据域</h3>
     *
     * 当前类对象无法通过反射获取父类数据域，但可以先获取父类对象，再获取父类数据域。
     */
    public static void testParentField() {
        System.out.println("TEST_PARENT_FIELD");

        Class<ChildClass> childClazz = ChildClass.class;
        /* 反射无法直接通过当前类对象获取到其继承的父类数据域 */
        Arrays.stream(childClazz.getDeclaredFields()).forEach(System.out::println);
        /* 但是你可以通过获取其父类再获取父类的所有数据域 */
        Class<? super ChildClass> superClazz = childClazz.getSuperclass();
        Arrays.stream(superClazz.getDeclaredFields()).forEach(System.out::println);
    }
}
