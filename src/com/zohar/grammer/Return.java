package com.zohar.grammer;

/**
 * <h3>探究关于 Return 的一些有趣操作</h3>
 *
 * Return 的执行流程：
 * <ol>
 *     <li>先将返回值拷贝存储在寄存器中</li>
 *     <li>判断是否有其他操作需要执行</li>
 *     <li>对其他操作进行处理</li>
 *     <li>返回之前拷贝的返回值，如果是引用，则返回引用地址</li>
 * </ol>
 *
 * @author zohar
 * @version 1.0
 * 2020/11/21 11:09
 */
public class Return {

    private static class MyObject {
        int value;

        @Override
        public String toString() {
            return "MyObject[value=" + value + "]";
        }
    }

    private static int value;

    private static Integer valueTypeValue = 128;

    private static final MyObject refValue = new MyObject();

    public static void main(String[] args) {
        System.out.println("Before invoke nextValue value: " + value);

        System.out.println("Invoke nextValue value: " + nextValue());

        System.out.println("After nextValue value: " + value);

        System.out.println("Before invoke getValueWithTry value: " + value);

        System.out.println("Invoke getValueWithTry value: " + getValueWithTry());

        System.out.println("After getValueWithTry value: " + value);

        System.out.println("Before invoke getValueTypeValueWithTry value: " + valueTypeValue);

        System.out.println("Invoke getValueTypeValueWithTry value: " + getValueTypeValueWithTry());

        System.out.println("After getValueTypeValueWithTry value: " + valueTypeValue);

        System.out.println("Before invoke getRefValueWithTry value: " + refValue);

        System.out.println("Invoke getRefValueWithTry value: " + getRefValueWithTry());

        System.out.println("After getRefValueWithTry value: " + refValue);
    }

    private static int nextValue() {
        return value++;
    }

    private static int getValueWithTry() {
        try {
            return value;
        } finally {
            value++;
        }
    }

    private static int getValueTypeValueWithTry() {
        try {
            return valueTypeValue;
        } finally {
            valueTypeValue++;
        }
    }

    private static MyObject getRefValueWithTry() {
        try {
            return refValue;
        } finally {
            refValue.value++;
        }
    }

}
