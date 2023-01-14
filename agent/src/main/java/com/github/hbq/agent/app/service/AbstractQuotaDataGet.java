package com.github.hbq.agent.app.service;

import com.github.hbq.agent.app.pojo.InstInfo;
import com.github.hbq.agent.app.pojo.QuotaData;
import com.github.hbq.agent.app.pojo.QuotaInfo;
import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author hbq
 */
public abstract class AbstractQuotaDataGet implements QuotaDataGet {

  @Autowired
  private InstanceRegistry registry;

  @Override
  public Collection<QuotaData> collectData() {
    return collectData(registry.getInstance());
  }

  protected abstract Collection<QuotaData> collectData(InstInfo instance);

  @Override
  public Collection<QuotaInfo> registry() {
    return registry(registry.getInstance());
  }

  protected abstract Collection<QuotaInfo> registry(InstInfo instance);
}
