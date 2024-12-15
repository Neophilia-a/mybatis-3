/*
 *    Copyright 2009-2023 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.reflection.factory;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.ibatis.reflection.ReflectionException;
import org.apache.ibatis.reflection.Reflector;

/**
 * @author Clinton Begin
 */
public class DefaultObjectFactory implements ObjectFactory, Serializable {

  private static final long serialVersionUID = -8855120656740914948L;

  @Override
  public <T> T create(Class<T> type) {
    return create(type, null, null);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> T create(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
    // 获得需要常见的类
    Class<?> classToCreate = resolveInterface(type);
    // 创建对象
    return (T) instantiateClass(classToCreate, constructorArgTypes, constructorArgs);
  }

  /**
   *
   *
   * @param type                  对应的类的类型
   * @param constructorArgTypes   对应构造器的参数类型
   * @param constructorArgs        对应构造器的参数值
   * @return
   * @param <T> 生成的对应的对象
   */
  private <T> T instantiateClass(Class<T> type, List<Class<?>> constructorArgTypes, List<Object> constructorArgs) {
    try {
      Constructor<T> constructor;
      // 通过无参构造方法创建对象
      if (constructorArgTypes == null || constructorArgs == null) {
        constructor = type.getDeclaredConstructor();
        try {
          // 根据无参构造器 创建对象
          return constructor.newInstance();
        } catch (IllegalAccessException e) {
          // 判断是否有权限去开启访问私有方法的权限
          if (Reflector.canControlMemberAccessible()) {
            // 开启反射访问无参的私有方法
            constructor.setAccessible(true);
            // 根据私有的无参构造器创建对象
            return constructor.newInstance();
          }
          throw e;
        }
      }
      // 根据参数类型拿到对应的有参构造器 创建指定的类对象
      constructor = type.getDeclaredConstructor(constructorArgTypes.toArray(new Class[0]));
      try {
        // 根据上面得到的有参构造器传入对应的值  创建对象
        return constructor.newInstance(constructorArgs.toArray(new Object[0]));
      } catch (IllegalAccessException e) {
        // 判断是否有权限去开启访问私有方法的权限
        if (Reflector.canControlMemberAccessible()) {
          // 开启反射访问无参的私有方法的权限
          constructor.setAccessible(true);
          // 根据上面得到的有参构造器传入对应的值  创建对象
          return constructor.newInstance(constructorArgs.toArray(new Object[0]));
        }
        throw e;
      }
    } catch (Exception e) {
      // 拼接参数类型
      String argTypes = Optional.ofNullable(constructorArgTypes).orElseGet(Collections::emptyList).stream()
          .map(Class::getSimpleName).collect(Collectors.joining(","));
      // 拼接参数值
      String argValues = Optional.ofNullable(constructorArgs).orElseGet(Collections::emptyList).stream()
          .map(String::valueOf).collect(Collectors.joining(","));
      // 抛出错误
      throw new ReflectionException("Error instantiating " + type + " with invalid types (" + argTypes + ") or values ("
          + argValues + "). Cause: " + e, e);
    }
  }

  protected Class<?> resolveInterface(Class<?> type) {
    Class<?> classToCreate;
    if (type == List.class || type == Collection.class || type == Iterable.class) {
      classToCreate = ArrayList.class;
    } else if (type == Map.class) {
      classToCreate = HashMap.class;
    } else if (type == SortedSet.class) { // issue #510 Collections Support
      classToCreate = TreeSet.class;
    } else if (type == Set.class) {
      classToCreate = HashSet.class;
    } else {
      classToCreate = type;
    }
    return classToCreate;
  }

  @Override
  public <T> boolean isCollection(Class<T> type) {
    // 如果是A.isAssignableFrom(B) 确定一个类(B)是不是继承来自于另一个父类(A)，
    // 类B是不是类A的子类或者两者相同
    return Collection.class.isAssignableFrom(type);
  }

}
