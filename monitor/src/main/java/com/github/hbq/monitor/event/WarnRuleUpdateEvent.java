package com.github.hbq.monitor.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author hbq
 */
public class WarnRuleUpdateEvent extends ApplicationEvent {

  public WarnRuleUpdateEvent(Object source) {
    super(source);
  }
}
