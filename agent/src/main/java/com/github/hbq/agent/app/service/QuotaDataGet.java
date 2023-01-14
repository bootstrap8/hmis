package com.github.hbq.agent.app.service;

import com.github.hbq.agent.app.pojo.CycleInfo;
import com.github.hbq.agent.app.pojo.QuotaData;
import java.util.Collection;

/**
 * @author hbq
 */
public interface QuotaDataGet extends QuotaRegistry {

  Collection<QuotaData> collectData();

  String identify();

  CycleInfo cycle();

}
