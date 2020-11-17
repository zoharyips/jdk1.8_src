package com.zohar.grammer;

import java.util.Arrays;

/**
 * <h3>数组测试类</h3>
 *
 * @author zohar
 * @version 1.0
 * 2020/11/16 22:06
 */
public class Array {
    public static void main(String[] args) {

        parentClassOfArr();

        objArrCastBack();

        objUndertakingAnyArr();

    }

    /**
     * 测试数组的父类，同时尝试获取数组的 length 数据域：<br/>
     *
     * 无法通过反射获取数组的 length 数据域，原因应该是数组的长度在创建时予以确定且不可更改，
     *
     *
     */
    private static void parentClassOfArr() {
        Class<String[]> strArrClass = String[].class;
        /* 数组类对象无法反射获取 length 属性 */
        Arrays.stream(strArrClass.getDeclaredFields()).forEach(System.out::println);
        Class<? super String[]> superclass = strArrClass.getSuperclass();
        /* 数组类的父类直接就是 Object */
        System.out.println(superclass.getName());
    }

    /**
     * 测试 Object 可以承接所有数组，无论什么维度。
     * Object[] 也可以承接所有数组。
     */
    private static void objUndertakingAnyArr() {

        /* Object 类型可以承接所有数组类型，包括多维数组，因为 Object 是所有类的父类 */
        Object obj;
        obj = new String[]{"HELLO ARR"};
        System.out.println(obj.getClass());
        obj = new String[][]{{"HELLO ARR"}};
        System.out.println(obj.getClass());

        /* Object[] 类型也可以承接所有数组类型 */
        @SuppressWarnings("MismatchedReadAndWriteOfArray")
        Object[] objArr;
        objArr = new String[]{"HELLO ARR"};
        System.out.println(objArr.getClass());

        /* 由于 Object[] 中的元素为 Object，Object 同样可以承接数组，因此 Object[] 可以承接多维数组 */
        objArr = new String[][]{{"HELLO ARR"}};
        System.out.println(objArr.getClass());
        objArr = new String[][][]{{{"HELLO ARR"}}};
        System.out.println(objArr.getClass());

        /* Object[][] 类型也可以承接二维数组 */
        @SuppressWarnings("MismatchedReadAndWriteOfArray")
        Object[][] twoDimensionalArr;
        twoDimensionalArr = new String[][]{{"HELLO ARR"}};
        System.out.println(twoDimensionalArr.getClass());

        /* 由于 Object[][] 中的元素为 Object，Object 可以数组，因此 Object[][] 可以承接多维数组，但不能承接一维数组 */
        twoDimensionalArr = new String[][][]{{{"HELLO ARR"}}};
        System.out.println(twoDimensionalArr.getClass());
    }

    /**
     * 测试 T[] 对象转换为 Object[] 对象后能否转回 T[] 对象：<br/>
     *
     * 当然可以，对象的类型在创建最初确定，不管是泛型还是多态，都无法改变运行过程中对象的类型。
     * 由于对象创建时为 T[] 类型，那么虚拟机在转换回 T[] 时不会抛出 ClassCastException，
     * 如果抛出异常，绝对是由于某个环节造成了堆污染。
     */
    private static void objArrCastBack() {
        String[] strArr = new String[]{"HELLO WORLD"};
        Object[] objArr = strArr;
        System.out.println(Arrays.toString(objArr));
        strArr = (String[]) objArr;
        System.out.println(Arrays.asList(strArr));
    }
}
