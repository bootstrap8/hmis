package com.github.hbq.common.spring.context;

/**
 * @author hbq
 */
public class BeanInfo<T> {

  private String id;
  private T bean;

  public BeanInfo(String id, T bean) {
    this.id = id;
    this.bean = bean;
  }

  public String getId() {
    return id;
  }

  public T getBean() {
    return bean;
  }
}
