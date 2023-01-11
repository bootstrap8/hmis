package com.github.hbq.manage.route.serv.impl;

import com.alibaba.fastjson.JSON;
import com.github.hbq.common.utils.SqlUtils;
import com.github.hbq.manage.route.dao.RouteDao;
import com.github.hbq.manage.route.pojo.RouteConfig;
import com.github.hbq.manage.route.pojo.RouteInfo;
import com.github.hbq.manage.route.pojo.TemplateInfo;
import com.github.hbq.manage.route.serv.RouteService;
import com.google.common.collect.ImmutableMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * @author hbq
 */
@Service("hbq-common-gateway-route-serv-RouteServiceImpl")
@Slf4j
public class RouteServiceImpl implements RouteService, InitializingBean {

  @Autowired(required = false)
  private KafkaTemplate kafka;

  @Autowired
  private JdbcTemplate jt;

  @Autowired
  private RouteDao dao;

  @Override
  public void afterPropertiesSet() throws Exception {
    createRouteConfig();
    createRouteTemplate();
  }

  @Override
  public void saveRouteConfig(RouteInfo route) {
    try {
      dao.deleteRouteConfig(route.getId());
      dao.saveRouteConfig(route.config());
      String msg = JSON.toJSONString(route);
      log.info("保存路由成功: {}", msg);
      if (Objects.nonNull(kafka)) {
        kafka.send("HBQ-GATEWAY-ROUTE-CHANGE", msg);
      }
    } catch (Exception e) {
      throw new RuntimeException(e.getCause());
    }
  }

  @Override
  public void deleteRouteConfig(String id) {
    try {
      dao.deleteRouteConfig(id);
      if (Objects.nonNull(kafka)) {
        String msg = JSON.toJSONString(ImmutableMap.of("routeId", id));
        kafka.send("HBQ-GATEWAY-ROUTE-CHANGE", msg);
      }
    } catch (Exception e) {
      throw new RuntimeException(e.getCause());
    }
  }

  @Override
  public List<RouteInfo> queryAllRouteConfig(int pageNum, int pageSize, String routeSelect, String routeKey) {
    return dao.queryAllRouteConfig(new RowBounds(pageNum, pageSize), routeSelect, routeKey).stream()
        .map(r -> r.info()).collect(Collectors.toList());
  }

  @Override
  public RouteInfo queryRoute(String id) {
    RouteConfig config = dao.queryRoute(id);
    return Objects.nonNull(config) ?
        config.info() : null;
  }

  @Override
  public void refreshRouteConfig() {
    if (Objects.nonNull(kafka)) {
      kafka.send("HBQ-GATEWAY-ROUTE-CHANGE", "{}");
    }
  }

  @Override
  public List<TemplateInfo> queryRouteTemplateInfos() {
    return dao.queryRouteTemplateInfos();
  }

  private void createRouteConfig() {
    try {
      dao.createRouteConfig();
    } catch (Exception e) {
    }
  }

  private void createRouteTemplate() {
    try {
      dao.createRouteTemplate();
      SqlUtils.initDataSql(jt, "/", "tps.sql", null);
      log.info("初始化模板配置数据");
    } catch (Exception e) {
    }
  }
}
