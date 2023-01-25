package com.github.hbq.agent.app.serv;

import com.github.hbq.agent.app.pojo.QuotaInfo;
import java.util.Collection;

/**
 * @author hbq
 */
public interface QuotaRegistry {

  /**
   * 指标注册
   *
   * @return
   */
  Collection<QuotaInfo> registry();
}
