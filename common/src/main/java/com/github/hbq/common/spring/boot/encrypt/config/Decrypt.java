package com.github.hbq.common.spring.boot.encrypt.config;

import com.github.hbq.common.spring.boot.encrypt.exception.EncryptRequestException;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author hbq
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Decrypt {

  /**
   * 加密算法
   *
   * @return
   */
  Algorithm algorithm() default Algorithm.AES;

  /**
   * 请求参数一定要是加密内容
   */
  boolean required() default true;

  /**
   * 请求数据时间戳校验时间差 超过(当前时间-指定时间)的数据认定为伪造 注意应用程序需要捕获 {@link EncryptRequestException} 异常
   */
  long timeout() default 3000;
}
