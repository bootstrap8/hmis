package com.github.hbq.config.pojo;

import lombok.Data;

/**
 * @author hbq
 */
@Data
public class BackupDetail {

  private String id;
  private String path;
  private String name;
  private String value;
  private long backTime;

  public String getImportData() {
    return String.join("=", path, name, value);
  }
}
