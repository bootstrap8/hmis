package com.github.hbq.manage.agent.serv.impl;

import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;

/**
 * @author hbq
 */
@Slf4j
public class HttpCallback implements Callback {

  private String serviceName;
  private String ip;
  private int port;

  public HttpCallback(String serviceName, String ip, int port) {
    this.serviceName = serviceName;
    this.ip = ip;
    this.port = port;
  }

  @Override
  public void onFailure(Call call, IOException e) {
    log.error("请求应用: {}, 实例: ({}:{}), 异常: {}", serviceName, ip, port, e.getMessage());
  }

  @Override
  public void onResponse(Call call, okhttp3.Response response) throws IOException {
    log.info("刷新应用: {}, 实例: ({}:{}), 配置成功", serviceName, ip, port);
  }
}
