package com.github.hbq.agent.app.pojo;

import java.text.NumberFormat;
import lombok.Data;

/**
 * @author hbq
 */
@Data
public class MemInfo {

  private static NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();
  public static long MEGA_BYTE = 1024 * 1024;

  static {
    NUMBER_FORMAT.setGroupingUsed(false);
    NUMBER_FORMAT.setMaximumFractionDigits(2);
  }

  private double usedHeapMemory;
  private double maxHeapMemory;
  private double rateHeapMemory;
  private double usedNonHeapMemory;
  private double maxNonHeapMemory;
  private double rateNonHeapMemory;
  private String memoryUnit = "m";

  public void calc() {
    if (maxHeapMemory > 0) {
      rateHeapMemory = format((usedHeapMemory / maxHeapMemory) * 100);
    }
    if (maxNonHeapMemory > 0) {
      rateNonHeapMemory = format((usedNonHeapMemory / maxNonHeapMemory) * 100);
    }
  }

  public static Double format(Double number) {
    return Double.valueOf(NUMBER_FORMAT.format(number));
  }

}
