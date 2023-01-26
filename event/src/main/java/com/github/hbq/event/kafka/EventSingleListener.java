package com.github.hbq.event.kafka;

import com.github.hbq.event.handle.EventObserver;
import com.github.hbq.event.handle.event.DictEvent;
import com.github.hbq.event.handle.event.KafkaInRateLimiterEvent;
import com.github.hbq.event.handle.event.RouteEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.kafka.annotation.KafkaListener;

/**
 * @author hbq
 */
@Slf4j
public class EventSingleListener implements ApplicationEventPublisherAware, ApplicationContextAware, InitializingBean {

  private ApplicationEventPublisher pub;

  @Autowired
  private ApplicationContext context;

  private List<EventObserver> observers = new ArrayList<>();

  @Override
  public void afterPropertiesSet() throws Exception {
    Map<String, EventObserver> map = context.getBeansOfType(EventObserver.class);
    log.info("注册的观察者: {}", map);
    if (Objects.nonNull(map)) {
      Collection<EventObserver> c = map.values();
      if (c != null) {
        observers.addAll(c);
      }
    }
  }

  @KafkaListener(topics = {"HBQ-GATEWAY-ROUTE-CHANGE"},
      groupId = "#{'${spring.application.name}-' + T(com.github.hbq.event.kafka.IDBox).GID}")
  void routeNotify(ConsumerRecord<String, String> rd) {
    try {
      String msg = rd.value();
      log.info("接收到门户网关路由变更kafka消息: {}", msg);
      RouteEvent event = new RouteEvent(msg);
      observers.forEach(observer -> observer.routeNotify(event));
      pub.publishEvent(event);
    } catch (Exception e) {
      log.error("", e);
    }
  }

  @KafkaListener(topics = {"HBQ-COMMON-DICT-CHANGE"},
      groupId = "#{'${spring.application.name}-' + T(com.github.hbq.event.kafka.IDBox).GID}")
  void dictNotify(ConsumerRecord<String, String> rd) {
    try {
      String msg = rd.value();
      log.info("接收到字典变更kafka消息: {}", msg);
      DictEvent event = new DictEvent(msg);
      observers.forEach(observer -> observer.dictNotify(event));
      pub.publishEvent(event);
    } catch (Exception e) {
      log.error("", e);
    }
  }

  @KafkaListener(topics = {"HBQ-AGENT-KAFKA-IN-RATE-LIMITER-CHANGE"},
      groupId = "#{'${spring.application.name}-' + T(com.github.hbq.event.kafka.IDBox).GID}")
  void kafkaRateLimiterNotify(ConsumerRecord<String, String> rd) {
    try {
      String msg = rd.value();
      log.info("接收到kafka入口消息速率变更消息: {}", msg);
      KafkaInRateLimiterEvent event = new KafkaInRateLimiterEvent(msg);
      observers.forEach(observer -> observer.kafkaRateLimiterNotify(event));
      pub.publishEvent(event);
    } catch (Exception e) {
      log.error("", e);
    }
  }

  @Override
  public void setApplicationEventPublisher(ApplicationEventPublisher pub) {
    this.pub = pub;
  }

  @Override
  public void setApplicationContext(ApplicationContext context) throws BeansException {
    this.context = context;
  }
}
