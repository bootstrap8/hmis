package com.github.hbq.common.spring.boot.encrypt.model;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

/**
 * @author hbq
 */
@Data
public class RSASignInfo {

  private String base64PrivateKey;
  private String encodeData;

  public boolean hasData() {
    return StringUtils.isNotBlank(encodeData);
  }

  @Override
  public String toString() {
    return JSON.toJSONString(this, true);
  }
}
