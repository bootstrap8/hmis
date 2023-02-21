package com.github.hbq.common.spring.cache;

import com.github.hbq.common.spring.cache.juc.JucCache;
import com.github.hbq.common.spring.cache.juc.NativeCache;
import com.google.common.collect.ImmutableList;
import java.util.Collection;
import lombok.Data;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.lang.Nullable;

/**
 * @author hbq
 */
@RefreshScope
@Data
public class CacheConfig implements FactoryBean<CacheManager> {

  @Value("${spring.cache.spi.juc.maxCapacity:10000}")
  private volatile Integer maxCapacity;

  protected Collection<Cache> buildCaches()
      throws Exception {
    NativeCache nativeCache = new NativeCache();
    nativeCache.setMaxCapacity(maxCapacity);
    nativeCache.afterPropertiesSet();
    return ImmutableList.of(new JucCache(nativeCache));
  }

  @Nullable
  @Override
  public CacheManager getObject()
      throws Exception {
    SimpleCacheManager cacheManager = new SimpleCacheManager();
    cacheManager.setCaches(buildCaches());
    cacheManager.afterPropertiesSet();
    return cacheManager;
  }

  @Nullable
  @Override
  public Class<?> getObjectType() {
    return CacheManager.class;
  }

  @Override
  public boolean isSingleton() {
    return true;
  }
}
