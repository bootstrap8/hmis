package com.github.hbq.manage.agent.web;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSON;
import com.github.hbq.agent.app.serv.AgentService;
import com.github.hbq.common.spring.boot.ctrl.Result;
import com.github.hbq.common.spring.boot.ctrl.Version;
import com.github.hbq.common.utils.FormatTime;
import com.github.hbq.manage.agent.serv.KafkaInService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hbq
 */
@RestController
@RequestMapping(path = "/agent")
@Slf4j
public class KafkaInCtrl {

  @Autowired
  private KafkaInService kafkaInService;

  @Autowired(required = false)
  private AgentService agentService;

  @ApiOperation("查询应用列表")
  @Version("v1.0")
  @RequestMapping(path = "/kafkaIn/queryAppList/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<PageInfo> queryAppList(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
      @RequestBody Map map) {
    log.info("查询应用列表: {}", map);
    try {
      Page page = PageHelper.startPage(pageNum, pageSize);
      PageInfo<Map> pageInfo = new PageInfo<>(kafkaInService.queryAppInfos(map, pageNum, pageSize));
      pageInfo.setTotal(page.getTotal());
      return Result.suc(pageInfo);
    } catch (Exception e) {
      log.error("查询应用列表异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("查询应用列表异常");
    }
  }

  @ApiOperation("保存应用kafka入口消息速率")
  @Version("v1.0")
  @RequestMapping(path = "/kafkaIn/saveAppPermits/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<?> saveAppPermits(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestBody Map map) {
    log.info("保存应用kafka入口消息速率: {}", map);
    try {
      Assert.notNull(agentService, "未启用指标采集[hbq.agent.enable=true]");
      map.put("update_time", FormatTime.nowSecs());
      log.info("保存应用kafka入口消息速率: {}", map);
      agentService.saveKafkaInAppRateLimiter(map);
      return Result.suc("保存成功");
    } catch (Exception e) {
      log.error("保存应用kafka入口消息速率异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("保存应用kafka入口消息速率异常");
    }
  }

  @ApiOperation("查询实例列表")
  @Version("v1.0")
  @RequestMapping(path = "/kafkaIn/queryInstanceList/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<PageInfo> queryInstanceList(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
      @RequestBody Map map) {
    log.info("查询实例列表: ({},{}),{}", pageNum, pageSize, map);
    try {
      Assert.notNull(agentService, "未启用指标采集[hbq.agent.enable=true]");
      Page page = PageHelper.startPage(pageNum, pageSize);
      PageInfo<Map> pageInfo = new PageInfo<>(kafkaInService.queryInstanceList(map, pageNum, pageSize));
      pageInfo.setTotal(page.getTotal());
      return Result.suc(pageInfo);
    } catch (Exception e) {
      log.error("查询实例列表异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("查询实例列表异常");
    }
  }

  @ApiOperation("保存应用实例kafka入口消息速率")
  @Version("v1.0")
  @RequestMapping(path = "/kafkaIn/saveInstancePermits/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<?> saveInstancePermits(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestBody Map map) {
    log.info("保存应用实例kafka入口消息速率: {}", map);
    try {
      Assert.notNull(agentService, "未启用指标采集[hbq.agent.enable=true]");
      map.put("update_time", FormatTime.nowSecs());
      agentService.saveKafkaInInstanceRateLimiter(map);
      return Result.suc("保存成功");
    } catch (Exception e) {
      log.error("保存应用实例kafka入口消息速率异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("保存应用实例kafka入口消息速率异常");
    }
  }
}
