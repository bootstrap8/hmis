package com.github.hbq.config.serv.impl;

import com.github.hbq.config.serv.DiscoveryAdapter;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hbq
 */
//@ConditionalOnBean({NacosDiscoveryClient.class})
//@Component("agent-service-NacosDiscoveryImpl")
@Slf4j
public class NacosDiscoveryImpl implements DiscoveryAdapter {

  @Override
  public List<Map> queryAppInfos(Map map) {
    return null;
  }

  @Override
  public void refreshConfig(Map map) {

  }
}
