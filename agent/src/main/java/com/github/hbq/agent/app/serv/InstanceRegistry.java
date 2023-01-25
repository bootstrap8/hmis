package com.github.hbq.agent.app.serv;

import com.github.hbq.agent.app.pojo.InstInfo;

/**
 * @author hbq
 */
public interface InstanceRegistry {

  /**
   * 注册实例信息
   */
  void registry();

  /**
   * 返回实例信息
   *
   * @return
   */
  InstInfo getInstance();
}
