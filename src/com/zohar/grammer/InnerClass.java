package com.zohar.grammer;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * <h3>Java 内部类测试类</h3>
 * <ol>
 *     <li>内部类中依旧可以使用内部类。</li>
 *     <li>要获取非静态内部类实例必须借助外围类的实例方法获取。</li>
 *     <li>
 *         存在着“类名.非静态元素”的语法，但仅限内部类可以做到，因为非静态内部类本质上依旧是一个类，
 *         无论是任何一个类的实例，非静态内部类用的都是同一个，因此支持“Class.NonStaticSubClass”的语法。
 *     </li>
 *     <li>
 *         所有非静态内部类实例都会持有所属外围类实例的隐式引用，该引用作为非静态内部类的第一个非可见数据域存在。
 *         这可能导致外围类实例在应该被回收时无法被回收，但绑定该实例的作用也很明显，
 *         非静态内部类的方法可以使用该外围类的所有数据域而没有作用域限制，包括私有域，因为是自己人。
 *     </li>
 * </ol>
 *
 * @author zohar
 * @version 1.0
 * 2020/11/15 21:32
 */
public class InnerClass {

    public static void main(String[] args) {

        System.out.println("Get class directly by peripheral class: ");

        System.out.println(NormalClass.InnerStaticSubClass.class);
        System.out.println(NormalClass.InnerNonStaticSubClass.class);

        System.out.println("====================================");
        System.out.println("Two different way to create instance");

        NormalClass.InnerStaticSubClass staticInstance = new NormalClass.InnerStaticSubClass();
        System.out.println(staticInstance);

        NormalClass outerClassInstance = new NormalClass();
        NormalClass.InnerNonStaticSubClass nonStaticInstance = outerClassInstance.getInnerNonStaticClass();
        System.out.println(nonStaticInstance);
        System.out.println("Outer class private data field: " + nonStaticInstance.touchOuterClassPrivateField());

        System.out.println("==========================================================");
        System.out.println("Get outer class instance by nonstatic inner class instance");

        /* 尝试从非静态内部类那获取所绑定外围类的实例 */
        Field[] fields = NormalClass.InnerNonStaticSubClass.class.getDeclaredFields();
        Optional<Field> outerClassReferenceField
                = Arrays.stream(fields)
                .filter(field -> Objects.equals(field.getName(), "this$0"))
                .findFirst();
        outerClassReferenceField.ifPresent(outerClassReference -> {
            try {
                System.out.println("Implicit reference visibility: " + outerClassReference.isAccessible());
                System.out.println("Implicit reference type: " + outerClassReference.getType());
                outerClassReference.setAccessible(true);
                NormalClass o = (NormalClass) outerClassReference.get(nonStaticInstance);
                System.out.println(o == outerClassInstance);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });

        System.out.println("=================================================");
        System.out.println("Create private nonstatic inner subclass by reflex");

        /* 通过反射创建私有内部类实例 */
        Class<?>[] classes = NormalClass.class.getDeclaredClasses();
        Optional<Class<?>> nonStaticSubClass = Arrays.stream(classes)
                .filter(clazz -> clazz.getSimpleName().contains("Private"))
                .findFirst();
        nonStaticSubClass.ifPresent(clazz -> {
            try {
                /* 对于非静态内部类而言，其构造函数的第一个参数为其外围类的类实例，因为非静态内部类必须是由其外围类实例创建的。 */
                Constructor<?> nonParamConstructor = clazz.getDeclaredConstructor(NormalClass.class);
                nonParamConstructor.setAccessible(true);
                /* 传递外围类实例给构造函数 */
                NormalClass.PrivateInnerNonStaticSubClass o =
                        (NormalClass.PrivateInnerNonStaticSubClass) nonParamConstructor.newInstance(outerClassInstance);
                System.out.println(o.getClass());
            } catch (InstantiationException
                    | IllegalAccessException
                    | NoSuchMethodException
                    | InvocationTargetException e) {
                e.printStackTrace();
            }
        });

    }

    @SuppressWarnings("FieldCanBeLocal")
    public static class NormalClass {

        private final int PRIVAT_FIELD = 42;

        public InnerNonStaticSubClass getInnerNonStaticClass() {
            return new InnerNonStaticSubClass();
        }

        @SuppressWarnings("InnerClassMayBeStatic")
        public class InnerNonStaticSubClass {
            public int touchOuterClassPrivateField() {
                return PRIVAT_FIELD;
            }
        }

        public static class InnerStaticSubClass {}

        @SuppressWarnings("InnerClassMayBeStatic")
        private class PrivateInnerNonStaticSubClass {}
    }
}
