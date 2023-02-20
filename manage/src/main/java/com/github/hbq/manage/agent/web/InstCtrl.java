package com.github.hbq.manage.agent.web;

import com.github.hbq.common.spring.boot.ctrl.Result;
import com.github.hbq.common.spring.boot.ctrl.Version;
import com.github.hbq.manage.agent.serv.DiscoveryAdapter;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hbq
 */
@RestController("agent-web-InstCtrl")
@Slf4j
@RequestMapping(path = "/agent/appInfo")
public class InstCtrl {

  @Autowired
  private DiscoveryAdapter discoveryAdapter;

  @ApiOperation("查询应用列表")
  @Version("v1.0")
  @RequestMapping(path = "/queryAppInfos/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<?> queryAppInfos(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestBody Map map) {
    log.info("查询应用列表: {}", map);
    try {
      return Result.suc(discoveryAdapter.queryAppInfos(map));
    } catch (Exception e) {
      log.error("查询应用列表异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("查询应用列表异常");
    }
  }

  @ApiOperation("刷新应用配置")
  @Version("v1.0")
  @RequestMapping(path = "/refreshConfig/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<?> refreshConfig(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestBody Map map) {
    log.info("刷新应用配置: {}", map);
    try {
      discoveryAdapter.refreshConfig(map);
      return Result.suc("刷新成功");
    } catch (Exception e) {
      log.error("刷新应用配置异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("刷新应用配置异常");
    }
  }
}
