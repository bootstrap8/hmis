package com.github.hbq.agent.app.service;

import com.github.hbq.agent.app.pojo.AppInfo;

/**
 * @author hbq
 */
public interface AppInfoRegistry {

  void registry();

  AppInfo getApp();
}
