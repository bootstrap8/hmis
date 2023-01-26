package com.github.hbq.event.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
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
public class EventBatchListener implements ApplicationEventPublisherAware, ApplicationContextAware, InitializingBean {

  private EventSingleListener single;

  private ApplicationEventPublisher pub;

  @Autowired
  private ApplicationContext context;

  @Override
  public void afterPropertiesSet() throws Exception {
    single = new EventSingleListener();
    single.setApplicationEventPublisher(pub);
    single.setApplicationContext(context);
    single.afterPropertiesSet();
  }

  @KafkaListener(topics = {"HBQ-GATEWAY-ROUTE-CHANGE"},
      groupId = "#{'${spring.application.name}-' + T(com.github.hbq.event.kafka.IDBox).GID}")
  void routeNotify(ConsumerRecords<String, String> rds) {
    rds.forEach(rd -> single.routeNotify(rd));
  }

  @KafkaListener(topics = {"HBQ-COMMON-DICT-CHANGE"},
      groupId = "#{'${spring.application.name}-' + T(com.github.hbq.event.kafka.IDBox).GID}")
  void dictNotify(ConsumerRecords<String, String> rds) {
    rds.forEach(rd -> single.dictNotify(rd));
  }

  @KafkaListener(topics = {"HBQ-AGENT-KAFKA-IN-RATE-LIMITER-CHANGE"},
      groupId = "#{'${spring.application.name}-' + T(com.github.hbq.event.kafka.IDBox).GID}")
  void kafkaRateLimiterNotify(ConsumerRecords<String, String> rds) {
    rds.forEach(rd -> single.kafkaRateLimiterNotify(rd));
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
