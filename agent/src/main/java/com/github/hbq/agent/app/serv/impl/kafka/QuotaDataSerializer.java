package com.github.hbq.agent.app.serv.impl.kafka;

import com.alibaba.fastjson.JSON;
import com.github.hbq.agent.app.pojo.QuotaData;
import java.io.UnsupportedEncodingException;
import java.util.List;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;

/**
 * @author hbq
 */
public class QuotaDataSerializer implements Serializer<List<QuotaData>> {

  @Override
  public byte[] serialize(String topic, List<QuotaData> list) {
    String str = JSON.toJSONString(list);
    try {
      return str.getBytes("utf-8");
    } catch (UnsupportedEncodingException e) {
      throw new SerializationException("Error when serializing string to byte[] due to unsupported encoding utf-8");
    }
  }
}
