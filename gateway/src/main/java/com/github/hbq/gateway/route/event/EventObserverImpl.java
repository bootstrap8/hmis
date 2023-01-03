package com.github.hbq.gateway.route.event;

import com.github.hbq.event.handle.EventObserver;
import com.github.hbq.event.handle.event.RouteEvent;
import com.github.hbq.gateway.route.RdbRouteDefinitionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

/**
 * @author hbq
 */
@Component("hbq-gateway-route-event-EventObserverImpl")
@Slf4j
public class EventObserverImpl implements EventObserver, ApplicationEventPublisherAware {

  @Autowired
  private RdbRouteDefinitionRepository repository;

  private ApplicationEventPublisher pub;

  @Override
  public void routeNotify(RouteEvent event) {
    repository.reloadImmediately();
    pub.publishEvent(new RefreshRoutesEvent("路由变化"));
  }

  @Override
  public void setApplicationEventPublisher(ApplicationEventPublisher pub) {
    this.pub = pub;
  }
}
