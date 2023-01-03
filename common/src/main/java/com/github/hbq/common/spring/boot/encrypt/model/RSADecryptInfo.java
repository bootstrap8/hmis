package com.github.hbq.common.spring.boot.encrypt.model;

import com.alibaba.fastjson.JSON;
import java.util.Objects;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.formula.functions.T;

/**
 * @author hbq
 */
@Data
public class RSADecryptInfo {

  private String privateKey;
  private String encode;
  private String charset = "utf-8";

  public boolean valid() {
    return StringUtils.isNotBlank(privateKey) && StringUtils.isNotBlank(encode);
  }

  @Override
  public String toString() {
    return JSON.toJSONString(this, true);
  }
}
