package com.github.hbq.manage.agent;

import com.alibaba.fastjson.JSON;
import com.github.hbq.agent.app.pojo.QuotaData;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author hbq
 */
//@Component
@Slf4j
public class KafkaQuotaDemo {

  //com.github.hbq.agent.app.serv.impl.kafka.QuotaDataDeserializer
  @KafkaListener(topics = {"HBQ-AGENT-QUOTA-DATA"}, properties = {"value.deserializer:com.github.hbq.agent.app.serv.impl.kafka.QuotaDataDeserializer"})
  public void rec(List<QuotaData> list) {
    list.forEach(q -> {
      log.info("客户端接收到消息: {}", JSON.toJSONString(q));
    });
  }
}
