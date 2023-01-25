package com.github.hbq.agent.app.serv;

/**
 * @author hbq
 */
public interface QuotaSchedule {

  /**
   * 提交指标采集定时任务
   *
   * @param g
   * @return
   */
  boolean submit(QuotaDataGet g);
}
