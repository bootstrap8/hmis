package com.github.hbq.agent.config;

import com.github.hbq.agent.app.dao.impl.AgentDaoOptional;
import com.github.hbq.agent.app.service.AppInfoRegistry;
import com.github.hbq.agent.app.service.InstanceRegistry;
import com.github.hbq.agent.app.service.QuotaManage;
import com.github.hbq.agent.app.service.QuotaSchedule;
import com.github.hbq.agent.app.service.impl.AppInfoRegistryImpl;
import com.github.hbq.agent.app.service.impl.DefaultQuotaDataGet;
import com.github.hbq.agent.app.service.impl.InstanceRegistryImpl;
import com.github.hbq.agent.app.service.impl.QuotaManageImpl;
import com.github.hbq.agent.app.service.impl.QuotaScheduleImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author hbq
 */
@Configuration
@Slf4j
@ComponentScan(basePackageClasses = {})
public class AgentConfiguration {

  @ConditionalOnProperty(prefix = "hbq.agent", name = "enable", havingValue = "true")
  @Bean("agent-dao-AgentDaoOptional")
  AgentDaoOptional agentDaoOptional() {
    return new AgentDaoOptional();
  }

  @ConditionalOnProperty(prefix = "hbq.agent", name = "enable", havingValue = "true")
  @Bean("agent-service-AppInfoRegistryImpl")
  AppInfoRegistry appInfoRegistry() {
    return new AppInfoRegistryImpl();
  }

  @ConditionalOnProperty(prefix = "hbq.agent", name = "enable", havingValue = "true")
  @Bean("agent-service-InstanceRegistryImpl")
  InstanceRegistry instanceRegistry() {
    return new InstanceRegistryImpl();
  }

  @ConditionalOnProperty(prefix = "hbq.agent", name = "enable", havingValue = "true")
  @Bean("agent-service-QuotaScheduleImpl")
  QuotaSchedule quotaSchedule() {
    return new QuotaScheduleImpl();
  }

  @ConditionalOnProperty(prefix = "hbq.agent", name = "enable", havingValue = "true")
  @Bean("agent-service-QuotaManageImpl")
  QuotaManage quotaManage() {
    return new QuotaManageImpl();
  }

  @ConditionalOnExpression("${hbq.agent.enable:false} && ${hbq.agent.jvm.enable:false}")
  @Bean("agent-service-DefaultQuotaDataGet")
  DefaultQuotaDataGet defaultQuotaDataGet() {
    return new DefaultQuotaDataGet();
  }

}
