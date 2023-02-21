package com.github.hbq.agent.app.serv.impl;

import com.github.hbq.agent.app.pojo.CycleInfo;
import com.github.hbq.agent.app.pojo.InstInfo;
import com.github.hbq.agent.app.pojo.QuotaData;
import com.github.hbq.agent.app.pojo.QuotaInfo;
import com.github.hbq.agent.app.pojo.QuotaInfo.Type;
import com.github.hbq.agent.app.serv.AbstractQuotaDataGet;
import com.github.hbq.common.spring.context.SpringContext;
import com.github.hbq.common.utils.Count;
import com.github.hbq.common.utils.FormatNumber;
import com.github.hbq.common.utils.FormatTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.event.EventListener;

/**
 * @author hbq
 */
@Slf4j
public class KafkaQuotaGetImpl extends AbstractQuotaDataGet {

  private Count in = Count.safe();
  private Count out = Count.safe();

  private long lastTime = 0;

  @Autowired
  private SpringContext context;

  @EventListener
  public void envListener(EnvironmentChangeEvent event) {
    Set<String> set = event.getKeys();
    for (String key : set) {
      if (key.contains("hbq.agent.kafka")) {
        log.info("kafka流控指标采集器配置发生变化，需要重新注册指标。{}", set);
        setSubmit(false);
        try {
          afterPropertiesSet();
        } catch (Exception e) {
        }
        return;
      }
    }
  }

  public void incrementAndGetIn(int c) {
    in.incrementAndGet(c);
  }

  public void incrementAndGetOut(int c) {
    out.incrementAndGet(c);
  }

  @Override
  protected Collection<QuotaData> collectData(InstInfo instance) {
    CycleInfo cycle = cycle();
    long time = TimeUnit.SECONDS.convert(cycle.getTime(), cycle.getUnit());
    if (lastTime == 0) {
      lastTime = time;
    } else {
      lastTime = FormatTime.nowSecs() - lastTime;
    }
    long collectTime = FormatTime.nowSecs();
    List<QuotaData> data = new ArrayList<>(2);
    QuotaInfo qi = new QuotaInfo(instance, "app,kafka,in", "应用指标,kafka,入口消息量", "条/秒", cycle, Type.Data);
    QuotaData qd = new QuotaData(qi);
    qd.collectData(FormatNumber.format((double) (in.longValue() / lastTime), 0), collectTime);
    data.add(qd);

    qi = new QuotaInfo(instance, "app,kafka,out", "应用指标,kafka,出口消息量", "条/秒", cycle, Type.Data);
    qd = new QuotaData(qi);
    qd.collectData(FormatNumber.format((double) (out.longValue() / lastTime), 0), collectTime);
    data.add(qd);

    lastTime = FormatTime.nowSecs();
    in.reset();
    out.reset();
    if (log.isDebugEnabled()) {
      log.debug("采集到应用出入口消息指标数据: {} 个", data.size());
    }
    return data;
  }

  @Override
  protected Collection<QuotaInfo> registry(InstInfo instance) {
    String[] names = {"app,kafka,in", "app,kafka,out"};
    String[] descs = {"应用指标,kafka,入口消息量", "应用指标,kafka,出口消息量"};
    String[] units = {"条/秒", "条/秒"};
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
    return String.join("", "应用kafka消息处理指标采集器[", c.getKey(), "]");
  }

  @Override
  public CycleInfo cycle() {
    String unit = context.getProperty("hbq.agent.kafka.cycle.unit");
    long t = context.getLongValue("hbq.agent.kafka.cycle.time", 5);
    return unit == null ? CycleInfo.SECOND5 : new CycleInfo(TimeUnit.valueOf(unit), t);
  }
}
