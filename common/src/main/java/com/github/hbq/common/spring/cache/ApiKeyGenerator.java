package com.github.hbq.common.spring.cache;

import com.github.hbq.common.utils.StrUtils;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;

/**
 * @author hbq
 */
public class ApiKeyGenerator extends SimpleKeyGenerator {

  @Override
  public Object generate(Object target, Method method, Object... params) {
    return createKey(true, target, method, params);
  }

  public static Object createKey(boolean hasMethod, Object target, Method method, Object[] params) {
    Expiry expire = AnnotationUtils.findAnnotation(method, Expiry.class);
    StringBuilder sb = new StringBuilder(200);
    sb.append(target.getClass().getName());
    if (hasMethod) {
      sb.append(method.getName());
    } else if (expire != null) {
      if (StrUtils.strNotEmpty(expire.methodKey())) {
        sb.append('.').append(expire.methodKey());
      }
    }
    sb.append("[");
    Object param;
    for (int i = 0; i < params.length; i++) {
      if (i > 0) {
        sb.append(",");
      }
      param = params[i];
      if (param == null) {
      } else if (ClassUtils.isPrimitiveArray(param.getClass())
          || ClassUtils.isPrimitiveWrapperArray(param.getClass())) {
        int len = Array.getLength(param);
        sb.append("[");
        for (int i1 = 0; i1 < len; i1++) {
          if (i1 > 0) {
            sb.append(",");
          }
          sb.append(Array.get(param, i1));
        }
        sb.append("]");
      } else if (param.getClass().isArray()) {
        Object[] array = (Object[]) param;
        for (int i1 = 0; i1 < array.length; i1++) {
          if (i1 > 0) {
            sb.append(",");
          }
          sb.append(array[i1]);
        }
      } else {
        sb.append(param);
      }
    }
    sb.append("]");
    String key = sb.toString();
    TimeUnit unit = TimeUnit.SECONDS;
    long time = -1;
    if (expire != null) {
      unit = expire.unit();
      time = expire.time();
    }
    return new ExpiryKey(key, unit, time);
  }
}
