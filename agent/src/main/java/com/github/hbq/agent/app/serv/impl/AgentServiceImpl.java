package com.github.hbq.agent.app.serv.impl;

import com.alibaba.fastjson.JSON;
import com.github.hbq.agent.app.dao.impl.AgentDaoOptional;
import com.github.hbq.agent.app.serv.AgentService;
import com.github.hbq.event.handle.pojo.KafkaInRateLimiterAppInfo;
import com.github.hbq.event.handle.pojo.KafkaInRateLimiterInstInfo;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * @author hbq
 */
@Slf4j
public class AgentServiceImpl implements AgentService, InitializingBean {

  @Autowired
  private AgentDaoOptional optional;

  @Autowired(required = false)
  private KafkaTemplate kafka;

  @Override
  public void afterPropertiesSet() throws Exception {
    createKafkaInRateLimiter();
  }

  @Override
  public void saveKafkaInAppRateLimiter(Map map) {
    String appName = MapUtils.getString(map, "app_name");
    int permits = MapUtils.getIntValue(map, "permits");
    optional.getAgentDao().ifPresent(dao -> {
      dao.deleteKafkaInAppRateLimiter(appName);
      dao.saveKafkaInAppRateLimiter(map);
      dao.updateKafkaInInstanceRateLimiter(appName, permits);
    });
    if (Objects.nonNull(kafka)) {
      KafkaInRateLimiterAppInfo appInfo = new KafkaInRateLimiterAppInfo(appName);
      appInfo.setPermits(permits);
      String msg = JSON.toJSONString(appInfo);
      log.info("发送kafka入口消息应用速率变更通知消息: {}", msg);
      kafka.send("HBQ-AGENT-KAFKA-IN-RATE-LIMITER-CHANGE", msg);
    }
  }

  @Override
  public void saveKafkaInInstanceRateLimiter(Map map) {
    String dc = MapUtils.getString(map, "data_center");
    String appName = MapUtils.getString(map, "app_name");
    String ip = MapUtils.getString(map, "ip");
    int port = MapUtils.getIntValue(map, "port", 8080);
    optional.getAgentDao().ifPresent(dao -> {
      dao.deleteKafkaInInstanceRateLimiter(dc, appName, ip, port);
      dao.saveKafkaInInstanceRateLimiter(map);
    });
    if (Objects.nonNull(kafka)) {
      KafkaInRateLimiterAppInfo appInfo = new KafkaInRateLimiterAppInfo(appName);
      KafkaInRateLimiterInstInfo instInfo = new KafkaInRateLimiterInstInfo(dc, ip, port);
      instInfo.setPermits(MapUtils.getIntValue(map, "permits"));
      appInfo.addInstance(instInfo);
      String msg = JSON.toJSONString(appInfo);
      log.info("发送kafka入口消息应用实例速率变更通知消息: {}", msg);
      kafka.send("HBQ-AGENT-KAFKA-IN-RATE-LIMITER-CHANGE", msg);
    }
  }

  private void createKafkaInRateLimiter() {
    optional.getAgentDao().ifPresent(dao -> {
      try {
        dao.createKafkaInAppRateLimiter();
        log.info("创建应用kafka入口消息速率配置表");
      } catch (Exception e) {
        log.info("应用kafka入口消息速率配置表已存在");
      }
      try {
        dao.createKafkaInInstanceRateLimiter();
        log.info("创建应用实例kafka入口消息速率配置表");
      } catch (Exception e) {
        log.info("应用实例kafka入口消息速率配置表已存在");
      }
    });
  }
}
