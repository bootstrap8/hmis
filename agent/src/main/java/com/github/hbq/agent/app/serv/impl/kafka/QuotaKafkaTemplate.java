package com.github.hbq.agent.app.serv.impl.kafka;

import com.github.hbq.agent.app.pojo.QuotaData;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * @author hbq
 */
public class QuotaKafkaTemplate implements InitializingBean {

  private KafkaTemplate proxy;

  @Autowired
  private KafkaTemplate kafka;

  @Override
  public void afterPropertiesSet() throws Exception {
    if (kafka != null) {
      Map<String, Object> copy = kafka.getProducerFactory().getConfigurationProperties();
      Serializer<List<QuotaData>> valueSerializer = new QuotaDataSerializer();
      DefaultKafkaProducerFactory factory = new DefaultKafkaProducerFactory(copy, new StringSerializer(), valueSerializer);
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
}
