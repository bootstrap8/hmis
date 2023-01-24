package com.github.hbq.agent.app.service;

import com.github.hbq.agent.app.pojo.QuotaInfo;
import java.util.Collection;

/**
 * @author hbq
 */
public interface QuotaManage {

  /**
   * 保存注册的指标信息
   *
   * @param qis
   */
  void saveQuotaInfo(Collection<QuotaInfo> qis);

}
