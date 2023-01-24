package com.github.hbq.agent.app.service.impl;

import static com.github.hbq.agent.app.pojo.MemInfo.MEGA_BYTE;
import static com.github.hbq.agent.app.pojo.MemInfo.format;

import com.github.hbq.agent.app.pojo.CpuInfo;
import com.github.hbq.agent.app.pojo.CycleInfo;
import com.github.hbq.agent.app.pojo.GcInfo;
import com.github.hbq.agent.app.pojo.InstInfo;
import com.github.hbq.agent.app.pojo.MemInfo;
import com.github.hbq.agent.app.pojo.QuotaData;
import com.github.hbq.agent.app.pojo.QuotaInfo;
import com.github.hbq.agent.app.pojo.QuotaInfo.Type;
import com.github.hbq.agent.app.service.AbstractQuotaDataGet;
import com.github.hbq.common.spring.context.SpringContext;
import com.github.hbq.common.utils.FormatNumber;
import com.github.hbq.common.utils.FormatTime;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author hbq
 */
@Slf4j
public class DefaultQuotaDataGet extends AbstractQuotaDataGet {

  @Autowired
  private SpringContext context;
  private long fullTime = 0;
  private long youngTime = 0;
  private OperatingSystemMXBean osMxBean;
  private ThreadMXBean threadBean;
  private long preTime = System.nanoTime();
  private long preUsedTime = 0;

  @Override
  protected Collection<QuotaData> collectData(InstInfo instance) {
    MemInfo mem = getMemInfo();
    CpuInfo cpu = getCpuInfo();
    GcInfo gc = getGcInfo();

    Collection<QuotaInfo> qis = registry(instance);
    List<QuotaData> qds = new ArrayList<>(qis.size());
    long collectTime = FormatTime.nowSecs();

    qds.addAll(mem.collectData(instance, collectTime));
    qds.addAll(cpu.collectData(instance, collectTime));
    qds.addAll(gc.collectData(instance, collectTime));

    return qds;
  }

  @Override
  protected Collection<QuotaInfo> registry(InstInfo instance) {
    String[] names = {"app,jvm,rate_heapmemory",
        "app,jvm,rate_nonheapmemory",
        "app,jvm,gc,ygc,count",
        "app,jvm,gc,ygc,time",
        "app,jvm,gc,fgc,count",
        "app,jvm,gc,fgc,time",
        "app,jvm,proc_cpuusage"};
    String[] descs = {"应用指标,jvm,堆内存占用率",
        "应用指标,jvm,非堆内存占用率",
        "应用指标,jvm,新生代gc次数",
        "应用指标,jvm,新生代gc耗时",
        "应用指标,jvm,老年代gc次数",
        "应用指标,jvm,老年代gc耗时",
        "应用指标,jvm,进程CPU使用率"};
    String[] units = {"%", "%", "次", "ms", "次", "ms", "%"};
    CycleInfo cycleInfo = cycle();
    List<QuotaInfo> qis = new ArrayList<>(names.length);
    for (int i = 0; i < names.length; i++) {
      QuotaInfo qi = new QuotaInfo(instance, names[i], descs[i], units[i], cycleInfo, Type.Data);
      qis.add(qi);
    }
    return qis;
  }

  @Override
  public String identify() {
    CycleInfo c = cycle();
    return String.join("", "jvm指标采集器[", c.getKey(), "]");
  }

  @Override
  public CycleInfo cycle() {
    String unit = context.getProperty("hbq.agent.jvm.cycle.unit");
    long t = context.getLongValue("hbq.agent.jvm.cycle.time", 30);
    return unit == null ? CycleInfo.SECOND30 : new CycleInfo(TimeUnit.valueOf(unit), t);
  }

  private MemInfo getMemInfo() {
    MemoryMXBean mmb = ManagementFactory.getMemoryMXBean();
    MemInfo memory = new MemInfo();
    memory.setMaxHeapMemory(format((double) (mmb.getHeapMemoryUsage().getMax() / MEGA_BYTE)));
    memory.setUsedHeapMemory(format((double) (mmb.getHeapMemoryUsage().getUsed() / MEGA_BYTE)));
    memory.setMaxNonHeapMemory(format((double) (mmb.getNonHeapMemoryUsage().getMax() / MEGA_BYTE)));
    memory
        .setUsedNonHeapMemory(format((double) (mmb.getNonHeapMemoryUsage().getUsed() / MEGA_BYTE)));
    memory.calc();
    return memory;
  }

  private GcInfo getGcInfo() {
    GcInfo info = new GcInfo();
    List<GarbageCollectorMXBean> gcs = ManagementFactory.getGarbageCollectorMXBeans();
    long fullCount = 0, fullTime = 0, youngCount = 0, youngTime = 0;
    for (GarbageCollectorMXBean gc : gcs) {
      switch (gc.getName().replaceAll(" ", "")) {
        case "MarkSweepCompact":
        case "PSMarkSweep":
        case "ConcurrentMarkSweep":
        case "G1OldGeneration":
          fullCount += gc.getCollectionCount();
          fullTime += gc.getCollectionTime();
          break;
        case "Copy":
        case "ParNew":
        case "PSScavenge":
        case "G1YoungGeneration":
          youngCount += gc.getCollectionCount();
          youngTime += gc.getCollectionTime();
          break;
      }
    }
    info.setYoungCount(youngCount);
    info.setFullCount(fullCount);
    info.setYoungTime(youngTime - this.youngTime);
    this.youngTime = youngTime;
    info.setFullTime(fullTime - this.fullTime);
    this.fullTime = fullTime;
    return info;
  }

  private CpuInfo getCpuInfo() {
    CpuInfo cpu = new CpuInfo();
    osMxBean = ManagementFactory.getOperatingSystemMXBean();
    threadBean = ManagementFactory.getThreadMXBean();
    try {
      long totalTime = 0;
      for (long id : threadBean.getAllThreadIds()) {
        totalTime += threadBean.getThreadCpuTime(id);
      }
      long curTime = System.nanoTime();
      long usedTime = totalTime - preUsedTime;
      long totalPassedTime = curTime - preTime;
      preTime = curTime;
      preUsedTime = totalTime;
      cpu.setProcUsage(
          FormatNumber.format(
              (((double) usedTime) / totalPassedTime / osMxBean.getAvailableProcessors()) * 100,
              2));
      cpu.setSysUsage(0d);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
    return cpu;
  }
}
