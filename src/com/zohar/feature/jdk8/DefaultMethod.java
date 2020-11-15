package com.zohar.feature.jdk8;

/**
 * <h3>接口缺省方法演示类</h3>
 * Jdk 8 开始，支持为接口方法添加默认实现，以避免编译没报错但运行失败的情况。
 * 如果设置默认方法，接口的实现类可能根本不知道接口中定义了这个默认方法，
 * 但是如果调用者直到接口存在该方法，
 * 则有可能因为未对方法进行适当重写使得表现出与预期不同的效果，甚至是发生异常。
 *
 * @author zohar
 * @version 1.0
 * 2020/11/15 21:07
 */
public class DefaultMethod {

    public static void main(String[] args) {
        new TestNoneOverrideImpl().test();
        new TestOverrideImpl().test();
    }

    private static class TestNoneOverrideImpl implements TestInterface {}

    private static class TestOverrideImpl implements TestInterface {
        @Override
        public void test() {
            System.out.println(this.getClass().getSimpleName() + ": I don't know the method has a default version!");
        }
    }

    private interface TestInterface {
        default void test() {
            System.out.println(this.getClass().getSimpleName() + ": Just test by default");
        }
    }
}