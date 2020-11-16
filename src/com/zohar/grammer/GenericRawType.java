package com.zohar.grammer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * <h3>泛型原生态类型测试类</h3>
 * 每个泛型类型都存在一个原生态类型，原生态类型就像从泛型类中删除所有泛型信息一样，
 * 它们存在的目的是为了兼容泛型出现之前的类。<br/><br/>
 *
 * 这种奇特的类型会导致泛型类中可以塞入任何类型的对象。同时代码在编译期间不会检测到任何 Error，
 * 同时在运行时可以正常地使用该原生态类型实例。但是在对实例中保存的对象进行类型转换时，
 * 如果转换错误就将抛出异常，这是非常危险的一件事。<br/><br/>
 *
 * 原生态类型和以 Object 为参数的参数化类型的区别就是，前者逃避了泛型检查，
 * 后者明确告知编译器它能接收一切对象。这是两个概念，因为后者依旧会进行类型检查和类型擦除，
 * 只不过擦除和没擦除的效果并没有什么两样而已。<br/>
 * {@code List<String>} 可以作为 List 的的值类型，但却不能作为 {@code List<Object>}
 * 的子类型，因为泛型依旧有子类型化的规则。也正因为如此，原生态类型会让参数化的子类型失去安全性，
 * 因为可以将参数化的子类型向上转型为原生态类型，再加入子类型不允许的元素到子类型中。
 *
 *
 * @author zohar
 * @version 1.0
 * 2020/11/16 0:22
 */
public class GenericRawType {
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        ArrayList arrayList = new ArrayList(8);

        arrayList.add(new Cat());
        arrayList.add(new Dog());
        Object obj0 = arrayList.get(0); // 在使用时如果类型转换错误肯定会抛出异常
        Object obj1 = arrayList.get(1); // 在使用时如果类型转换错误肯定会抛出异常
        System.out.println(obj0.getClass() + " : " + obj1.getClass());
        System.out.println(Cat.class.getDeclaredField("NAME").get(obj0));
        System.out.println(Dog.class.getDeclaredField("NAME").get(obj1));

        /* 原生态类型可以接参数化子类型，此时可绕过参数化子类型的类型检查和类型擦除 */
        List list = new LinkedList<String>(); // 使用 List<Object> list = new LinkedList<String>() 则会报错
        list.add("HELLO RAW TYPE");
        list.add(42);
        LinkedList<String> subTyping = (LinkedList<String>) list;
        System.out.println(subTyping.get(0)); // 使用 System.out.println(subTyping.get(1)) 必将抛出异常

    }

    private static class Cat {
        public final String NAME = "CAT";
    }
    private static class Dog {
        public final String NAME = "DOG";
    }
}
