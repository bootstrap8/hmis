package com.github.hbq.agent.app.serv;

import com.github.hbq.agent.app.pojo.CycleInfo;
import com.github.hbq.agent.app.pojo.QuotaData;
import java.util.Collection;

/**
 * @author hbq
 */
public interface QuotaDataGet extends QuotaRegistry {

  /**
   * 采集指标数据
   *
   * @return
   */
  Collection<QuotaData> collectData();

  /**
   * 定义采集器名称
   *
   * @return
   */
  String identify();

  /**
   * 采集周期定义
   *
   * @return
   */
  CycleInfo cycle();

}
