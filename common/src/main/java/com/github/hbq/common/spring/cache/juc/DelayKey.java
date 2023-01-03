package com.github.hbq.common.spring.cache.juc;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hbq
 */
@Slf4j
@Data
public class DelayKey implements Delayed {

  private long initMills;

  private long expiry;

  private TimeUnit unit;

  private String key;

  private volatile boolean expired = false;

  public DelayKey(String key, long expiry, TimeUnit unit) {
    this.initMills = System.currentTimeMillis();
    this.key = key;
    this.expiry = expiry;
    this.unit = unit;
  }

  @Override
  public long getDelay(TimeUnit tu) {
    if (isExpired()) {
      return 0;
    }
    long delay = TimeUnit.MILLISECONDS.convert(expiry, unit) - now();
    return tu.convert(delay, TimeUnit.MILLISECONDS);
  }

  @Override
  public int compareTo(Delayed other) {
    if (other == this) {
      return 0;
    }
    long d = (getDelay(TimeUnit.MILLISECONDS) - other.getDelay(TimeUnit.MILLISECONDS));
    return (d == 0) ? 0 : ((d < 0) ? -1 : 1);
  }

  private long now() {
    return System.currentTimeMillis() - initMills;
  }

  public void expire() {
    this.expired = true;
  }

  public boolean isExpired() {
    return this.expired;
  }
}
