package com.github.hbq.common.spring.boot.encrypt.model;

import com.alibaba.fastjson.JSON;
import lombok.Data;

/**
 * @author hbq
 */
@Data
public class AESInfo {

  private String key;
  private String charset = "utf-8";
  private String content;

  @Override
  public String toString() {
    return JSON.toJSONString(this, true);
  }
}
