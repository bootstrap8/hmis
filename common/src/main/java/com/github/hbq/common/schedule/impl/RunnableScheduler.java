package com.github.hbq.common.schedule.impl;

import com.github.hbq.common.schedule.ScheduleHelper;
import com.github.hbq.common.schedule.RoundTimeHelper;
import com.github.hbq.common.schedule.annotation.Schedule;
import com.github.hbq.common.utils.RoundInfo;
import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hbq
 */
@Slf4j
public class RunnableScheduler implements Runnable {

  private final Object bean;

  private final Method method;

  private final RoundTimeHelper roundTimeHelper;

  private final Schedule schedule;

  public RunnableScheduler(Object bean, Method method, RoundTimeHelper roundTimeHelper,
      Schedule schedule) {
    this.bean = bean;
    this.method = method;
    this.roundTimeHelper = roundTimeHelper;
    this.schedule = schedule;
  }

  /**
   * Return the target instance to call the method on.
   */
  public Object getBean() {
    return this.bean;
  }

  /**
   * Return the target method to call.
   */
  public Method getMethod() {
    return this.method;
  }

  @Override
  public void run() {
    try {
      RoundInfo roundInfo = roundTimeHelper.createRoundInfo(schedule);
      ScheduleHelper target = ScheduleHelper.class.cast(bean);
      LogScheduleHelperImpl proxy = new LogScheduleHelperImpl(target, schedule);
      proxy.init(roundInfo);
      proxy.clean(roundInfo);
      proxy.execute(roundInfo);
      proxy.reform(roundInfo);
    } catch (Throwable ex) {
      log.error("", ex);
    }
  }

  @Override
  public String toString() {
    return this.method.getDeclaringClass().getName() + "." + this.method.getName();
  }

}
