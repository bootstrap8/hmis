package com.github.hbq.manage.route.web;

import com.alibaba.fastjson.JSON;
import com.github.hbq.common.spring.boot.ctrl.Result;
import com.github.hbq.common.spring.boot.ctrl.Version;
import com.github.hbq.common.utils.ResourceUtils;
import com.github.hbq.manage.route.pojo.RouteConfig;
import com.github.hbq.manage.route.pojo.RouteInfo;
import com.github.hbq.manage.route.pojo.TemplateInfo;
import com.github.hbq.manage.route.serv.RouteService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
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
@RequestMapping(path = "/route")
@Api(description = "路由管理")
@Slf4j
public class RouteCtrl {

  @Autowired
  private RouteService service;

  @ApiOperation("获取路由选项列表")
  @Version("v1.0")
  @RequestMapping(path = "/queryRouteOptions/{v}", method = RequestMethod.GET)
  @ResponseBody
  public Result<?> queryRouteOptions(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v) {
    log.info("获取路由选项列表");
    try (InputStream in = ResourceUtils.read("/", "route-options.json", null)) {
      String str = IOUtils.toString(in, "utf-8");
      List<Map> options = JSON.parseArray(str, Map.class);
      return Result.suc(options);
    } catch (Exception e) {
      log.error("获取路由选项列表异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("获取路由选项列表异常");
    }
  }

  @ApiOperation("分页查询路由信息")
  @Version("v1.0")
  @RequestMapping(path = "/queryAllRouteConfig/{v}", method = RequestMethod.GET)
  @ResponseBody
  public Result<PageInfo> queryAllRouteConfig(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
      @RequestParam(name = "routeSelect", defaultValue = "route_id") String routeSelect,
      @RequestParam(name = "routeKey", required = false) String routeKey) {
    log.info("分页查询路由信息, 分页:({},{}), routeSelect:{}, routeKey:{}",
        pageNum, pageSize, routeSelect, routeKey);
    try {
      Page page = PageHelper.startPage(pageNum, pageSize);
      List<RouteConfig> routes = service.queryAllRouteConfig(pageNum, pageSize, routeSelect, routeKey)
          .stream().map(r -> r.config()).collect(Collectors.toList());
      PageInfo<RouteConfig> pageInfo = new PageInfo<>(routes);
      pageInfo.setTotal(page.getTotal());
      log.info("{}", pageInfo);
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

  @ApiOperation("刷新路由")
  @Version("v1.0")
  @RequestMapping(path = "/refresh/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<?> refresh(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v) {
    log.info("刷新路由");
    try {
      service.refreshRouteConfig();
      return Result.suc("刷新成功");
    } catch (Exception e) {
      log.error("刷新路由异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("刷新路由异常");
    }
  }

  @ApiOperation("查询路由模板列表")
  @Version("v1.0")
  @RequestMapping(path = "/queryRouteTemplates/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<List<TemplateInfo>> queryRouteTemplates(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v) {
    log.info("查询路由模板列表");
    try {
      return Result.suc(service.queryRouteTemplateInfos());
    } catch (Exception e) {
      log.error("查询路由模板列表异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("查询路由模板列表异常");
    }
  }
}
