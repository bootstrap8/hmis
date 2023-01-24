package com.github.hbq.agent.app.pojo;

import com.github.hbq.agent.app.service.Collect;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Data;

/**
 * @author hbq
 */
@Data
public class MemInfo implements Collect {

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

  @Override
  public Collection<QuotaData> collectData(InstInfo instInfo, long collectTime) {
    calc();
    List<QuotaData> list = new ArrayList<>(2);

    QuotaInfo qi = new QuotaInfo(instInfo, "app,jvm,rate_heapmemory", "应用指标,jvm,堆内存占用率", "%");
    QuotaData qd = new QuotaData(qi);
    qd.collectData(new DataInfo(rateHeapMemory, ""), collectTime);
    list.add(qd);

    qi = new QuotaInfo(instInfo, "app,jvm,rate_nonheapmemory", "应用指标,jvm,非堆内存占用率", "%");
    qd = new QuotaData(qi);
    qd.collectData(new DataInfo(rateNonHeapMemory, ""), collectTime);
    list.add(qd);

    return list;
  }

  public static Double format(Double number) {
    return Double.valueOf(NUMBER_FORMAT.format(number));
  }

}
