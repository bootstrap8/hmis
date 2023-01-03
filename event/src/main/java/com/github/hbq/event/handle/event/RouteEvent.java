package com.github.hbq.event.handle.event;

import org.springframework.context.ApplicationEvent;

/**
 * @author hbq
 */
public class RouteEvent extends ApplicationEvent {

  public RouteEvent(String msg) {
    super(msg);
  }
}
