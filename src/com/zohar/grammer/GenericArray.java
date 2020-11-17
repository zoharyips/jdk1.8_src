package com.zohar.grammer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <h3>泛型数组演示类</h3>
 *
 * 泛型数组仅可用于声明，无法用于实例化，如 new T[] 这样的操作。
 * 申请数组需要确定元素的类型以申请连续的空间，泛型数组无法得知数组元素的类型，
 * 因此不可能成功申请。<br/>
 * 无法创建参数化类型数组，但是可以创建无限参数类型数组。
 *
 * @author zohar
 * @version 1.0
 * 2020/11/16 21:42
 */
public class GenericArray {
    public static void main(String[] args) {

        /* 无法创建泛型数组 List<String>[]，但是创建无限通配类型数组是合法的 */
        List<?>[] listArray = new List<?>[2];
        List<String> stringList = new ArrayList<String>() {{
            add("HELLO ARRAY");
        }};
        List<Long> longList = new ArrayList<Long>() {{
            add(42L);
        }};
        listArray[0] = stringList;
        listArray[1] = longList;
        Arrays.stream(listArray).forEach(list -> System.out.println(list.get(0)));

        GenericArr<String> stringGenericArr;
        String[] strArr;
        Object[] objArr;
        stringGenericArr = new GenericArr<>(new String[]{"NEW STR ARR"});
        /* 你知道并且保证它是字符串元素的数组，编译器也知道，此处运行不会报错 */
        strArr = stringGenericArr.getGenericArr();
        System.out.println(Arrays.toString(strArr));
        stringGenericArr = new GenericArr<>(Collections.singletonList("NEW STR ARR"));
        /*
         * 你知道并且保证它是字符串元素的数组，编译器也知道，但是你必须一个一个地进行转化而不能强转为 String 数组：
         * strArr = stringGenericArr.getGenericArr(); // ClassCastException
         *
         * 原因在于，虽然 genericArr 的元素确确实实是字符串对象，但是此时它的类型是 Object[]，
         * Object[] 数组自然无法转换为 String[]。
         * List 容器中通过初始化 Object[] 数组来存储所有的泛型参数元素，
         * 但它的 toArray 方法没采用泛型返回值 + 递归拷贝的方式来返回，反而是直接复制了它内部所维护的 Object[] 数组，
         * 这就造成了，我们明知道数组的元素是 String，但却无法类型转换问 String[] 的情况。
         *
         * 所以，我们只能帮 List 主动做这件事：通过遍历 List 本身或者遍历 toArray 导出的 Object[] 数组，
         * 逐一将元素添加到我们想要的数组中去。
         *
         * 但最好的做法是：不要拿数组去承接 List 的元素。
         */
        objArr = stringGenericArr.getGenericArr();
        for (int i = 0; i < objArr.length; i++) {
            strArr[i] = (String) objArr[i];
        }
        System.out.println(Arrays.toString(strArr));
    }

    private static class GenericArr<T> {
        // 会造成堆污染
        private final T[] genericArr;

        public GenericArr(List<T> genericList) {
            Object[] objects = genericList.toArray();
            //noinspection unchecked
            this.genericArr = (T[]) objects;
        }

        public GenericArr(T[] genericArr) {
            this.genericArr = genericArr;
        }

        public T[] getGenericArr() {
            return genericArr;
        }
    }
}
