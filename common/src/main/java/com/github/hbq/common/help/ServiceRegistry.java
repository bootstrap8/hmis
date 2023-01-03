package com.github.hbq.common.help;

/**
 * @author hbq
 */
public interface ServiceRegistry<KEY, SERVICE> {

  /**
   * 注册服务实例
   *
   * @param key
   * @param service
   */
  void registry(KEY key, SERVICE service);

  /**
   * 获取服务实例
   *
   * @param key
   * @return
   */
  SERVICE query(KEY key);

  /**
   * 获取服务实例
   *
   * @return
   */
  SERVICE query();
}
