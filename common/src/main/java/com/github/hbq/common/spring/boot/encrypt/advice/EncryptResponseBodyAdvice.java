package com.github.hbq.common.spring.boot.encrypt.advice;

import com.github.hbq.common.spring.boot.encrypt.config.AESConfig;
import com.github.hbq.common.spring.boot.encrypt.config.Algorithm;
import com.github.hbq.common.spring.boot.encrypt.config.Encrypt;
import com.github.hbq.common.spring.boot.encrypt.config.RSAConfig;
import com.github.hbq.common.spring.boot.encrypt.utils.AESUtil;
import com.github.hbq.common.spring.boot.encrypt.utils.Base64Util;
import com.github.hbq.common.spring.boot.encrypt.utils.JsonUtils;
import com.github.hbq.common.spring.boot.encrypt.utils.RSAUtil;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author hbq
 **/
@ControllerAdvice({"com"})
@Slf4j
public class EncryptResponseBodyAdvice implements ResponseBodyAdvice<Object> {

  @Autowired
  private RSAConfig rsaConfig;

  @Autowired
  private AESConfig aesConfig;

  @Override
  public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
    Method method = returnType.getMethod();
    if (Objects.isNull(method)) {
      return false;
    }
    return (rsaConfig.isEnable() || aesConfig.isEnable()) && method.isAnnotationPresent(Encrypt.class);
  }

  @Override
  public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
      Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

    Encrypt encrypt = returnType.getMethodAnnotation(Encrypt.class);
    if (aesConfig.isEnable() && encrypt.algorithm() == Algorithm.AES) {
      String key = aesConfig.getKey();
      String charset = aesConfig.getCharset();
      try {
        String content = JsonUtils.writeValueAsString(body);
        if (StringUtils.isBlank(key)) {
          throw new IllegalArgumentException("aes加密的密钥不能为空");
        }
        if (StringUtils.isBlank(charset)) {
          charset = "utf-8";
        }
        String encryptBody = AESUtil.encrypt(content, key, Charset.forName(charset));
        if (aesConfig.isShowLog()) {
          log.info("请求响应, aes加密前: {}, 加密后: {}", content, encryptBody);
        }
        return encryptBody;
      } catch (Exception e) {
        log.error("加密数据异常", e);
      }
    }

    if (rsaConfig.isEnable() && encrypt.algorithm() == Algorithm.RSA) {
      String publicKey = rsaConfig.getPublicKey();
      String charset = rsaConfig.getCharset();
      try {
        if (StringUtils.isBlank(publicKey)) {
          throw new IllegalArgumentException("rsa加密的公钥不能为空");
        }
        if (StringUtils.isBlank(charset)) {
          charset = "utf-8";
        }
        String content = JsonUtils.writeValueAsString(body);
        byte[] data = content.getBytes(charset);
        byte[] encodedData = RSAUtil.encrypt(data, publicKey);
        String result = Base64Util.encode(encodedData);
        if (rsaConfig.isShowLog()) {
          log.info("请求响应, rsa加密前：{}，加密后：{}", content, result);
        }
        return result;
      } catch (Exception e) {
        log.error("加密数据异常", e);
      }
    }
    return body;
  }
}
