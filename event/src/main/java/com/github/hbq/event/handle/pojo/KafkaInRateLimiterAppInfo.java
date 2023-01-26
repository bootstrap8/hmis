package com.github.hbq.event.handle.pojo;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

/**
 * @author hbq
 */
@Data
public class KafkaInRateLimiterAppInfo {

  private String appName;
  private List<KafkaInRateLimiterInstInfo> instances;

  private int permits;

  public KafkaInRateLimiterAppInfo() {
  }

  public KafkaInRateLimiterAppInfo(String appName) {
    this.appName = appName;
  }

  public void addInstance(KafkaInRateLimiterInstInfo instance) {
    if (CollectionUtils.isEmpty(instances)) {
      instances = new ArrayList<>(5);
    }
    instances.add(instance);
  }

  public void addInstances(List<KafkaInRateLimiterInstInfo> insts) {
    if (CollectionUtils.isEmpty(instances)) {
      instances = new ArrayList<>(5);
    }
    instances.addAll(insts);
  }

  public static KafkaInRateLimiterAppInfo parse(String msg) {
    return JSON.parseObject(msg, KafkaInRateLimiterAppInfo.class);
  }

  /**
   * 是否为实例级别的速率变更,否则所有实例进行刷新
   *
   * @return
   */
  public boolean singleInstanceChange() {
    return CollectionUtils.isNotEmpty(instances);
  }

  /**
   * 返回和当前运行的实例一致的实例信息
   *
   * @param dataCenter
   * @param appName
   * @param ip
   * @param port
   * @return
   */
  public KafkaInRateLimiterInstInfo matchCurInstance(String dataCenter, String appName, String ip, int port) {
    if (CollectionUtils.isEmpty(instances)) {
      return null;
    }
    for (KafkaInRateLimiterInstInfo instance : instances) {
      String curKey = Joiner.on(",").join(appName, dataCenter, ip, port);
      String instKey = Joiner.on(",").join(this.appName, instance.getKey());
      if (curKey.equals(instKey)) {
        return instance;
      }
    }
    return null;
  }
}
