package com.github.hbq.manage.monitor.web;

import com.github.hbq.agent.app.serv.AgentService;
import com.github.hbq.common.spring.boot.ctrl.Result;
import com.github.hbq.common.spring.boot.ctrl.Version;
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
@RestController("manage-web-MonitorCtrl")
@RequestMapping(path = "/monitor")
@Slf4j
public class MonitorCtrl {

  @Autowired(required = false)
  private AgentService agentService;

  @ApiOperation("查询注册指标数据")
  @Version("v1.0")
  @RequestMapping(path = "/queryQuotaInfos/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<?> queryQuotaInfos(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestParam(name = "pageNum", defaultValue = "1") int pageNum,
      @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
      @RequestBody Map map) {
    log.info("查询注册指标数据: ({},{}), {}", pageNum, pageSize, map);
    try {
      Page page = PageHelper.startPage(pageNum, pageSize);
      PageInfo<Map> pageInfo = new PageInfo<>(agentService.queryQuotaInfos(map, pageNum, pageSize));
      pageInfo.setTotal(page.getTotal());
      return Result.suc(pageInfo);
    } catch (Exception e) {
      log.error("查询注册指标数据异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("查询注册指标数据异常");
    }
  }
}
