package com.github.hbq.common.schedule.impl;

import com.github.hbq.common.schedule.RoundTimeHelper;
import com.github.hbq.common.schedule.ScheduleHelper;
import com.github.hbq.common.schedule.annotation.Schedule;
import com.github.hbq.common.schedule.annotation.Schedules;
import com.github.hbq.common.utils.StrUtils;
import it.sauronsoftware.cron4j.Scheduler;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

/**
 * @author hbq
 */
@Slf4j
public class ScheduleProcessor implements BeanPostProcessor, DisposableBean {

  @Autowired
  private Environment environment;

  private RoundTimeHelper roundTimeHelper = new RoundTimeHelperImpl();

  private Scheduler scheduler;

  private final Set<Class<?>> nonAnnotatedClasses = Collections
      .newSetFromMap(new ConcurrentHashMap<>(64));

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    if (bean instanceof ScheduleHelper) {
      Class<?> targetClass = AopProxyUtils.ultimateTargetClass(bean);
      if (!this.nonAnnotatedClasses.contains(targetClass)) {
        Map<Method, Set<Schedule>> annotatedMethods = MethodIntrospector.selectMethods(targetClass,
            (MethodIntrospector.MetadataLookup<Set<Schedule>>) method -> {
              Set<Schedule> scheduledMethods = AnnotatedElementUtils.getMergedRepeatableAnnotations(
                  method, Schedule.class, Schedules.class);
              return (!scheduledMethods.isEmpty() ? scheduledMethods : null);
            });
        if (annotatedMethods.isEmpty()) {
          this.nonAnnotatedClasses.add(targetClass);
        } else {
          scheduler = new Scheduler();
          annotatedMethods.forEach((method, scheduledMethods) ->
              scheduledMethods.forEach(scheduled -> processScheduled(scheduled, method, bean)));
          scheduler.start();
        }
      }
    }
    return bean;
  }

  @Override
  public void destroy() throws Exception {
    if (scheduler != null) {
      scheduler.stop();
    }
  }

  private void processScheduled(Schedule scheduled, Method method, Object bean) {
    boolean enable = parseEnable(scheduled);
    if (enable) {
      Assert.isTrue(method.getParameterCount() == 1,
          "Only no-arg methods may be annotated with @Schedule");
      Method invocableMethod = AopUtils.selectInvocableMethod(method, bean.getClass());
      Runnable runnable = new RunnableScheduler(bean, invocableMethod, roundTimeHelper,
          scheduled);
      String cronVariable = parseScheduleCron(scheduled, environment);
      scheduler.schedule(cronVariable, runnable);
    }
  }

  public static String parseScheduleCron(Schedule scheduled, Environment env) {
    String cronVariable = scheduled.cron();
    if (StrUtils.isVariable(cronVariable)) {
      cronVariable = env.getProperty(StrUtils.getVariableKey(cronVariable));
    }
    return cronVariable;
  }

  private boolean parseEnable(Schedule scheduled) {
    String variable = scheduled.enable();
    boolean enable;
    if (StrUtils.isVariable(variable)) {
      enable = Boolean.valueOf(environment.getProperty(StrUtils.getVariableKey(variable)));
    } else {
      enable = Boolean.valueOf(variable);
    }
    return enable;
  }
}
