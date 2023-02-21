package com.github.hbq.common.spring.boot.encrypt.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * @author hbq
 **/
@RefreshScope
@Data
@ConfigurationProperties(prefix = "hbq.common.restful.encrypt.rsa")
@Slf4j
public class RSAConfig {

  private volatile String privateKey;

  private volatile String publicKey;

  private volatile String charset = "utf-8";

  private volatile boolean enable = false;

  private volatile boolean showLog = false;

  /**
   * 请求数据时间戳校验时间差 超过指定时间的数据认定为伪造
   */
  private volatile boolean timestampCheck = false;
}
