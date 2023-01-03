package com.github.hbq.common.schedule.annotation;

import com.github.hbq.common.schedule.Time;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author hbq
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Schedule {

  /**
   * 是否启用
   *
   * @return
   */
  String enable() default "true";

  /**
   * 统计任务名称
   *
   * @return
   */
  String name();

  /**
   * crontab表达式, 格式参考linux的crontab
   *
   * @return
   */
  String cron();

  /**
   * 程序统计时间粒度
   *
   * @return
   */
  Time time() default Time.DAYS;

  /**
   * 时间粒度偏移量
   *
   * @return
   */
  int offset() default -1;

  /**
   * 时间是否整点对齐
   *
   * @return
   */
  boolean align() default true;
}
