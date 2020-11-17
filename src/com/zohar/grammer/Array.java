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
        objUndertakingAnyArr();

        objArrCastBack();
    }

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

    private static void objArrCastBack() {
        String[] strArr = new String[]{"HELLO WORLD"};
        Object[] objArr = strArr;
        System.out.println(Arrays.toString(objArr));
        strArr = (String[]) objArr;
        System.out.println(Arrays.asList(strArr));
    }
}
