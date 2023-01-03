package com.github.hbq.common.spring.boot.encrypt.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.hbq.common.spring.boot.encrypt.utils.Base64Util;
import com.google.common.collect.ImmutableMap;
import com.github.hbq.common.spring.boot.ctrl.Result;
import com.github.hbq.common.spring.boot.ctrl.Version;
import com.github.hbq.common.spring.boot.encrypt.model.RSADecryptInfo;
import com.github.hbq.common.spring.boot.encrypt.model.RSAEncryptInfo;
import com.github.hbq.common.spring.boot.encrypt.model.RSASignInfo;
import com.github.hbq.common.spring.boot.encrypt.utils.RSAUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
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
@ConditionalOnExpression("${hbq.common.restful.encrypt.rsa.enable:false}")
@RestController("hbq-common-restful-RSACtrl")
@RequestMapping(path = "/common/encrypt/rsa")
@Slf4j
@Api(description = "RSA非对称加密工具")
public class RSACtrl {

  @ApiOperation("获取密钥对")
  @Version("v1.0")
  @RequestMapping(path = "/genKeyPair/{v}", method = RequestMethod.GET)
  @ResponseBody
  public Result<Map> genKeyPair(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v) {
    log.info("获取密钥对");
    try {
      Map keyPair = RSAUtil.genKeyPair();
      RSAPublicKey publicKey = (RSAPublicKey) keyPair.get(RSAUtil.PUBLIC_KEY);
      RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.get(RSAUtil.PRIVATE_KEY);
      return Result.suc(ImmutableMap
          .of(RSAUtil.PUBLIC_KEY, Base64.encodeBase64String(publicKey.getEncoded()),
              RSAUtil.PRIVATE_KEY, Base64.encodeBase64String(privateKey.getEncoded())));
    } catch (Exception e) {
      log.error("获取密钥对异常", e);
      return Result.fail("获取密钥对异常");
    }
  }

  @ApiOperation("对加密的内容进行签名")
  @Version("v1.0")
  @RequestMapping(path = "/sign/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<String> sign(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestBody RSASignInfo info) {
    log.info("对加密的内容进行签名: {}", info);
    try {
      if (info.hasData()) {
        return Result.suc(RSAUtil.sign(info.getEncodeData().getBytes(), info.getBase64PrivateKey()));
      } else {
        return Result.fail("签名失败");
      }
    } catch (Exception e) {
      log.error("对加密的内容进行签名异常", e);
      return Result.fail("对加密的内容进行签名异常");
    }
  }

  @ApiOperation("使用公钥加密")
  @Version("v1.0")
  @RequestMapping(path = "/encryptWithPublicKey/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<String> encryptWithPublicKey(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestBody RSAEncryptInfo info) {
    log.info("使用公钥加密: {}", info);
    try {
      if (info.valid()) {
        ObjectMapper mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(info.getObj());
        byte[] data = content.getBytes(info.getCharset());
        data = RSAUtil.encrypt(data, info.getPublicKey());
        String result = Base64Util.encode(data);
        return Result.suc(result);
      } else {
        return Result.fail("加密失败");
      }
    } catch (Exception e) {
      log.error("使用公钥加密异常", e);
      return Result.fail("使用公钥加密异常");
    }
  }

  @ApiOperation("使用私钥解密")
  @Version("v1.0")
  @RequestMapping(path = "/decryptWithPrivateKey/{v}", method = RequestMethod.POST)
  @ResponseBody
  public Result<String> decryptWithPrivateKey(
      @RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v,
      @RequestBody RSADecryptInfo info) {
    log.info("使用私钥解密: {}", info);
    try {
      String content = info.getEncode();
      if (info.valid()) {
        StringBuilder result = new StringBuilder();
        content = content.replaceAll(" ", "+");
        if (!StringUtils.isEmpty(content)) {
          String[] contents = content.split("\\|");
          for (String value : contents) {
            value = new String(RSAUtil.decrypt(Base64Util.decode(value), info.getPrivateKey()), info.getCharset());
            result.append(value);
          }
        }
        return Result.suc(result.toString());
      } else {
        return Result.fail("解码失败");
      }
    } catch (Exception e) {
      log.error("使用私钥解密异常", e);
      return Result.fail("使用私钥解密异常");
    }
  }
}
