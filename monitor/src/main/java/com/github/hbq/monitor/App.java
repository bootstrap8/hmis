package com.github.hbq.monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication(exclude = {})
@EnableCaching
@EnableTransactionManagement(proxyTargetClass = true)
@EnableDiscoveryClient
@EnableSwagger2
@EnableWebMvc
@EnableScheduling
@EnableConfigurationProperties
public class App {

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }

}
