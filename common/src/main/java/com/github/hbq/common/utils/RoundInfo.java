package com.github.hbq.common.utils;

/**
 * @author hbq
 */
public class RoundInfo {

  private TimeInfo startTime;
  private TimeInfo endTime;

  public TimeInfo getStartTime() {
    return startTime;
  }

  public RoundInfo setStartTime(TimeInfo startTime) {
    this.startTime = startTime;
    return this;
  }

  public TimeInfo getEndTime() {
    return endTime;
  }

  public RoundInfo setEndTime(TimeInfo endTime) {
    this.endTime = endTime;
    return this;
  }

  @Override
  public String toString() {
    return String.format("统计调度, 开始时间(%s, %s), 结束时间(%s, %s)", startTime.getFormatTimeString(), startTime.getMills(), endTime.getFormatTimeString(),
        endTime.getMills());
  }

  public String simpleInfo() {
    return String.format("[%s, %s%s", startTime.getFormatTimeString(), endTime.getFormatTimeString(), endTime.isClosure() ? "]" : ")");
  }
}
