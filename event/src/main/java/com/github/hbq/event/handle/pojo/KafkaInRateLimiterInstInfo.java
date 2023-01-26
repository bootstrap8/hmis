package com.github.hbq.event.handle.pojo;

import com.google.common.base.Joiner;
import lombok.Data;

/**
 * @author hbq
 */
@Data
public class KafkaInRateLimiterInstInfo {

  private String dataCenter;
  private String ip;
  private int port;

  private int permits;

  public KafkaInRateLimiterInstInfo() {
  }

  public KafkaInRateLimiterInstInfo(String dataCenter, String ip, int port) {
    this.dataCenter = dataCenter;
    this.ip = ip;
    this.port = port;
  }

  public String getKey() {
    return Joiner.on(",").join(dataCenter, ip, port);
  }
}
