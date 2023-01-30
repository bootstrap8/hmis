package com.github.hbq.agent.app.serv.impl;

import com.github.hbq.agent.app.dao.impl.AgentDaoOptional;
import com.github.hbq.agent.app.pojo.QuotaInfo;
import com.github.hbq.agent.app.serv.QuotaManage;
import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author hbq
 */
@Slf4j
public class QuotaManageImpl implements QuotaManage, InitializingBean {

  @Autowired
  private AgentDaoOptional optional;

  @Override
  public void afterPropertiesSet() throws Exception {
    createQuotaInfo();
  }

  private void createQuotaInfo() {
    optional.getAgentDao().ifPresent(agentDao -> {
      try {
        agentDao.createQuotaInfo();
        log.info("创建指标配置表成功");
      } catch (Exception e) {
        log.info("指标配置表已经存在");
      }
    });
  }

  @Override
  public void saveQuotaInfo(Collection<QuotaInfo> qis) {
    optional.getAgentDao().ifPresent(agentDao -> {
      qis.forEach(quota -> {
        try {
          agentDao.deleteQuotaInfo(quota.getInstInfo().getApp().getName(), quota.getName());
          agentDao.saveQuotaInfo(quota.toMybatisMap());
        } catch (Exception e) {
          log.error(String.format("保存注册指标[%s]失败", quota.getInstQuotaKey()), e);
        }
      });
    });
  }
}
