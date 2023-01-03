package com.github.hbq.common.schedule.impl;

import com.github.hbq.common.schedule.ScheduleHelper;
import com.github.hbq.common.schedule.annotation.Schedule;
import com.github.hbq.common.utils.RoundInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hbq
 */
@Slf4j
public class LogScheduleHelperImpl implements ScheduleHelper {

  private ScheduleHelper target;

  private Schedule schedule;

  public LogScheduleHelperImpl(ScheduleHelper target, Schedule schedule) {
    this.target = target;
    this.schedule = schedule;
  }

  @Override
  public void execute(RoundInfo roundInfo) {
    if (log.isDebugEnabled()) {
      log.debug("[统计应用][{}] {}, 开始创建统计表。", schedule.name(), roundInfo.simpleInfo());
    }
    try {
      target.execute(roundInfo);
    } catch (Throwable e) {
      log.error("", e);
    }
    if (log.isDebugEnabled()) {
      log.debug("[统计应用][{}] {}, 创建统计表结束。", schedule.name(), roundInfo.simpleInfo());
    }
  }

  @Override
  public void clean(RoundInfo roundInfo) {
    if (log.isDebugEnabled()) {
      log.debug("[统计应用][{}] {}, 开始清理数据。", schedule.name(), roundInfo.simpleInfo());
    }
    try {
      target.clean(roundInfo);
    } catch (Throwable e) {
      log.error("", e);
    }
    if (log.isDebugEnabled()) {
      log.debug("[统计应用][{}] {}, 清理数据结束。", schedule.name(), roundInfo.simpleInfo());
    }
  }

  @Override
  public void reform(RoundInfo roundInfo) {
    if (log.isDebugEnabled()) {
      log.debug("[统计应用][{}] {}, 开始重建索引。", schedule.name(), roundInfo.simpleInfo());
    }
    try {
      target.reform(roundInfo);
    } catch (Throwable e) {
      log.error("", e);
    }
    if (log.isDebugEnabled()) {
      log.debug("[统计应用][{}] {}, 重建索引结束。", schedule.name(), roundInfo.simpleInfo());
    }
  }

  @Override
  public void init(RoundInfo roundInfo) {
    if (log.isDebugEnabled()) {
      log.debug("[统计应用][{}] {}, 开始创建统计表。", schedule.name(), roundInfo.simpleInfo());
    }
    try {
      target.init(roundInfo);
    } catch (Throwable e) {
      log.error("", e);
    }
    if (log.isDebugEnabled()) {
      log.debug("[统计应用][{}] {}, 创建统计表结束。", schedule.name(), roundInfo.simpleInfo());
    }
  }

}
