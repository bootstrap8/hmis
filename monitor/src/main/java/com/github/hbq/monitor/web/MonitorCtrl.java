package com.github.hbq.monitor.web;

import com.github.hbq.common.spring.boot.ctrl.Result;
import com.github.hbq.common.spring.boot.ctrl.Version;
import com.github.hbq.monitor.pojo.QuotaWarnRuleInfo;
import com.github.hbq.monitor.serv.MonitorService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
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
@RestController("monitor-web-MonitorCtrl")
@Slf4j
@RequestMapping(path = "/rule")
public class MonitorCtrl {

  @Autowired
  private MonitorService monitorService;

  @ApiOperation("查询指标通知规则")
  @Version("v1.0")
  @RequestMapping(path = "/queryQuotaWarnRules/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<PageInfo> queryQuotaWarnRules(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
      @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
      @RequestBody Map map) {
    log.info("查询指标通知规则: ({},{}), {}", pageNum, pageSize, map);
    try {
      Page page = PageHelper.startPage(pageNum, pageSize);
      PageInfo<QuotaWarnRuleInfo> pageInfo = new PageInfo<>(monitorService.queryQuotaWarnRules(map, pageNum, pageSize));
      pageInfo.setTotal(page.getTotal());
      return Result.suc(pageInfo);
    } catch (Exception e) {
      log.error("查询指标通知规则异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("查询指标通知规则异常");
    }
  }

  @ApiOperation("保存指标通知规则")
  @Version("v1.0")
  @RequestMapping(path = "/saveQuotaWarnRule/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<?> saveQuotaWarnRule(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestBody Map map) {
    log.info("保存指标通知规则: {}", map);
    try {
      monitorService.saveQuotaWarnRule(map);
      return Result.suc("保存成功");
    } catch (Exception e) {
      log.error("保存指标通知规则异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("保存指标通知规则异常");
    }
  }

  @ApiOperation("删除指标通知规则")
  @Version("v1.0")
  @RequestMapping(path = "/deleteQuotaWarnRule/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<?> deleteQuotaWarnRule(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestBody Map map) {
    log.info("删除指标通知规则: {}", map);
    try {
      String appName = MapUtils.getString(map, "appName");
      String quotaName = MapUtils.getString(map, "quotaName");
      monitorService.deleteQuotaWarnRule(appName, quotaName);
      return Result.suc("删除成功");
    } catch (Exception e) {
      log.error("删除指标通知规则异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("删除指标通知规则异常");
    }
  }

  @ApiOperation("批量删除指标通知规则")
  @Version("v1.0")
  @RequestMapping(path = "/deleteQuotaWarnRules/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<?> deleteQuotaWarnRules(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestBody List<Map> list) {
    log.info("批量删除指标通知规则: {}", list);
    try {
      monitorService.deleteQuotaWarnRules(list);
      return Result.suc("删除成功");
    } catch (Exception e) {
      log.error("批量删除指标通知规则异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("批量删除指标通知规则异常");
    }
  }

  @ApiOperation("更新应用指标通知规则的通知号码")
  @Version("v1.0")
  @RequestMapping(path = "/updatePhoneNumsOnApp/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<?> updatePhoneNumsOnApp(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestBody Map map) {
    log.info("更新应用指标通知规则的通知号码: {}", map);
    try {
      String appName = MapUtils.getString(map, "appName");
      String phoneNums = MapUtils.getString(map, "phoneNums");
      monitorService.updatePhoneNumsOnApp(appName, phoneNums);
      return Result.suc("更新成功");
    } catch (Exception e) {
      log.error("更新应用指标通知规则的通知号码异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("更新应用指标通知规则的通知号码异常");
    }
  }

  @ApiOperation("更新所有通知规则的通知号码")
  @Version("v1.0")
  @RequestMapping(path = "/updatePhoneNumsOnAll/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<?> updatePhoneNumsOnAll(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestBody Map map) {
    log.info("更新所有通知规则的通知号码: {}", map);
    try {
      monitorService.updatePhoneNumsOnAll(MapUtils.getString(map, "phoneNums"));
      return Result.suc("更新成功");
    } catch (Exception e) {
      log.error("更新所有通知规则的通知号码异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("更新所有通知规则的通知号码异常");
    }
  }
}
