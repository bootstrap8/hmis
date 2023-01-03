package com.github.hbq.common.spring.cloud;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hbq
 */
@RestController("hbq-common-spring-cloud-CheckCtrl")
@Api(description = "健康检查")
@Slf4j
public class CheckCtrl {

  @Value("${spring.application.name}")
  private String name;

  @ApiOperation("心跳接口")
  @RequestMapping(path = "/check-me", method = RequestMethod.GET)
  @ResponseBody
  public String check() {
    log.info("注册中心 -> {}, 心跳。", name);
    return "^_^";
  }
}
