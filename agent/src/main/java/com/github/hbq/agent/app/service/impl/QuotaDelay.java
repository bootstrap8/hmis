package com.github.hbq.agent.app.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.hbq.agent.app.pojo.QuotaData;
import com.github.hbq.agent.app.service.InstanceRegistry;
import com.github.hbq.agent.app.service.QuotaDataGet;
import com.github.hbq.common.spring.context.SpringContext;
import java.util.Collection;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * @author hbq
 */
@Slf4j
public class QuotaDelay implements Delayed {

  private long initMills;

  @Getter
  private long delayMills;

  private SpringContext context;

  private QuotaDataGet get;

  public QuotaDelay(long delayMills, SpringContext context, QuotaDataGet get) {
    this.delayMills = delayMills;
    this.context = context;
    this.get = get;
    this.initMills = System.currentTimeMillis();
  }

  @Override
  public long getDelay(TimeUnit unit) {
    long diff = delayMills - (System.currentTimeMillis() - initMills);
    return diff;
  }

  @Override
  public int compareTo(Delayed o) {
    if (o == this) {
      return 0;
    }
    long d = (getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
    return (d == 0) ? 0 : (d < 0) ? -1 : 1;
  }

  public String identify() {
    return get.identify();
  }

  public void reset() {
    this.initMills = System.currentTimeMillis();
  }

  public void collect() {
    KafkaTemplate kafka = context.getBean(KafkaTemplate.class);
    InstanceRegistry instanceRegistry = context.getBean(InstanceRegistry.class);
    if (kafka != null) {
      Collection<QuotaData> data = get.collectData();
      String msg = JSON.toJSONString(data);
      if (log.isDebugEnabled()) {
        log.debug("推送指标数据至kafka: {}", msg);
      }
      if (instanceRegistry == null) {
        kafka.send("HBQ-AGENT-QUOTA-DATA", msg);
      } else {
        String key = instanceRegistry.getInstance().getKey();
        kafka.send("HBQ-AGENT-QUOTA-DATA", key, msg);
      }
    }
  }
}
