package com.github.hbq.manage.route.pojo;

import com.alibaba.fastjson.JSON;
import java.util.List;
import lombok.Data;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;

/**
 * @author hbq
 */
@Data
public class RouteInfo {

  private String id;
  private String uri;
  private List<PredicateDefinition> predicates;
  private List<FilterDefinition> filters;
  private int order;

  public RouteConfig config() {
    RouteConfig config = new RouteConfig();
    config.setId(id);
    config.setUri(uri);
    config.setPredicates(JSON.toJSONString(predicates));
    config.setFilters(JSON.toJSONString(filters));
    config.setOrder(order);
    return config;
  }

}
