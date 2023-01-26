package com.github.hbq.event.handle.event;

import com.github.hbq.event.handle.pojo.KafkaInRateLimiterAppInfo;
import org.springframework.context.ApplicationEvent;

/**
 * @author hbq
 */
public class KafkaInRateLimiterEvent extends ApplicationEvent {

  private KafkaInRateLimiterAppInfo appInfo;

  public KafkaInRateLimiterEvent(String source) {
    super(source);
    this.appInfo = KafkaInRateLimiterAppInfo.parse(source);
  }

  public KafkaInRateLimiterAppInfo getAppInfo() {
    return appInfo;
  }
}
