package org.apache.ibatis.reflection;

/**
 * 测试异常的例子
 */
public class ExceptionUtilExample {


  // 一个简单的示例类，包含一个抛出异常的方法
  public static class ExampleClass {

    public void throwException() throws Exception {
      throw new IllegalArgumentException("This is an illegal argument exception.");
    }
  }
}
