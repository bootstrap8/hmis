package com.github.hbq.manage.route.pojo;

import com.alibaba.fastjson.JSON;
import java.util.Objects;
import lombok.Data;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;

/**
 * @author hbq
 */
@Data
public class RouteConfig {

  private String id;
  private String uri;
  private String predicates;
  private String filters;
  private int order;

  public RouteInfo info() {
    RouteInfo info = new RouteInfo();
    info.setId(id);
    info.setUri(uri);
    if (Objects.nonNull(predicates)) {
      info.setPredicates(JSON.parseArray(predicates, PredicateDefinition.class));
    }
    if (Objects.nonNull(filters)) {
      info.setFilters(JSON.parseArray(filters, FilterDefinition.class));
    }
    info.setOrder(order);
    return info;
  }
}
