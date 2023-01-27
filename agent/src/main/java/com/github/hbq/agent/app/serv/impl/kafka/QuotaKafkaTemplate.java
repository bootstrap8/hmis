package com.github.hbq.agent.app.serv.impl.kafka;

import com.alibaba.fastjson.JSON;
import com.github.hbq.common.utils.StrUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * @author hbq
 */
@Slf4j
public class QuotaKafkaTemplate implements InitializingBean {

  private KafkaTemplate proxy;

  @Autowired(required = false)
  private KafkaTemplate kafka;

  @Value("${hbq.agent.kafka.configs:{}}")
  private String quotaKafkaConfig;

  @Override
  public void afterPropertiesSet() throws Exception {
    if (Objects.nonNull(kafka)) {
      Map<String, Object> map = new HashMap<>(16);
      Map<String, Object> configs = kafka.getProducerFactory().getConfigurationProperties();
      map.putAll(configs);
      if (StrUtils.strNotEmpty(quotaKafkaConfig)) {
        Map custom = JSON.parseObject(quotaKafkaConfig);
        map.putAll(custom);
      }
      log.info("推送应用指标数据的kafka集群配置: {}", JSON.toJSONString(map));
      DefaultKafkaProducerFactory factory = new DefaultKafkaProducerFactory(map);
      proxy = new KafkaTemplate(factory);
    }
  }

  public void push(String topic, Object key, Object data) {
    if (Objects.nonNull(proxy)) {
      if (Objects.isNull(key)) {
        proxy.send(topic, data);
      } else {
        proxy.send(topic, key, data);
      }
    }
  }

  public static void main(String[] args) throws Exception {
    String str = "{\"bootstrap.servers\":[\"192.168.56.2:9092\"],\"value.serializer\":\"com.github.hbq.agent.app.serv.impl.kafka.QuotaDataSerializer\"}";
    Map custom = JSON.parseObject(str);
    System.out.println(custom);
  }
}
