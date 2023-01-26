package com.github.hbq.agent.app.serv;

import java.util.Map;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author hbq
 */
public interface AgentService {

  /**
   * 保存kafka入口消息速率应用配置
   *
   * @param map
   */
  @Transactional(rollbackFor = Exception.class)
  void saveKafkaInAppRateLimiter(Map map);

  /**
   * 保存kafka入口消息速率应用实例配置
   *
   * @param map
   */
  @Transactional(rollbackFor = Exception.class)
  void saveKafkaInInstanceRateLimiter(Map map);
}
