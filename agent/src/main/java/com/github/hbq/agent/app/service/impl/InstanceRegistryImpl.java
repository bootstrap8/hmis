package com.github.hbq.agent.app.service.impl;

import com.github.hbq.agent.app.dao.impl.AgentDaoOptional;
import com.github.hbq.agent.app.pojo.InstInfo;
import com.github.hbq.agent.app.service.AppInfoRegistry;
import com.github.hbq.agent.app.service.InstanceRegistry;
import com.github.hbq.common.spring.context.SpringContext;
import com.github.hbq.common.utils.CmdUtils;
import com.github.hbq.common.utils.HostInfo;
import lombok.extern.slf4j.Slf4j;
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

  @Autowired
  private AgentDaoOptional optional;

  @Override
  public void afterPropertiesSet() throws Exception {
    try {
      optional.getAgentDao().ifPresent(agentDao -> {
        try {
          agentDao.createInstInfo();
          log.info("应用实例表创建成功");
        } catch (Exception e) {
          log.info("应用实例表已经存在");
        }
      });
      registry();
    } catch (Exception e) {
      log.error("初始化异常", e);
    }
  }

  @Override
  public void registry() {
    String dc = context.getProperty("hbq.agent.data-center", "dc");
    int port = context.getIntValue("server.port", 8080);
    HostInfo host = CmdUtils.getLocalAddress();
    this.instance = new InstInfo(registry.getApp(), dc, host.getIp(),
        host.getHostName(), port, CmdUtils.getProcessNo());
    log.info("注册的应用实例信息: {}", this.instance);
    optional.getAgentDao().ifPresent(agentDao -> agentDao.saveInstInfo(this.instance.toMybatisMap()));
  }

  @Override
  public void destroy() throws Exception {
    optional.getAgentDao().ifPresent(agentDao -> {
      try {
        agentDao.deleteInstInfo(instance.getDataCenter(), instance.getApp().getName(),
            this.instance.getIp(), instance.getPort());
        log.info("应用实例注销成功: {}", instance);
      } catch (Exception e) {
        log.error("应用实例注销失败", e);
      }
    });
  }

  @Override
  public InstInfo getInstance() {
    return this.instance;
  }
}
