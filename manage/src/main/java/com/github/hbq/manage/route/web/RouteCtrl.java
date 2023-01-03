package com.github.hbq.manage.route.web;

import com.github.hbq.common.spring.boot.ctrl.Result;
import com.github.hbq.common.spring.boot.ctrl.Version;
import com.github.hbq.manage.route.pojo.RouteInfo;
import com.github.hbq.manage.route.serv.RouteService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.List;
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
@RestController("hbq-manage-web-RouteCtrl")
@RequestMapping(path = "/manage/route")
@Api(description = "路由管理")
@Slf4j
public class RouteCtrl {

  @Autowired
  private RouteService service;

  @ApiOperation("分页查询路由信息")
  @Version("v1.0")
  @RequestMapping(path = "/queryAllRouteConfig/{v}", method = RequestMethod.GET)
  @ResponseBody
  public Result<PageInfo> queryAllRouteConfig(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
    log.info("分页查询路由信息");
    try {
      PageHelper.startPage(pageNum, pageSize);
      List<RouteInfo> routes = service.queryAllRouteConfig(pageNum, pageSize);
      PageInfo<RouteInfo> pageInfo = new PageInfo<>(routes);
      log.debug("{}", routes);
      return Result.suc(pageInfo);
    } catch (Exception e) {
      log.error("分页查询路由信息异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("分页查询路由信息异常");
    }
  }

  @ApiOperation("根据id查询路由")
  @Version("v1.0")
  @RequestMapping(path = "/queryRouteConfig/{v}", method = RequestMethod.GET)
  @ResponseBody
  public Result<?> queryRouteConfig(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestParam(name = "id") String id) {
    log.info("根据id查询路由: {}", id);
    try {
      return Result.suc(service.queryRoute(id));
    } catch (Exception e) {
      log.error("根据id查询路由异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("根据id查询路由异常");
    }
  }

  @ApiOperation("保存路由")
  @Version("v1.0")
  @RequestMapping(path = "/saveRouteConfig/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<?> saveRouteConfig(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestBody RouteInfo route) {
    log.info("保存路由: {}", route);
    try {
      service.saveRouteConfig(route);
      return Result.suc("保存成功");
    } catch (Exception e) {
      log.error("保存路由异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("保存路由异常");
    }
  }

  @ApiOperation("删除路由")
  @Version("v1.0")
  @RequestMapping(path = "/deleteRouteConfig/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<?> deleteRouteConfig(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestParam(name = "id") String id) {
    log.info("删除路由: {}", id);
    try {
      service.deleteRouteConfig(id);
      return Result.suc(id);
    } catch (Exception e) {
      log.error("删除路由异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("删除路由异常");
    }
  }
}
