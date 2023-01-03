package com.github.hbq.common.dict;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author hbq
 */
public interface Dict<T> {

  /**
   * 重载字典缓存
   */
  void reloadImmediately();

  /**
   * 查询字典基础信息
   *
   * @param fieldName
   * @return
   */
  Optional<DictInfo> queryDict(String fieldName);

  /**
   * 判断是否为字典
   *
   * @param fieldName
   * @return
   */
  boolean isDict(String fieldName);

  /**
   * 返回所有字典信息
   *
   * @return
   */
  Set<DictInfo> allDict();

  /**
   * 使用字典中Key进行转义
   *
   * @param data
   * @param fieldName
   * @param nullOver
   */
  void tranForKey(T data, String fieldName, boolean nullOver);

  /**
   * 使用字典中Val进行转义
   *
   * @param data
   * @param fieldName
   * @param nullOver
   */
  void tranForVal(T data, String fieldName, boolean nullOver);

  /**
   * 使用字典中Key来进行转义
   *
   * @param data
   * @param nullOver
   */
  void tranAnyForKey(T data, boolean nullOver);

  /**
   * 使用字典中Val来进行转义
   *
   * @param data
   * @param nullOver
   */
  void tranAnyForVal(T data, boolean nullOver);

  /**
   * 使用指定策略进行转义
   *
   * @param data
   * @param policies
   * @param nullOver
   */
  void tranAnyForPolicy(T data, List<TranPolicy> policies, boolean nullOver);
}
