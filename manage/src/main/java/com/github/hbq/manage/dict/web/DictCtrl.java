package com.github.hbq.manage.dict.web;

import com.github.hbq.common.dict.DictInfo;
import com.github.hbq.common.spring.boot.ctrl.Result;
import com.github.hbq.common.spring.boot.ctrl.Version;
import com.github.hbq.manage.dict.serv.DictService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@RestController("hbq-manage-dict-web-DictCtrl")
@RequestMapping(path = "/manage/dict")
@Api(description = "字典管理")
@Slf4j
public class DictCtrl {

  @Autowired
  private DictService service;

  @ApiOperation("分页查询字典信息")
  @Version("v1.0")
  @RequestMapping(path = "/queryAllDict/{v}", method = RequestMethod.GET)
  @ResponseBody
  public Result<?> queryAllDict(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
      @RequestParam(name = "word", required = false) String word) {
    log.info("分页查询字典信息, {}, ({},{})", word, pageNum, pageSize);
    try {
      PageHelper.startPage(pageNum, pageSize);
      return Result.suc(new PageInfo<>(service.queryAllDict(pageNum, pageSize, word)));
    } catch (Exception e) {
      log.error("分页查询字典信息异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("分页查询字典信息异常");
    }
  }

  @ApiOperation("查询字典普通枚举配置")
  @Version("v1.0")
  @RequestMapping(path = "/queryDictEnumExt/{v}", method = RequestMethod.GET)
  @ResponseBody
  public Result<DictInfo> queryDictEnumExt(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestParam(name = "fn") String fn) {
    log.info("查询字典普通枚举配置: {}", fn);
    try {
      return Result.suc(service.queryDictPairs(fn));
    } catch (Exception e) {
      log.error("查询字典普通枚举配置异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("查询字典普通枚举配置异常");
    }
  }

  @ApiOperation("查询字典sql枚举配置")
  @Version("v1.0")
  @RequestMapping(path = "/queryDictSqlExt/{v}", method = RequestMethod.GET)
  @ResponseBody
  public Result<DictInfo> queryDictSqlExt(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestParam(name = "fn") String fn) {
    log.info("查询字典sql枚举配置: {}", fn);
    try {
      return Result.suc(service.queryDict(fn));
    } catch (Exception e) {
      log.error("查询字典sql枚举配置异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("查询字典sql枚举配置异常");
    }
  }

  @ApiOperation("保存字典基本信息")
  @Version("v1.0")
  @RequestMapping(path = "/saveDictInfo/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<DictInfo> saveDictInfo(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestBody DictInfo dict) {
    log.info("保存字典基本信息: {}", dict);
    try {
      service.saveDict(dict);
      return Result.suc(dict);
    } catch (Exception e) {
      log.error("保存字典基本信息异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("保存字典基本信息异常");
    }
  }

  @ApiOperation("保存字典普通枚举配置")
  @Version("v1.0")
  @RequestMapping(path = "/saveDictEnumExt/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<DictInfo> saveDictEnumExt(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestBody DictInfo dict) {
    log.info("保存字典普通枚举配置: {}", dict);
    try {
      service.saveDictEnumExt(dict);
      return Result.suc(dict);
    } catch (Exception e) {
      log.error("保存字典普通枚举配置异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("保存字典普通枚举配置异常");
    }
  }

  @ApiOperation("保存字典sql枚举配置")
  @Version("v1.0")
  @RequestMapping(path = "/saveDictSqlExt/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<DictInfo> saveDictSqlExt(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestBody DictInfo dict) {
    log.info("保存字典sql枚举配置: {}", dict);
    try {
      service.saveDictSqlExt(dict);
      return Result.suc(dict);
    } catch (Exception e) {
      log.error("保存字典sql枚举配置异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("保存字典sql枚举配置异常");
    }
  }

  @ApiOperation("删除字典配置")
  @Version("v1.0")
  @RequestMapping(path = "/deleteAllDict/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<?> deleteAllDict(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestParam(name = "fn") String fn) {
    log.info("删除字典配置: {}", fn);
    try {
      service.deleteAllDictConfig(fn);
      return Result.suc(fn);
    } catch (Exception e) {
      log.error("删除字典配置异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("删除字典配置异常");
    }
  }
}
