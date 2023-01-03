package com.github.hbq.common.spring.cache.juc;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hbq
 */
@AllArgsConstructor
@Slf4j
public class ExpiryTask extends Thread {

  private NativeCache nativeCache;

  private DelayQueue<DelayKey> delayQueue;

  @Override
  public void run() {
    log.info("延时队列检出任务启动.");
    DelayKey key;
    while (true) {
      key = delayQueue.poll();
      if (key == null) {
        try {
          TimeUnit.MILLISECONDS.sleep(500);
        } catch (InterruptedException e) {
          break;
        }
        continue;
      }
      if (key.isExpired()) {
        continue;
      }
      if (log.isDebugEnabled()) {
        log.debug("Key: {}, ({},{}) 过期,, delay: {}",
            key.getKey(), key.getExpiry(), key.getUnit(), delayQueue.size());
      }
      nativeCache.remove(key.getKey());
    }
  }
}
