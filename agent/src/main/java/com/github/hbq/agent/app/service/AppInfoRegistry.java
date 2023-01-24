package com.github.hbq.agent.app.service;

import com.github.hbq.agent.app.pojo.AppInfo;

/**
 * @author hbq
 */
public interface AppInfoRegistry {

  /**
   * 应用注册
   */
  void registry();

  /**
   * 获取应用信息
   *
   * @return
   */
  AppInfo getApp();
}
