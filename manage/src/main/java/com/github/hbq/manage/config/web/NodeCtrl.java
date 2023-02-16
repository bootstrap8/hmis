package com.github.hbq.manage.config.web;

import com.github.hbq.common.spring.boot.ctrl.Result;
import com.github.hbq.common.spring.boot.ctrl.Version;
import com.github.hbq.manage.config.serv.NodeService;
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
@RestController("zkui-web-NodeCtrl")
@RequestMapping(path = "/config")
@Slf4j
public class NodeCtrl {

  @Autowired
  private NodeService nodeService;

  @ApiOperation("查询节点树信息")
  @Version("v1.0")
  @RequestMapping(path = "/queryNodes/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<?> queryNodes(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestBody Map map) {
    log.info("查询节点树信息: {}", map);
    try {
      return Result.suc(nodeService.queryNodes(map));
    } catch (Exception e) {
      log.error("查询节点树信息异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("查询节点树信息异常");
    }
  }
}
