package com.github.hbq.manage.agent.serv.impl;

import com.github.hbq.agent.app.dao.impl.AgentDaoOptional;
import com.github.hbq.agent.app.pojo.AppInfo;
import com.github.hbq.agent.app.pojo.InstInfo;
import com.github.hbq.common.spring.context.SpringContext;
import com.github.hbq.common.utils.FormatTime;
import com.github.hbq.manage.agent.serv.InstanceManage;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author hbq
 */
@Component("manage-agent-service-InstanceManageImpl")
@Slf4j
public class InstanceManageImpl implements InstanceManage {

  @Autowired(required = false)
  private AgentDaoOptional optional;

  @Autowired
  private SpringContext context;

  @Scheduled(cron = "${hbq.agent.invalid-instance-clean.cron:0 */5 * * * *}")
  @Override
  public void cleanInstance() {
    if (Objects.isNull(optional)) {
      return;
    }
    String expireInfo = context.getProperty("hbq.agent.invalid-instance-expire-time", "1,DAYS");
    String[] array = expireInfo.split(",");
    long timeSecs = TimeUnit.SECONDS.convert(Long.valueOf(array[0]), TimeUnit.valueOf(array[1]));
    long expireTime = FormatTime.nowSecs() - timeSecs;
    optional.getAgentDao().ifPresent(dao -> {
      List<Map> instances = dao.queryInvalidInstances(expireTime);
      log.info("查询到无效的实例: {} 个", instances.size());
      for (Map inst : instances) {
        String dc = MapUtils.getString(inst, "dataCenter");
        String ip = MapUtils.getString(inst, "ip");
        int port = MapUtils.getIntValue(inst, "port", 8080);
        AppInfo appInfo = new AppInfo(MapUtils.getString(inst, "appName"), "");
        InstInfo instInfo = new InstInfo(appInfo, dc, ip, "", port, "");
        try {
          dao.cleanInstance(instInfo);
          dao.cleanKafkaInInstance(instInfo);
          log.info("清理无效实例: {}", instInfo);
        } catch (Exception e) {
          log.warn(String.format("清理无效实例异常 %s", instInfo), e);
        }
      }
    });
  }
}
