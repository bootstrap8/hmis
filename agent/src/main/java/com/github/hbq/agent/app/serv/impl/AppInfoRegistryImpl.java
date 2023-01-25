package com.github.hbq.agent.app.serv.impl;

import com.github.hbq.agent.app.dao.impl.AgentDaoOptional;
import com.github.hbq.agent.app.pojo.AppInfo;
import com.github.hbq.agent.app.serv.AppInfoRegistry;
import com.github.hbq.common.spring.context.SpringContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author hbq
 */
@Slf4j
public class AppInfoRegistryImpl implements AppInfoRegistry, InitializingBean {

  @Autowired
  private SpringContext context;

  private AppInfo app;

  @Autowired
  private AgentDaoOptional optional;


  @Override
  public void afterPropertiesSet() throws Exception {
    try {
      optional.getAgentDao().ifPresent(agentDao -> {
        try {
          agentDao.createAppInfo();
          log.info("应用配置表创建成功");
        } catch (Exception e) {
          log.info("应用配置表已经存在");
        }
      });
      registry();
    } catch (Exception e) {
      log.error("初始化异常", e);
    }
  }

  @Override
  public void registry() {
    String name = context.getProperty("spring.application.name");
    String desc = context.getProperty("spring.application.desc", name);
    this.app = new AppInfo(name, desc);
    log.info("注册的应用信息；{}", this.app);
    optional.getAgentDao().ifPresent(agentDao -> agentDao.saveAppInfo(this.app.toMybatisMap()));
  }

  @Override
  public AppInfo getApp() {
    return app;
  }
}
