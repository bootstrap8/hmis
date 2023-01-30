package com.github.hbq.monitor.web;

import com.github.hbq.common.spring.boot.ctrl.Result;
import com.github.hbq.common.spring.boot.ctrl.Version;
import com.github.hbq.monitor.serv.MonitorService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.List;
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
@RestController("monitor-web-QuotaDataCtrl")
@Slf4j
@RequestMapping(path = "/qd")
public class QuotaDataCtrl {

  @Autowired
  private MonitorService monitorService;

  @ApiOperation("查询指标数据")
  @Version("v1.0")
  @RequestMapping(path = "/queryQuotaDatas/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<List<Map>> queryQuotaDatas(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestBody Map map) {
    log.info("查询指标数据: {}", map);
    try {
      return Result.suc(monitorService.queryQuotaDatas(map));
    } catch (Exception e) {
      log.error("查询指标数据异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("查询指标数据异常");
    }
  }
}
