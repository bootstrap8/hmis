package com.github.hbq.event.handle.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author hbq
 */
public class DictEvent extends ApplicationEvent {

  public DictEvent(String msg) {
    super(msg);
  }
}
