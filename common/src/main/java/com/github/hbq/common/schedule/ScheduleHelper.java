package com.github.hbq.common.schedule;


import com.github.hbq.common.utils.RoundInfo;

/**
 * @author hbq
 */
public interface ScheduleHelper {

  /**
   * 统计调度接口
   *
   * @param roundInfo
   */
  void execute(RoundInfo roundInfo);

  /**
   * 清理数据接口
   *
   * @param roundInfo
   */
  void clean(RoundInfo roundInfo);

  /**
   * 重建索引接口
   *
   * @param roundInfo
   */
  void reform(RoundInfo roundInfo);

  /**
   * 表初始化接口
   *
   * @param roundInfo
   */
  void init(RoundInfo roundInfo);
}
