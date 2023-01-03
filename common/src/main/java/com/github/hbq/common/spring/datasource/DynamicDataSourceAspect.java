package com.github.hbq.common.spring.datasource;

import com.github.hbq.common.help.MDC;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * @author hbq
 */
@Aspect
@Slf4j
public class DynamicDataSourceAspect {

  @Pointcut("@annotation(com.github.hbq.common.spring.datasource.HDS)")
  public void dataSourcePointCut() {

  }

  @Around("dataSourcePointCut()")
  public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
    HDS ds = getDSAnnotation(joinPoint);
    String dsKey = "dbcp2";
    if (Objects.nonNull(ds)) {
      dsKey = ds.value();
    }
    MDC.put("hds", dsKey);
    try {
      return joinPoint.proceed();
    } finally {
      MDC.get("hds");
    }
  }

  /**
   * 根据类或方法获取数据源注解
   */
  private HDS getDSAnnotation(ProceedingJoinPoint joinPoint) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    HDS ds = AnnotationUtils.findAnnotation(signature.getMethod(), HDS.class);
    return ds;
  }
}
