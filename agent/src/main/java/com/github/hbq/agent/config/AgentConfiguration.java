package com.github.hbq.agent.config;

import com.github.hbq.agent.app.dao.impl.AgentDaoOptional;
import com.github.hbq.agent.app.serv.AppInfoRegistry;
import com.github.hbq.agent.app.serv.InstanceRegistry;
import com.github.hbq.agent.app.serv.QuotaManage;
import com.github.hbq.agent.app.serv.QuotaSchedule;
import com.github.hbq.agent.app.serv.impl.AgentServiceImpl;
import com.github.hbq.agent.app.serv.impl.AppInfoRegistryImpl;
import com.github.hbq.agent.app.serv.impl.DefaultQuotaDataGet;
import com.github.hbq.agent.app.serv.impl.InstanceRegistryImpl;
import com.github.hbq.agent.app.serv.impl.KafkaQuotaGetImpl;
import com.github.hbq.agent.app.serv.impl.QuotaManageImpl;
import com.github.hbq.agent.app.serv.impl.QuotaScheduleImpl;
import com.github.hbq.agent.app.serv.impl.kafka.KafkaInAspect;
import com.github.hbq.agent.app.serv.impl.kafka.KafkaInRateLimit;
import com.github.hbq.agent.app.serv.impl.kafka.KafkaInRateLimiterObserveImpl;
import com.github.hbq.agent.app.serv.impl.kafka.KafkaOutAspect;
import com.github.hbq.agent.app.serv.impl.kafka.QuotaKafkaTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author hbq
 */
@RefreshScope
@Configuration
@Slf4j
@ComponentScan(basePackageClasses = {})
public class AgentConfiguration {

  @ConditionalOnProperty(prefix = "hbq.agent", name = "enable", havingValue = "true")
  @Bean("agent-service-AgentServiceImpl")
  AgentServiceImpl agentService() {
    return new AgentServiceImpl();
  }

  @ConditionalOnProperty(prefix = "hbq.agent", name = "enable", havingValue = "true")
  @Bean("agent-service-QuotaKafkaTemplate")
  QuotaKafkaTemplate quotaKafkaTemplate() {
    return new QuotaKafkaTemplate();
  }

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

  @ConditionalOnExpression("${hbq.agent.enable:false} && ${hbq.agent.kafka.enable:false} && ${hbq.agent.kafka.auto-collect.enable:true}")
  @Bean("agent-service-kafka-KafkaInAspect")
  KafkaInAspect kafkaInAspect() {
    return new KafkaInAspect();
  }

  @ConditionalOnExpression("${hbq.agent.enable:false} && ${hbq.agent.kafka.enable:false} && ${hbq.agent.kafka.auto-collect.enable:true}")
  @Bean("agent-service-kafka-KafkaOutAspect")
  KafkaOutAspect kafkaOutAspect() {
    return new KafkaOutAspect();
  }

  @ConditionalOnExpression("${hbq.agent.enable:false} && ${hbq.agent.kafka.enable:false}")
  @Bean("agent-service-KafkaQuotaGetImpl")
  KafkaQuotaGetImpl kafkaQuotaGet() {
    return new KafkaQuotaGetImpl();
  }

  @ConditionalOnExpression("${hbq.agent.enable:false} && ${hbq.agent.kafka.enable:false}")
  @Bean("agent-service-KafkaInRateLimit")
  KafkaInRateLimit kafkaInRateLimit() {
    return new KafkaInRateLimit();
  }

  @ConditionalOnExpression("${hbq.common.event.enable:false} && ${hbq.agent.enable:false} && ${hbq.agent.kafka.enable:false}")
  @Bean("agent-service-KafkaInRateLimiterObserveImpl")
  KafkaInRateLimiterObserveImpl kafkaInRateLimiterObserve() {
    return new KafkaInRateLimiterObserveImpl();
  }

}
