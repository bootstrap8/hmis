package com.github.hbq.common.help;

import java.util.Optional;

/**
 * @author hbq
 */
public interface OptionalRegistry<KEY, T> {

  /**
   * 注册服务实例
   *
   * @param key
   * @param t
   */
  void registry(KEY key, T t);

  /**
   * 获取服务实例
   *
   * @param key
   * @return
   */
  Optional<T> query(KEY key);

  /**
   * 获取服务实例
   *
   * @return
   */
  default Optional<T> query() {
    return query((KEY) "default");
  }
}
