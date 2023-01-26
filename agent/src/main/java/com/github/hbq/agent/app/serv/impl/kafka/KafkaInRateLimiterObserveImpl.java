package com.github.hbq.agent.app.serv.impl.kafka;

import com.github.hbq.agent.app.pojo.InstInfo;
import com.github.hbq.agent.app.serv.InstanceRegistry;
import com.github.hbq.common.spring.context.SpringContext;
import com.github.hbq.event.handle.EventObserver;
import com.github.hbq.event.handle.event.KafkaInRateLimiterEvent;
import com.github.hbq.event.handle.pojo.KafkaInRateLimiterAppInfo;
import com.github.hbq.event.handle.pojo.KafkaInRateLimiterInstInfo;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author hbq
 */
@Slf4j
public class KafkaInRateLimiterObserveImpl implements EventObserver {

  @Autowired
  private KafkaInRateLimit kafkaInRateLimit;

  @Autowired
  private SpringContext context;

  @Autowired
  private InstanceRegistry instanceRegistry;

  @Override
  public void kafkaRateLimiterNotify(KafkaInRateLimiterEvent event) {
    log.info("实例观察者接收到事件对象: {}", event);
    KafkaInRateLimiterAppInfo appInfo = event.getAppInfo();
    if (appInfo.singleInstanceChange()) {

      InstInfo curInst = instanceRegistry.getInstance();

      KafkaInRateLimiterInstInfo instance = appInfo.matchCurInstance(curInst.getDataCenter(),
          curInst.getApp().getName(), curInst.getIp(), curInst.getPort());

      if (Objects.nonNull(instance)) {
        log.info("修改当前实例kafka入口消息速率");
        kafkaInRateLimit.changeRateLimiter(instance.getPermits());
      }

    } else {
      log.info("修改应用所有实例kafka入口消息速率");
      kafkaInRateLimit.changeRateLimiter(appInfo.getPermits());
    }
  }
}
