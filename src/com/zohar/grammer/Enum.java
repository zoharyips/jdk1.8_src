package com.zohar.grammer;

import java.lang.reflect.Field;

/**
 * <h3>枚举测试类</h3>
 *
 * @author zohar
 * @version 1.0
 * 2020/11/17 21:29
 */
public class Enum {

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        System.out.println(Operation.PLUS);
        System.out.println(Operation.MINUS);
        System.out.println(Operation.TIMES);
        System.out.println(Operation.DIVIDE);
        System.out.println(Operation.TIMES.apply(100, 3.14));

        Class<Operation> operationClass = Operation.class;
        @SuppressWarnings("JavaReflectionMemberAccess")
        Field name = operationClass.getDeclaredField("name");
//        operationClass.getField
        name.setAccessible(true);
        System.out.println(name.get(Operation.PLUS));

    }

    private static enum Operation {

        PLUS("+") {
            @Override public double apply(double x, double y) { return x + y; }
        },
        MINUS("-") {
            @Override public double apply(double x, double y) { return x - y; }
        },
        TIMES("*") {
            @Override public double apply(double x, double y) { return x * y; }
        },
        DIVIDE("/") {
            @Override public double apply(double x, double y) { return x / y; }
        };

        private final String symbol;

        public abstract double apply(double x, double y);

        Operation(String symbol) {
            this.symbol = symbol;
        }


        @Override
        public String toString() {
            return super.toString()
                    + "@[symbol="
                    + this.symbol
                    + ", name="
                    + this.name()
                    + ", ordinary="
                    + this.ordinal()
                    + "]"
                    ;
        }
    }
}
