package com.github.hbq.common.schedule.impl;

import com.github.hbq.common.schedule.ScheduleHelper;
import com.github.hbq.common.schedule.Time;
import com.github.hbq.common.schedule.annotation.Schedule;
import com.github.hbq.common.utils.RoundInfo;

/**
 * @author hbq
 */
public class XXXScheduler implements ScheduleHelper {

  @Schedule(name = "任务描述", cron = "* * * * *", time = Time.DAYS, offset = -1, align = true)
  @Override
  public void execute(RoundInfo roundInfo) {
    // 执行任务
  }

  @Override
  public void clean(RoundInfo roundInfo) {
    // 清理
  }

  @Override
  public void reform(RoundInfo roundInfo) {
    // 执行任务前准备工作
  }

  @Override
  public void init(RoundInfo roundInfo) {
    // 初始化工作
  }
}
