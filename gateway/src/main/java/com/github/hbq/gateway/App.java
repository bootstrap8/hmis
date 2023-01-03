package com.github.hbq.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableCaching
@EnableTransactionManagement(proxyTargetClass = true)
@EnableDiscoveryClient
@EnableScheduling
public class App {

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }

}
