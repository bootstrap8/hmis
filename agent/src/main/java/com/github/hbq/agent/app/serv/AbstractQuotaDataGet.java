package com.github.hbq.agent.app.serv;

import com.github.hbq.agent.app.pojo.InstInfo;
import com.github.hbq.agent.app.pojo.QuotaData;
import com.github.hbq.agent.app.pojo.QuotaInfo;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author hbq
 */
@Slf4j
public abstract class AbstractQuotaDataGet implements QuotaDataGet, InitializingBean {

  @Autowired
  private InstanceRegistry registry;

  @Autowired
  private QuotaManage quotaManage;

  @Autowired
  private QuotaSchedule schedule;

  private volatile boolean submit = true;

  @Override
  public void afterPropertiesSet() throws Exception {
    try {
      Collection<QuotaInfo> qis = registry();
      quotaManage.saveQuotaInfo(qis);
      if (submit) {
        schedule.submit(this);
      }
    } catch (Exception e) {
      log.error("保存注册指标失败,可能导致采集不到指标数据", e);
    }
  }

  public void setSubmit(boolean submit) {
    this.submit = submit;
  }

  @Override
  public Collection<QuotaData> collectData() {
    return collectData(registry.getInstance());
  }

  /**
   * 采集指标数据
   *
   * @param instance
   * @return
   */
  protected abstract Collection<QuotaData> collectData(InstInfo instance);

  @Override
  public Collection<QuotaInfo> registry() {
    return registry(registry.getInstance());
  }

  /**
   * 注册指标信息
   *
   * @param instance
   * @return
   */
  protected abstract Collection<QuotaInfo> registry(InstInfo instance);
}
