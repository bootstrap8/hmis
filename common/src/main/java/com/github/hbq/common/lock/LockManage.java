package com.github.hbq.common.lock;

import com.github.hbq.common.help.ServiceRegistry;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hbq
 */
@Slf4j
public class LockManage implements ServiceRegistry<String, Lock> {

  private Map<String, Lock> locks = new ConcurrentHashMap<>();

  private volatile String key;

  @Override
  public void registry(String key, Lock lock) {
    this.key = key;
    locks.putIfAbsent(key, lock);
    log.info("注册分布式锁实现, key: {}, lock: {}", key, lock);
  }

  @Override
  public Lock query(String key) {
    Lock lock = locks.get(key);
    if (Objects.isNull(lock)) {
      throw new UnsupportedOperationException("未注册任何分布式锁实现");
    } else {
      return lock;
    }
  }

  @Override
  public Lock query() {
    if (Objects.isNull(this.key)) {
      throw new UnsupportedOperationException("未注册任何分布式锁实现");
    }
    return locks.get(this.key);
  }
}
