package com.github.hbq.common.spring.cache;

import com.alibaba.fastjson.JSON;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author hbq
 */
@AllArgsConstructor
@Data
public class ExpiryKey {

  private String key;

  private TimeUnit unit;

  private long expiry;

  @Override
  public String toString() {
    return JSON.toJSONString(this);
  }
}
