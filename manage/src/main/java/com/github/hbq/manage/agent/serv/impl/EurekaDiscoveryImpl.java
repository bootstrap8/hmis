package com.github.hbq.manage.agent.serv.impl;

import com.github.hbq.manage.agent.serv.DiscoveryAdapter;
import java.util.List;
import java.util.Map;

/**
 * @author hbq
 */
//@ConditionalOnBean({EurekaDiscoveryClient.class})
public class EurekaDiscoveryImpl implements DiscoveryAdapter {

  @Override
  public List<Map> queryAppInfos(Map map) {
    return null;
  }

  @Override
  public void refreshConfig(Map map) {

  }
}