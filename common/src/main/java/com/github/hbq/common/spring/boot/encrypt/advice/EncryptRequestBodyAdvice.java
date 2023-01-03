package com.github.hbq.common.spring.boot.encrypt.advice;

import com.github.hbq.common.spring.boot.encrypt.config.AESConfig;
import com.github.hbq.common.spring.boot.encrypt.config.Decrypt;
import com.github.hbq.common.spring.boot.encrypt.config.RSAConfig;
import com.github.hbq.common.spring.boot.encrypt.exception.EncryptRequestException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

/**
 * @author hbq
 **/
@ControllerAdvice({"com"})
@Slf4j
public class EncryptRequestBodyAdvice implements RequestBodyAdvice {

  @Autowired
  private RSAConfig rsaConfig;

  @Autowired
  private AESConfig aesConfig;


  @Override
  public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
    Method method = methodParameter.getMethod();
    boolean hasDecrypt = method.isAnnotationPresent(Decrypt.class);
    Decrypt decrypt = method.getAnnotation(Decrypt.class);
    return (rsaConfig.isEnable() || aesConfig.isEnable()) && hasDecrypt && decrypt.required();
  }

  @Override
  public Object handleEmptyBody(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    return body;
  }

  @Override
  public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    try {
      return new DecryptHttpInputMessage(inputMessage, rsaConfig, aesConfig, parameter.getMethod().getAnnotation(Decrypt.class));
    } catch (EncryptRequestException e) {
      throw e;
    } catch (Exception e) {
      log.error("Decryption failed", e);
    }
    return inputMessage;
  }

  @Override
  public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
      Class<? extends HttpMessageConverter<?>> converterType) {
    return body;
  }
}
