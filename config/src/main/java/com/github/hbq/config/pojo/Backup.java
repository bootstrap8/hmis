package com.github.hbq.config.pojo;

import com.github.hbq.common.utils.FormatTime;
import lombok.Data;

/**
 * @author hbq
 */
@Data
public class Backup {

  private String id;
  private long backTime;
  private int size;

  public String getFmtBackupTime() {
    return FormatTime.YYYYMMDDHHMISS.withSecs(backTime);
  }
}
