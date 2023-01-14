package com.github.hbq.agent.app.pojo;

import lombok.Data;

/**
 * @author hbq
 */
@Data
public class GcInfo {

  private long youngCount;
  private long youngTime;
  private long fullCount;
  private long fullTime;
}
