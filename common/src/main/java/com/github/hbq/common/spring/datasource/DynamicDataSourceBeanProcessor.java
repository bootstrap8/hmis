package com.github.hbq.common.spring.datasource;

import com.github.hbq.common.help.MDC;
import java.lang.reflect.Proxy;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * @author hbq
 */
public class DynamicDataSourceBeanProcessor implements BeanPostProcessor {

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object target, String beanName) throws BeansException {
    Class<?> targetClass = AopProxyUtils.ultimateTargetClass(target);
    HDS ds = AnnotationUtils.findAnnotation(targetClass, HDS.class);
    if (ds == null) {
      return target;
    }
    ClassLoader loader = targetClass.getClassLoader();
    Class<?>[] interfaces = targetClass.getInterfaces();
    return Proxy.newProxyInstance(loader, interfaces, (proxy, method, args) ->
    {
      String dsKey = ds.value();
      try {
        MDC.put("hds", dsKey);
        return method.invoke(target, args);
      } finally {
        MDC.get("hds");
      }
    });
  }
}
