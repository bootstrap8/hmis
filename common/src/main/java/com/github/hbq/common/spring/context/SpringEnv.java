package com.github.hbq.common.spring.context;

import java.util.Map;
import java.util.Set;

/**
 * @author hbq
 */
public interface SpringEnv {

  /**
   * 返回spring所有配置属性(此方法存在问题，请勿使用)
   *
   * @return
   */
  @Deprecated
  Map<String, Object> getEnvironmentVariables();

  /**
   * 返回spring所有配置属性key
   *
   * @return
   */
  Set<String> getEnvironmentPropertyKeys();
}
