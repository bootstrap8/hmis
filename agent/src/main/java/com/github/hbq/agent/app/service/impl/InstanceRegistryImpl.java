package com.github.hbq.agent.app.service.impl;

import com.github.hbq.agent.app.dao.AgentDao;
import com.github.hbq.agent.app.pojo.InstInfo;
import com.github.hbq.agent.app.service.AppInfoRegistry;
import com.github.hbq.agent.app.service.InstanceRegistry;
import com.github.hbq.common.spring.context.SpringContext;
import com.github.hbq.common.utils.CmdUtils;
import com.github.hbq.common.utils.HostInfo;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author hbq
 */
@Slf4j
public class InstanceRegistryImpl implements InstanceRegistry, InitializingBean, DisposableBean {

  @Autowired
  private AppInfoRegistry registry;

  @Autowired
  private SpringContext context;

  private InstInfo instance;

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
          dao.createInstInfo();
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
    String dc = context.getProperty("agent.data-center", "dc");
    int port = context.getIntValue("server.port", 8080);
    HostInfo host = CmdUtils.getLocalAddress();
    this.instance = new InstInfo(registry.getApp(), dc, host.getIp(),
        host.getHostName(), port, CmdUtils.getProcessNo());
    log.info("注册的应用实例信息: {}", this.instance);
    if (dao != null) {
      dao.saveInstInfo(this.instance.toMybatisMap());
    }
  }

  @Override
  public void destroy() throws Exception {
    SqlSessionTemplate stmt = context.getBean(SqlSessionTemplate.class);
    if (stmt != null) {
      AgentDao dao = stmt.getMapper(AgentDao.class);
      try {
        dao.deleteInstInfo(instance.getDataCenter(), this.instance.getIp(), instance.getPort());
        log.info("应用实例注销成功: {}", instance);
      } catch (Exception e) {
      }
    }
  }

  @Override
  public InstInfo getInstance() {
    return this.instance;
  }
}
