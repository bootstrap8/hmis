package com.github.hbq.common.spring.cache.juc;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * @author hbq
 */
@Slf4j
public class NativeCache extends LinkedHashMap<String, Object> implements InitializingBean, DisposableBean {

  @Setter
  private Integer maxCapacity = 10000;

  private DelayQueue<DelayKey> delayQueue = new DelayQueue<>();

  private ExpiryTask expire;

  private Map<String, DelayKey> dks;

  @Override
  public void afterPropertiesSet() throws Exception {
    this.dks = new ConcurrentHashMap<>(maxCapacity);
    this.expire = new ExpiryTask(this, delayQueue);
    this.expire.setName("Spring方法缓存清理线程");
    this.expire.start();
  }

  @Override
  protected boolean removeEldestEntry(Map.Entry eldest) {
    return super.size() > maxCapacity;
  }

  @Override
  public Object put(String key, Object value) {
    if (log.isDebugEnabled()) {
      log.debug("call put.., Key: {}", key);
    }
    return this.put(key, value, 10, TimeUnit.MINUTES);
  }

  @Override
  public Object putIfAbsent(String key, Object value) {
    if (log.isDebugEnabled()) {
      log.debug("call putIfAbsent.., Key: {}", key);
    }
    Object v = super.get(key);
    if (v == null) {
      this.put(key, value, 10, TimeUnit.MINUTES);
      return null;
    } else {
      return v;
    }
  }

  @Override
  public Object remove(Object key) {
    if (log.isDebugEnabled()) {
      log.debug("call remove.., Key: {}", key);
    }
    DelayKey dk = dks.remove(key);
    if (dk != null) {
      dk.expire();
    }
    Object v = super.remove(key);
    if (log.isDebugEnabled()) {
      log.debug("info(dks: {}, delay: {}, cache: {})",
          dks.size(), delayQueue.size(), super.size());
    }
    return v;
  }

  public Object put(String key, Object value, long expiry, TimeUnit unit) {
    if (log.isDebugEnabled()) {
      log.debug("call put(expire).., Key: {}", key);
    }
    DelayKey dk = new DelayKey(key, expiry, unit);
    Object result = super.put(key, value);
    delayQueue.offer(dk);
    dks.put(key, dk);
    if (log.isDebugEnabled()) {
      log.debug("info(dks: {}, delay: {}, cache: {})",
          dks.size(), delayQueue.size(), super.size());
    }
    return result;
  }

  @Override
  public void destroy() throws Exception {
    this.expire.interrupt();
    delayQueue.clear();
    super.clear();
    dks.clear();
  }

  public Integer getMaxCapacity() {
    return maxCapacity;
  }
}
