package com.github.hbq.common.spring.cache.juc;

import cn.hutool.core.lang.Assert;
import com.github.hbq.common.spring.cache.ExpiryKey;
import java.util.concurrent.Callable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.lang.Nullable;

/**
 * @author hbq
 */
@Slf4j
public class JucCache extends AbstractValueAdaptingCache {

  private NativeCache nativeCache;

  private String name;

  public JucCache(NativeCache nativeCache) {
    this("default", nativeCache, true);
  }

  public JucCache(NativeCache nativeCache, boolean allowNullValues) {
    this("default", nativeCache, allowNullValues);
  }

  public JucCache(String name, NativeCache nativeCache, boolean allowNullValues) {
    super(allowNullValues);
    this.name = name;
    this.nativeCache = nativeCache;
  }

  @Nullable
  @Override
  protected Object lookup(Object key) {
    Assert.notNull(key);
    ExpiryKey expire = (ExpiryKey) key;
    return this.nativeCache.get(expire.getKey());
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public Object getNativeCache() {
    return this.nativeCache;
  }

  @Nullable
  @Override
  public synchronized <T> T get(Object key, Callable<T> valueLoader) {
    Assert.notNull(key);
    ExpiryKey expire = (ExpiryKey) key;
    try {
      T v = (T) this.lookup(expire);
      if (v == null) {
        v = valueLoader.call();
      }
      this.put(expire, v);
      return v;
    } catch (Exception e) {
      return null;
    }
  }

  @Override
  public synchronized void put(Object key, @Nullable Object value) {
    Assert.notNull(key);
    if (!this.isAllowNullValues() && value == null) {
      throw new NullPointerException(String.format("Key: [%s], Value is null, isAllowNullValues [no]", key));
    }
    ExpiryKey expire = (ExpiryKey) key;
    this.nativeCache.put(expire.getKey(), value, expire.getExpiry(), expire.getUnit());
  }

  @Nullable
  @Override
  public synchronized ValueWrapper putIfAbsent(Object key, @Nullable Object value) {
    Assert.notNull(key);
    if (!this.isAllowNullValues() && value == null) {
      throw new NullPointerException(String.format("Key: [%s], Value is null, isAllowNullValues [no]", key));
    }
    ExpiryKey expire = (ExpiryKey) key;
    String k = expire.getKey();
    Object v = this.nativeCache.get(k);
    if (v == null) {
      this.nativeCache.put(k, value, expire.getExpiry(), expire.getUnit());
      return null;
    } else {
      return new SimpleValueWrapper(v);
    }
  }

  @Override
  public synchronized void evict(Object key) {
    ExpiryKey expire = (ExpiryKey) key;
    this.nativeCache.remove(expire.getKey());
  }

  @Override
  public synchronized void clear() {
    this.nativeCache.clear();
  }
}
