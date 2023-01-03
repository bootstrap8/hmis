package com.github.hbq.common.ids;

import com.github.hbq.common.spring.boot.ctrl.Result;
import com.github.hbq.common.spring.boot.ctrl.Version;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hbq
 */
@RequestMapping(path = "/common/id/snowflake")
@RestController
@Api(description = "分布式id")
@Slf4j
public class SnowflakeCtrl {

  @Autowired
  private Snowflake snowflake;

  @ApiOperation("获取分布式id")
  @Version("v1.0")
  @RequestMapping(path = "/nextId/{v}", method = RequestMethod.GET)
  @ResponseBody
  public Result<?> nextId(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v) {
    try {
      return Result.suc(snowflake.nextId());
    } catch (Exception e) {
      log.error("获取分布式id异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("获取分布式id异常");
    }
  }
}
