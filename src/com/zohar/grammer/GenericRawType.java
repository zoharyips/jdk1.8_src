package com.zohar.grammer;

import java.util.ArrayList;

/**
 * <h3>泛型原生态类型测试类</h3>
 * 每个泛型类型都存在一个原生态类型，原生态类型就像从泛型类中删除所有泛型信息一样，
 * 它们存在的目的是为了兼容泛型出现之前的类。<br/><br/>
 *
 * 这种奇特的类型会导致泛型类中可以塞入任何类型的对象。同时代码在编译期间不会检测到任何 Error，
 * 同时在运行时可以正常地使用该原生态类型实例。但是在对实例中保存的对象进行类型转换时，
 * 如果转换错误就将抛出异常，这是非常危险的一件事。
 *
 *
 * @author zohar
 * @version 1.0
 * 2020/11/16 0:22
 */
public class GenericRawType {
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void main(String[] args) {
        ArrayList list = new ArrayList(8);

        list.add(new Cat());
        list.add(new Dog());
        Object obj0 = list.get(0);
        Object obj1 = list.get(1);
        System.out.println("Class cast correctly: ");
        System.out.println(((Cat) obj0).NAME);
        System.out.println(((Dog) obj1).NAME);
        System.out.println("Class case mistakenly: ");
        //noinspection ConstantConditions
        System.out.println(((Dog) obj0).NAME);
        //noinspection ConstantConditions
        System.out.println(((Cat) obj1).NAME);
    }

    private static class Cat {
        public final String NAME = "CAT";
    }
    private static class Dog {
        public final String NAME = "DOG";
    }
}
