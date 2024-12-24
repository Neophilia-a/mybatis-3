package org.apache.ibatis.reflection;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

/**
 * 异常类的示例方法
 */
public class ExceptionUtilExampleTest {

  /**
   * 原始异常抛出
   */
  @Test
  public void testException() {
    try {
      ExceptionUtilExample.ExampleClass exampleClass = new ExceptionUtilExample.ExampleClass();
      Method method = ExceptionUtilExample.ExampleClass.class.getMethod("throwException");
      // 使用反射调用 这里会抛出一个 InvocationTargetException
      method.invoke(exampleClass);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  @Test
  public void testExceptionUtil() {
    try {
      ExceptionUtilExample.ExampleClass exampleClass = new ExceptionUtilExample.ExampleClass();
      Method method = ExceptionUtilExample.ExampleClass.class.getMethod("throwException");
      // 使用反射调用 这里会抛出一个 InvocationTargetException
      method.invoke(exampleClass);
    } catch (Exception e) {
      Throwable throwable = ExceptionUtil.unwrapThrowable(e);
      throwable.printStackTrace();
    }
  }
}
