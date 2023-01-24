package com.github.hbq.agent.app.pojo;

import com.github.hbq.agent.app.service.Collect;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Data;

/**
 * @author hbq
 */
@Data
public class CpuInfo implements Collect {

  /**
   * 进程CPU使用率
   */
  private double procUsage;
  /**
   * 系统CPU使用率
   */
  private double sysUsage;

  @Override
  public Collection<QuotaData> collectData(InstInfo instInfo, long collectTime) {
    List<QuotaData> list = new ArrayList<>(2);

    QuotaInfo qi = new QuotaInfo(instInfo, "app,jvm,proc_cpuusage", "应用指标,jvm,进程CPU使用率", "%");
    QuotaData qd = new QuotaData(qi);
    qd.collectData(new DataInfo(MemInfo.format(procUsage), ""), collectTime);
    list.add(qd);

    return list;
  }
}
