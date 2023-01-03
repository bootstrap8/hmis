package com.github.hbq.common.spring.boot.encrypt.config;

import com.github.hbq.common.spring.boot.encrypt.utils.AESUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author hbq
 **/
@Data
@ConfigurationProperties(prefix = "hbq.common.restful.encrypt.aes")
@Slf4j
public class AESConfig {

  private String charset = "utf-8";

  private volatile boolean enable = false;

  private volatile boolean showLog = false;

  private volatile String key = AESUtil.KEY;
}
