package com.github.hbq.agent.app.serv.impl.kafka;

import com.github.hbq.agent.app.dao.impl.AgentDaoOptional;
import com.github.hbq.agent.app.pojo.InstInfo;
import com.github.hbq.agent.app.serv.InstanceRegistry;
import com.github.hbq.common.spring.context.SpringContext;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.RateLimiter;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author hbq
 */
@Slf4j
public class KafkaInRateLimit implements InitializingBean {

  @Autowired
  private SpringContext context;

  @Autowired
  private AgentDaoOptional optional;

  @Autowired
  private InstanceRegistry instanceRegistry;

  private volatile RateLimiter rl;

  @Override
  public void afterPropertiesSet() throws Exception {
    int permits = context.getIntValue("hbq.agent.kafka.in.rate-limiter", 50000);
    InstInfo curInst = instanceRegistry.getInstance();
    optional.getAgentDao().ifPresent(dao -> {
      Map permitMap = null;
      permitMap = dao.queryKafkaInInstanceRateLimiter(ImmutableMap.of("data_center", curInst.getDataCenter(),
          "app_name", curInst.getApp().getName(), "ip", curInst.getIp(), "port", curInst.getPort()));
      log.info("读取到实例的kafka入口消息速率配置: {}", permitMap);
      if (Objects.isNull(permitMap)) {
        permitMap = dao.queryKafkaInAppRateLimiter(ImmutableMap.of("app_name", curInst.getApp().getName()));
        log.info("读取到应用的kafka入口消息速率配置: {}", permitMap);
      }
      if (Objects.nonNull(permitMap)) {
        changeRateLimiter(MapUtils.getIntValue(permitMap, "permits"));
      } else {
        changeRateLimiter(permits);
      }
    });

  }

  public void changeRateLimiter(int permits) {
    rl = RateLimiter.create(permits);
    log.info("修改kafka入口消息速率为: {} 条/秒", permits);
  }

  public void acquire() {
    acquire(1);
  }

  public void acquire(int permits) {
    rl.acquire(permits);
  }
}
