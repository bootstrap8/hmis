package com.github.hbq.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.reactive.socket.server.WebSocketService;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy;

/**
 * @author hbq
 */
@Configuration
public class Config {

  @Value("${spring.cloud.gateway.httpclient.websocket.max-frame-payload-length}")
  private static Integer DEFAULT_FRAME_MAX_SIZE = 10 * 1024 * 1024;

  @Bean
  @Primary
  public WebSocketService customWebSocketService() {
    ReactorNettyRequestUpgradeStrategy requestUpgradeStrategy = new ReactorNettyRequestUpgradeStrategy();
    requestUpgradeStrategy.setMaxFramePayloadLength(DEFAULT_FRAME_MAX_SIZE);
    return new HandshakeWebSocketService(requestUpgradeStrategy);
  }
}
