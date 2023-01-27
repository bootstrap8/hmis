package com.github.hbq.monitor.dao.dialect.manage;

import com.github.hbq.common.help.OptionalRegistry;
import com.github.hbq.monitor.dao.dialect.DialectHelper;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author hbq
 */
@Component("monitor-dao-dialect-DialectManage")
@Slf4j
public class DialectManage implements OptionalRegistry<String, DialectHelper> {

  private Map<String, DialectHelper> dialects = new HashMap<>();

  @Value("${hbq.monitor.dialect:h2db}")
  private String dialectKey;

  @Override
  public void registry(String key, DialectHelper dialectHelper) {
    dialects.put(key, dialectHelper);
    log.info("注册dialect: {}, {}", key, dialectHelper);
  }

  @Override
  public Optional<DialectHelper> query(String key) {
    return Optional.ofNullable(dialects.get(key));
  }

  @Override
  public Optional<DialectHelper> query() {
    return query(dialectKey);
  }
}
