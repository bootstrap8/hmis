package com.github.hbq.monitor.pojo;

import com.github.hbq.common.utils.FormatTime;
import com.google.common.base.Splitter;
import java.util.List;
import lombok.Data;

/**
 * @author hbq
 */
@Data
public class QuotaWarnRuleInfo {

  private String appName;
  private String quotaName;
  private String rule;
  private int checkCount;
  private long notifyTimeoutSec;
  private String phoneNums;
  private int enable = -1;
  private long updateTime;

  public void update() {
    this.updateTime = FormatTime.nowSecs();
  }

  public String getKey() {
    return String.join(",", appName, quotaName);
  }

  public String[] arrayOfPhoneNums() {
    List<String> list = Splitter.on(",").splitToList(phoneNums);
    String[] array = new String[list.size()];
    list.toArray(array);
    return array;
  }

  public boolean acceptCount(int actual) {
    return actual >= checkCount;
  }

  public boolean acceptUnFirstNotifyTimeout(long lastNotifyTime) {
    return FormatTime.nowSecs() - lastNotifyTime >= notifyTimeoutSec;
  }

  public String getFmtUpdateTime() {
    return FormatTime.YYYYMMDDHHMISS.withSecs(updateTime);
  }

  public String getFmtEnable() {
    return enable == 1 ? "启用" : "停止";
  }
}
