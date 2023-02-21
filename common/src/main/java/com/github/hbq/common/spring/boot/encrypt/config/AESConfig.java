package com.github.hbq.common.spring.boot.encrypt.config;

import com.github.hbq.common.spring.boot.encrypt.utils.AESUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * @author hbq
 **/
@RefreshScope
@Data
@ConfigurationProperties(prefix = "hbq.common.restful.encrypt.aes")
@Slf4j
public class AESConfig {

  private volatile String charset = "utf-8";

  private volatile boolean enable = false;

  private volatile boolean showLog = false;

  private volatile String key = AESUtil.KEY;
}
