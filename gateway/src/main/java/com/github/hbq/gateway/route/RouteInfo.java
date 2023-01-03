package com.github.hbq.gateway.route;

import com.alibaba.fastjson.JSON;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;
import lombok.Data;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;

/**
 * @author hbq
 */
@Data
public class RouteInfo {

  private String id;
  private String uri;
  private String predicates;
  private String filters;
  private int order;

  public RouteDefinition entry() {
    RouteDefinition fin = new RouteDefinition();
    fin.setId(id);
    try {
      fin.setUri(new URI(uri));
    } catch (URISyntaxException e) {
    }
    fin.setPredicates(JSON.parseArray(predicates, PredicateDefinition.class));
    fin.setFilters(JSON.parseArray(filters, FilterDefinition.class));
    fin.setOrder(order);
    return fin;
  }

  public void to(RouteDefinition fin) {
    this.id = fin.getId();
    this.uri = fin.getUri().toString();
    if (Objects.nonNull(fin.getPredicates())) {
      this.predicates = JSON.toJSONString(fin.getPredicates());
    }
    if (Objects.nonNull(fin.getFilters())) {
      this.filters = JSON.toJSONString(fin.getFilters());
    }
    this.order = fin.getOrder();
  }
}
