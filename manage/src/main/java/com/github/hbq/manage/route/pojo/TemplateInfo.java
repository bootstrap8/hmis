package com.github.hbq.manage.route.pojo;

import lombok.Data;

/**
 * @author hbq
 */
@Data
public class TemplateInfo {

  private int tid;
  private String name;
  private String uri;
  private String predicates;
  private String filters;
  private int order;
}
