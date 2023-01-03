package com.github.hbq.common.ids;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.github.hbq.common.ids.Snowflake;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import org.junit.Assert;
import org.junit.jupiter.api.Test;

/**
 * @author hbq
 */
public class SnowflakeTest {

  @Test
  public void testSnowFlake() {
    Snowflake sf = new Snowflake();
    int c = 1000000;
    int n = 10;
    CountDownLatch cdl = new CountDownLatch(n);
    Set<Long> set = new ConcurrentHashSet<>(c * n);
    for (int i = 0; i < n; i++) {
      new Thread(() -> {
        Long v;
        for (int i1 = 0; i1 < c; i1++) {
          v = Long.valueOf(sf.nextId());
          set.add(v);
        }
        cdl.countDown();
      }).start();
    }
    try {
      cdl.await();
    } catch (InterruptedException e) {
    }
    Assert.assertTrue(n * c == set.size());
  }
}
