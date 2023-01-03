package com.github.hbq.common.spring.cache;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.github.hbq.common.spring.cache.juc.JucCache;
import com.github.hbq.common.spring.cache.juc.NativeCache;
import com.github.hbq.common.utils.UidBox;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author hbq
 */
@Slf4j
public class CacheTest {

  @Test
  public void testSpringCache() throws Exception {
    NativeCache nativeCache = new NativeCache();
    nativeCache.afterPropertiesSet();
    JucCache cache = new JucCache(nativeCache, true);
    int c = 100000;
    CountDownLatch cdl = new CountDownLatch(c);
    Set<String> set = new ConcurrentHashSet<>();
    Random rd = new Random();
    ExpiryKey key = new ExpiryKey("foo", TimeUnit.DAYS, 1);
    for (int i = 0; i < c; i++) {
      CompletableFuture.runAsync(() -> {
        Object v = cache.get(key, () -> {
          TimeUnit.MILLISECONDS.sleep(rd.nextInt(10));
          return UidBox.uid();
        });
        set.add(v.toString());
        cdl.countDown();
      });
    }
    cdl.await();
    Assert.assertEquals(1, set.size());
  }
}
