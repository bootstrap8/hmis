package com.github.hbq.agent.app.serv.impl.kafka;

import com.alibaba.fastjson.JSON;
import com.github.hbq.agent.app.pojo.QuotaData;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

/**
 * @author hbq
 */
public class QuotaDataDeserializer implements Deserializer<List<QuotaData>> {

  @Override
  public List<QuotaData> deserialize(String topic, byte[] bytes) {
    try {
      String str = new String(bytes, "utf-8");
      List<QuotaData> data = JSON.parseArray(str, QuotaData.class);
      return data;
    } catch (UnsupportedEncodingException e) {
      throw new SerializationException("Error when serializing string to byte[] due to unsupported encoding utf-8");
    }
  }
}
