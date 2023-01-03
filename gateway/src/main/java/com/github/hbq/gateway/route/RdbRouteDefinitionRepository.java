package com.github.hbq.gateway.route;

import com.github.hbq.gateway.route.dao.RouteDao;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author hbq
 */
@Component("hbq-common-gateway-route-RdbRouteDefinitionRepository")
@Slf4j
public class RdbRouteDefinitionRepository implements RouteDefinitionRepository, InitializingBean {

  private Map<String, RouteDefinition> routeDefinitionEntry = new ConcurrentHashMap<>();

  private List<RouteDefinition> routeDefinitionList = new Vector<>();

  @Autowired
  private RouteDao dao;

  private CountDownLatch cdl = new CountDownLatch(1);

  @Override
  public void afterPropertiesSet() throws Exception {
    createRouteConfig();
    reloadImmediately();
  }

  @Override
  public Flux<RouteDefinition> getRouteDefinitions() {
    try {
      cdl.await();
    } catch (InterruptedException e) {
    }
    return Flux.fromIterable(routeDefinitionList);
  }

  @Override
  public Mono<Void> save(Mono<RouteDefinition> route) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Mono<Void> delete(Mono<String> routeId) {
    throw new UnsupportedOperationException();
  }

  public synchronized void reloadImmediately() {
    log.info("重载路由配置");
    try {
      routeDefinitionEntry.clear();
      routeDefinitionList.clear();
      List<RouteInfo> all = dao.queryAllRouteConfig();
      all.forEach(r -> {
        RouteDefinition f = r.entry();
        routeDefinitionEntry.put(r.getId(), f);
        routeDefinitionList.add(f);
      });
      log.info("加载到路由配置: {} 条", routeDefinitionList.size());
    } finally {
      cdl.countDown();
    }
  }

  private void createRouteConfig() {
    try {
      dao.createRouteConfig();
    } catch (Exception e) {
    }
  }
}
