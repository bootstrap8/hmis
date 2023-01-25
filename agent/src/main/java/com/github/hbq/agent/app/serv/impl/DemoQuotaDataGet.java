package com.github.hbq.agent.app.serv.impl;

import com.github.hbq.agent.app.pojo.CycleInfo;
import com.github.hbq.agent.app.pojo.DataInfo;
import com.github.hbq.agent.app.pojo.InstInfo;
import com.github.hbq.agent.app.pojo.QuotaData;
import com.github.hbq.agent.app.pojo.QuotaInfo;
import com.github.hbq.agent.app.pojo.QuotaInfo.Type;
import com.github.hbq.agent.app.serv.AbstractQuotaDataGet;
import com.github.hbq.common.utils.FormatTime;
import com.google.common.collect.Lists;
import java.util.Collection;

/**
 * @author hbq
 */
public class DemoQuotaDataGet extends AbstractQuotaDataGet {

  @Override
  protected Collection<QuotaData> collectData(InstInfo instance) {
    CycleInfo cycleInfo = cycle();
    QuotaInfo qi = new QuotaInfo(instance, "app,jvm,rate_heapmemory", "应用指标,jvm,堆内存占用率", "%", cycleInfo, Type.Data);
    QuotaData data = new QuotaData(qi);
    data.collectData(new DataInfo(83.2, ""), FormatTime.nowSecs());
    return Lists.newArrayList(data);
  }

  @Override
  protected Collection<QuotaInfo> registry(InstInfo instance) {
    CycleInfo cycleInfo = cycle();
    QuotaInfo qi = new QuotaInfo(instance, "app,jvm,rate_heapmemory", "应用指标,jvm,堆内存占用率", "%", cycleInfo, Type.Data);
    return Lists.newArrayList(qi);
  }

  @Override
  public String identify() {
    CycleInfo c = cycle();
    return String.join("", "Demo指标采集器[", c.getKey(), "]");
  }

  @Override
  public CycleInfo cycle() {
    return CycleInfo.SECOND30;
  }
}
