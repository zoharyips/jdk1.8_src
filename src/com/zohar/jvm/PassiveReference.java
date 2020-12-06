package com.zohar.jvm;

/**
 * <h3>被动引用示例</h3>
 *
 * @author zohar
 * @version 1.0
 * 2020/11/28 22:12
 */
public class PassiveReference {

    public static void main(String[] args) {

        /*
         * 常量在编译阶段会存入类的常量池中，本质上没有直接引用到定义常量的类，因此调用类中的常量不会造成类初始化
         */
        System.out.println(ParentClass.VALUE);

        System.out.println("==================");

        /*
         * 创建某类型数组，该类不会被初始化
         */
        //noinspection unused
        ParentClass[] parentClasses = new ParentClass[42];

        System.out.println("==================");

        /*
         * 通过子类调用父类静态属性或静态方法，子类不会被初始化
         */
        SubClass.test();

        System.out.println(parentClasses.getClass());
        System.out.println(Integer[].class.getName());
        System.out.println("byte => " + byte[].class.getName());
        System.out.println("byte[] => " + byte[][].class.getName());
        System.out.println("boolean => " + boolean[].class.getName());
        System.out.println("char => " + char[].class.getName());
        System.out.println("short => " + short[].class.getName());
        System.out.println("int => " + int[].class.getName());
        System.out.println("long => " + long[].class.getName());
        System.out.println("double => " + double[].class.getName());
        System.out.println("float => " + float[].class.getName());

        System.out.println(SubClass.VALUE);
    }


    private static class ParentClass {
        static final int VALUE = 0;

        static {
            System.out.println("Parent Class init.");
        }

        static void test() {
        }
    }

    private static class SubClass extends ParentClass {
        static final int VALUE = 10;
        static {
            System.out.println("Sub Class init.");
        }

        static {
            a = 20;

//            System.out.println(a);
        }
        static int a = 10;
    }

    private static interface MyInterface {
        public static void test() {
            System.out.println("FUNNY");
        }
    }
}
