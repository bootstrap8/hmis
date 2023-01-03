package com.github.hbq.common.spring.cache;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * @author hbq
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Expiry
{

    /**
     * 过期时间单位
     *
     * @return
     */
    TimeUnit unit() default TimeUnit.MILLISECONDS;

    /**
     * 过去时间值
     *
     * @return
     */
    long time() default 300000L;

    /**
     * 可以不指定，除非存在有相同参数的方法时
     *
     * @return
     */
    String methodKey() default "";
}
