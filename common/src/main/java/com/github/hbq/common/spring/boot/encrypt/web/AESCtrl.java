package com.github.hbq.common.spring.boot.encrypt.web;

import com.github.hbq.common.spring.boot.ctrl.Result;
import com.github.hbq.common.spring.boot.ctrl.Version;
import com.github.hbq.common.spring.boot.encrypt.model.AESInfo;
import com.github.hbq.common.spring.boot.encrypt.utils.AESUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.nio.charset.Charset;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
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
@ConditionalOnExpression("${hbq.common.restful.encrypt.aes.enable:false}")
@Api(description = "AES对称加密工具")
@RestController("hbq-common-restful-AESCtrl")
@RequestMapping(path = "/common/encrypt/aes")
@Slf4j
public class AESCtrl {

  @ApiOperation("获取AES随机码")
  @Version("v1.0")
  @RequestMapping(path = "/getRandomKey/{v}", method = RequestMethod.GET)
  @ResponseBody
  public Result<String> getRandomKey(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestParam(name = "len", defaultValue = "16") Integer len) {
    log.info("获取AES随机码");
    try {
      return Result.suc(AESUtil.randomKeyWithAES(len));
    } catch (Exception e) {
      log.error("获取AES随机码异常", e);
      return Result.fail("获取AES随机码异常");
    }
  }

  @ApiOperation("使用aes加密")
  @Version("v1.0")
  @RequestMapping(path = "/encrypt/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<String> encrypt(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestBody AESInfo info) {
    log.info("使用aes加密: {}", info);
    try {
      return Result.suc(AESUtil.encrypt(info.getContent(), info.getKey(), Charset.forName(info.getCharset())));
    } catch (Exception e) {
      log.error("使用aes加密异常", e);
      return Result.fail("使用aes加密异常");
    }
  }

  @ApiOperation("使用aes解密")
  @Version("v1.0")
  @RequestMapping(path = "/decrypt/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<String> decrypt(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestBody AESInfo info) {
    log.info("使用aes解密: {}", info);
    try {
      return Result.suc(AESUtil.decrypt(info.getContent(), info.getKey(), Charset.forName(info.getCharset())));
    } catch (Exception e) {
      log.error("使用aes解密异常", e);
      return Result.fail("使用aes解密异常");
    }
  }
}
