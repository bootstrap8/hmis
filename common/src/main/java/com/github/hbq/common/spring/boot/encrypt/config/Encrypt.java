package com.github.hbq.common.spring.boot.encrypt.config;

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
public @interface Encrypt {

  /**
   * 加密算法
   *
   * @return
   */
  Algorithm algorithm() default Algorithm.AES;
}
