package com.github.hbq.manage.config.serv;

import java.util.List;
import java.util.Map;

/**
 * @author hbq
 */
public interface DiscoveryAdapter {

  List<Map> queryAppInfos(Map map);

  void refreshConfig(Map map);
}
