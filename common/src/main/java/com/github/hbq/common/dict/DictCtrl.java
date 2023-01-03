package com.github.hbq.common.dict;

import com.alibaba.fastjson.JSON;
import com.github.hbq.common.dict.map.MapDictImpl;
import com.github.hbq.common.spring.boot.ctrl.Result;
import com.github.hbq.common.spring.boot.ctrl.Version;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
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
@ConditionalOnExpression("${hbq.common.dict.enable:false}")
@RequestMapping(path = "/common/dict")
@RestController
@Api(description = "字典管理")
@Slf4j
public class DictCtrl {

  @Autowired
  private Select select;

  @Autowired
  private MapDictImpl mapDict;

  @ApiOperation("重载字典数据")
  @Version("v1.0")
  @RequestMapping(path = "/reloadImmediately/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<?> reloadDicImmediately(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v) {
    try {
      mapDict.reloadImmediately();
      return Result.suc("立即重载字典接口调用成功");
    } catch (Exception e) {
      log.error("立即重载字典接口异常", e);
      return Result.fail("立即重载字典接口异常");
    }
  }

  @ApiOperation("获取字典下拉列表数据")
  @Version("v1.0")
  @RequestMapping(path = "/querySelectList/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<List<Map<String, Object>>> querySelectList(@RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestBody SelectModel select) {
    try {
      log.info("查询下拉列表枚举: {}", JSON.toJSONString(select));
      select.check();
      List<Map<String, Object>> list = this.select
          .querySelectList(select.isMultiSelect(), select.getDicName(), select.getShowType(), select.getQueryOrSaveType(),
              select.getDefaultSelectValue());
      log.info("查询下拉列表枚举数量: {}, {}", select.getDicName(), list.size());
      return Result.suc(list);
    } catch (Exception e) {
      log.error("查询下拉列表枚举异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("查询下拉列表枚举异常");
    }
  }
}
