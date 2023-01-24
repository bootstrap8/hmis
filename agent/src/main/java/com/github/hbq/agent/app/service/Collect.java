package com.github.hbq.agent.app.service;

import com.github.hbq.agent.app.pojo.InstInfo;
import com.github.hbq.agent.app.pojo.QuotaData;
import java.util.Collection;

/**
 * @author hbq
 */
public interface Collect {

  /**
   * 采集指标数据辅助接口
   *
   * @param instInfo
   * @param collectTime
   * @return
   */
  Collection<QuotaData> collectData(InstInfo instInfo, long collectTime);
}
