package com.github.hbq.manage.config.serv.impl;

import com.alibaba.fastjson.JSON;
import com.github.hbq.manage.config.pojo.ZKNode;
import com.github.hbq.manage.config.serv.NodeService;
import com.github.hbq.manage.config.serv.ZookeeperService;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author hbq
 */
@Service("zkui-service-NodeServiceImpl")
@Slf4j
public class NodeServiceImpl implements NodeService {

  @Autowired
  private ZookeeperService zookeeperService;

  @Override
  public Map queryNodes(Map map) {
    String zkPath = MapUtils.getString(map, "zkPath");
    Map result = new HashMap();
    try {
      log.info("查询路径[{}]下的节点信息", zkPath);
      ZKNode zkNode = zookeeperService.listNodeEntries(zkPath, "ADMIN");
      result.put("tree", zkNode.createTree());
      result.put("leaf", zkNode.getLeafBeanLSt());
      log.info("查询到zk配置信息: {}", JSON.toJSONString(result));
    } catch (Exception e) {
      log.error("", e);
    }
    return result;
  }
}
