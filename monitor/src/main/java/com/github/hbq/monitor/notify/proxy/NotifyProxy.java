package com.github.hbq.monitor.notify.proxy;

import com.github.hbq.common.help.OptionalRegistry;
import com.github.hbq.monitor.notify.INotify;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author hbq
 */
@Component("monitor-notify-NotifyProxy")
@Slf4j
public class NotifyProxy implements OptionalRegistry<String, INotify> {

  private Map<String, INotify> notifies = new ConcurrentHashMap<>();

  @Override
  public void registry(String key, INotify iNotify) {
    notifies.put(key, iNotify);
    log.info("注册通知实现: {}, {}", key, iNotify);
  }

  @Override
  public Optional<INotify> query(String key) {
    return Optional.ofNullable(notifies.get(key));
  }
}
