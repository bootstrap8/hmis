package com.github.hbq.manage.config.serv.impl;

import com.github.hbq.manage.config.serv.DiscoveryAdapter;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hbq
 */
//@ConditionalOnBean({EurekaDiscoveryClient.class})
//@Component("agent-service-EurekaDiscoveryImpl")
@Slf4j
public class EurekaDiscoveryImpl implements DiscoveryAdapter {

  @Override
  public List<Map> queryAppInfos(Map map) {
    return null;
  }

  @Override
  public void refreshConfig(Map map) {

  }
}
