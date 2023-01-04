package com.github.hbq.common.utils;


import java.util.concurrent.TimeUnit;

/**
 * @author hbq
 */
public class TimeInfo {

  private long mills;

  private long secs;

  private String formatTimeString;

  private boolean closure = false;

  public TimeInfo() {
  }

  public TimeInfo(long time, TimeUnit unit) {
    setMills(TimeUnit.MILLISECONDS.convert(time, unit));
  }

  public TimeInfo format() {
    this.formatTimeString = FormatTime.YYYYMMDDHHMISS.withMills(mills);
    return this;
  }

  public long getMills() {
    return mills;
  }

  public TimeInfo setMills(long mills) {
    this.mills = mills;
    this.secs = mills / 1000L;
    return this;
  }

  public long getSecs() {
    return secs;
  }

  public TimeInfo setSecs(long secs) {
    return setMills(secs * 1000L);
  }

  public String getFormatTimeString() {
    return formatTimeString;
  }

  public boolean isClosure() {
    return closure;
  }

  public TimeInfo closure() {
    this.closure = true;
    return this;
  }

}
