package com.github.hbq.event.handle;

import com.github.hbq.event.handle.event.DictEvent;
import com.github.hbq.event.handle.event.KafkaInRateLimiterEvent;
import com.github.hbq.event.handle.event.RouteEvent;

/**
 * @author hbq
 */
public interface EventObserver {

  /**
   * 路由变化事件
   *
   * @param event
   */
  default void routeNotify(RouteEvent event) {
  }

  /**
   * 字典变化事件
   *
   * @param event
   */
  default void dictNotify(DictEvent event) {

  }

  /**
   * kafka入口消息限速事件
   *
   * @param event
   */
  default void kafkaRateLimiterNotify(KafkaInRateLimiterEvent event) {

  }
}
