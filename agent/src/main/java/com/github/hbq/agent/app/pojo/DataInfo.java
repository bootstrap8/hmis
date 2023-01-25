package com.github.hbq.agent.app.pojo;

import com.github.hbq.common.utils.FormatNumber;
import lombok.Data;

/**
 * @author hbq
 */
@Data
public class DataInfo {

  private double value;
  private String desc;
  private double fmtValue;

  public DataInfo() {
  }

  public DataInfo(double value) {
    this.value = value;
  }

  public DataInfo(double value, String desc) {
    this.value = value;
    this.desc = desc;
  }

  public double getFmtValue() {
    return getFmtValueWithFractionDigits(2);
  }

  public double getFmtValueWithFractionDigits(int maximumFractionDigits) {
    return FormatNumber.format(value, maximumFractionDigits);
  }
}
