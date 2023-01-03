package com.github.hbq.manage.dict.web;

import com.github.hbq.common.spring.boot.ctrl.Result;
import com.github.hbq.common.spring.boot.ctrl.Version;
import com.github.hbq.common.spring.cache.Expiry;
import com.github.hbq.common.utils.UidBox;
import com.google.common.collect.ImmutableMap;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping(path = "/manage/cache")
@Api(description = "缓存测试")
@Slf4j
public class CacheCtrl {

  @ApiOperation("测试缓存")
  @Version("v1.0")
  @RequestMapping(path = "/testCache/{v}", method = RequestMethod.GET)
  @ResponseBody
  @Cacheable(value = "default", keyGenerator = "apiKeyGenerator")
  @Expiry(unit = TimeUnit.SECONDS, time = 20L, methodKey = "putCache")
  public Result<?> testCache(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestParam(name = "name", defaultValue = "World") String name) {
    log.info("测试缓存调用开始: {}", name);
    try {
      TimeUnit.SECONDS.sleep(new Random().nextInt(10));
      log.info("测试缓存调用结束。");
      return Result.suc(ImmutableMap.of("message", "Hello " + name, "uid", UidBox.uid()));
    } catch (Exception e) {
      log.error("测试缓存异常", e);
      return (e instanceof RuntimeException) ?
          Result.fail(e.getMessage()) :
          Result.fail("测试缓存异常");
    }
  }

  @ApiOperation("清除缓存")
  @Version("v1.0")
  @RequestMapping(path = "/clearCache/{v}", method = RequestMethod.DELETE)
  @ResponseBody
  @CacheEvict(value = "default", keyGenerator = "apiKeyGenerator")
  @Expiry(methodKey = "putCache")
  public Result<?> clearCache(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestParam(name = "name", defaultValue = "World") String name) {
    log.debug("清除缓存: {}", name);
    return Result.suc("OK");
  }
}
