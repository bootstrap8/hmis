package com.github.hbq.monitor.mq;

import com.github.hbq.agent.app.pojo.QuotaData;
import com.github.hbq.monitor.serv.MonitorService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author hbq
 */
@Component("monitor-in-QuotaDataConsumer")
@Slf4j
public class QuotaDataConsumer implements InitializingBean {

  @Autowired
  private MonitorService monitorService;

  @Override
  public void afterPropertiesSet() throws Exception {
  }

  @KafkaListener(topics = {"HBQ-AGENT-QUOTA-DATA"}, properties = {"#{'${hbq.agent.kafka.consumer.configs}'.split(';')}"})
  void consume(List<QuotaData> list) {
    log.info("接收到指标数据: {} 条", list.size());
    try {
      monitorService.saveQuotaData(list);
      for (QuotaData qd : list) {
        monitorService.checkWarnRule(qd);
      }
    } catch (Exception e) {
      log.error("", e);
    }
  }
}
