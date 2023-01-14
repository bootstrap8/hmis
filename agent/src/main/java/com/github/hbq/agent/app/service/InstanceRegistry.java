package com.github.hbq.agent.app.service;

import com.github.hbq.agent.app.pojo.InstInfo;

/**
 * @author hbq
 */
public interface InstanceRegistry {

  void registry();

  InstInfo getInstance();
}
