package com.github.hbq.agent.app.service.impl;

import com.github.hbq.agent.app.dao.AgentDao;
import com.github.hbq.agent.app.pojo.AppInfo;
import com.github.hbq.agent.app.service.AppInfoRegistry;
import com.github.hbq.common.spring.context.SpringContext;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
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

  private AgentDao dao;


  @Override
  public void afterPropertiesSet() throws Exception {
    try {
      SqlSessionTemplate stmt = context.getBean(SqlSessionTemplate.class);
      if (stmt != null) {
        dao = stmt.getMapper(AgentDao.class);
      }
      if (dao != null) {
        try {
          dao.createAppInfo();
        } catch (Exception e) {
        }
      }
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
    if (dao != null) {
      dao.saveAppInfo(this.app.toMybatisMap());
    }
  }

  @Override
  public AppInfo getApp() {
    return app;
  }
}
