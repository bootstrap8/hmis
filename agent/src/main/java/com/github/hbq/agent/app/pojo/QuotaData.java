package com.github.hbq.agent.app.pojo;

import com.github.hbq.common.utils.FormatTime;

/**
 * @author hbq
 */
public class QuotaData {

  private QuotaInfo quota;
  private long collectTime;
  private DataInfo data;

  public QuotaData(QuotaInfo quota, long collectTime) {
    this.quota = quota;
    this.collectTime = collectTime;
  }

  public void collectData(double value, long collectTime) {
    this.data = new DataInfo(value, null);
    this.collectTime = collectTime;
  }

  public void collectData(DataInfo data, long collectTime) {
    this.data = data;
    this.collectTime = collectTime;
  }

  public void collectData(double value, String desc, long collectTime) {
    this.data = new DataInfo(value, desc);
    this.collectTime = collectTime;
  }

  public QuotaInfo getQuota() {
    return quota;
  }

  public long getCollectTime() {
    return collectTime;
  }

  public String getFmtCollectTime() {
    return FormatTime.YYYYMMDDHHMISS.withSecs(getCollectTime());
  }

  public DataInfo getData() {
    return data;
  }
}
