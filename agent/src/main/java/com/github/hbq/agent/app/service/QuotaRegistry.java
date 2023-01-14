package com.github.hbq.agent.app.service;

import com.github.hbq.agent.app.pojo.QuotaInfo;
import java.util.Collection;

/**
 * @author hbq
 */
public interface QuotaRegistry {

  Collection<QuotaInfo> registry();
}
