package com.github.hbq.event.autoconfig;

import com.github.hbq.event.kafka.EventBatchListener;
import com.github.hbq.event.kafka.EventSingleListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author hbq
 */
@Configuration
@Slf4j
public class EventAutoConfiguration {

  public static final String KAFKA_LISTENER_TYPE_BATCH = "batch";

  @Value("${spring.kafka.listener.type:single}")
  private String listenerType;

  @Bean("hbq-event-ApplicationEventPublisherAware")
  @ConditionalOnExpression("${hbq.common.event.enable:false}")
  public ApplicationEventPublisherAware listener() {
    ApplicationEventPublisherAware aware;
    if (KAFKA_LISTENER_TYPE_BATCH.equals(listenerType)) {
      aware = new EventBatchListener();
      log.info("初始化批处理kafka事件监听器");
    } else {
      aware = new EventSingleListener();
      log.info("初始化单处理kafka事件监听器");
    }
    return aware;
  }
}
