package com.github.hbq.common.schedule;


import com.github.hbq.common.schedule.annotation.Schedule;
import com.github.hbq.common.utils.RoundInfo;

/**
 * @author hbq
 */
public interface RoundTimeHelper {

  /**
   * 创建统计程序的时间条件信息
   *
   * @param report
   * @return
   */
  RoundInfo createRoundInfo(Schedule report);
}
