package com.github.hbq.agent.config;

import com.github.hbq.agent.app.service.AppInfoRegistry;
import com.github.hbq.agent.app.service.InstanceRegistry;
import com.github.hbq.agent.app.service.impl.AppInfoRegistryImpl;
import com.github.hbq.agent.app.service.impl.InstanceRegistryImpl;
import lombok.extern.slf4j.Slf4j;
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

  @Bean("agent-service-AppInfoRegistryImpl")
  AppInfoRegistry appInfoRegistry() {
    return new AppInfoRegistryImpl();
  }

  @Bean("agent-service-InstanceRegistryImpl")
  InstanceRegistry instanceRegistry() {
    return new InstanceRegistryImpl();
  }
}
