package com.github.hbq.agent.app.pojo;

import lombok.Data;

/**
 * @author hbq
 */
@Data
public class CpuInfo {

  /**
   * 进程CPU使用率
   */
  private double procUsage;
  /**
   * 系统CPU使用率
   */
  private double sysUsage;

}
