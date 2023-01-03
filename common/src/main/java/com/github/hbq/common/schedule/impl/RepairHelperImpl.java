package com.github.hbq.common.schedule.impl;

import com.github.hbq.common.schedule.RepairHelper;
import com.github.hbq.common.schedule.ScheduleHelper;
import com.github.hbq.common.schedule.annotation.Schedule;
import com.github.hbq.common.spring.context.SpringContext;
import com.github.hbq.common.utils.FormatTime;
import com.github.hbq.common.utils.RoundInfo;
import com.github.hbq.common.utils.TimeFragment;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

/**
 * @author hbq
 */
@Slf4j
public class RepairHelperImpl implements RepairHelper, BeanFactoryAware {

  public static final String PREFIX_SCHEDULE_REPAIR = "hbq.schedule.repair.";
  public static final String SUFFIX_ENABLE = ".enable";
  public static final String SUFFIX_START_TIME = ".startTime";
  public static final String SUFFIX_END_TIME = ".endTime";

  @Autowired
  private SpringContext springContext;

  private BeanFactory beanFactory;

  @Override
  public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
    this.beanFactory = beanFactory;
  }

  @PostConstruct
  @Override
  public void repair() {
    Set<String> keys = springContext.getEnvironmentPropertyKeys();
    for (String key : keys) {
      if (key.startsWith(PREFIX_SCHEDULE_REPAIR) && key.endsWith(SUFFIX_ENABLE)) {
        String scheduleClass = key.substring(PREFIX_SCHEDULE_REPAIR.length(), key.length() - SUFFIX_ENABLE.length());
        boolean enable = springContext.getBoolValue(key, false);
        if (!enable) {
          continue;
        }
        String startTime = springContext.getProperty(String.join("", PREFIX_SCHEDULE_REPAIR, scheduleClass, SUFFIX_START_TIME));
        String endTime = springContext.getProperty(String.join("", PREFIX_SCHEDULE_REPAIR, scheduleClass, SUFFIX_END_TIME));
        Class<?> clz;
        Object obj;
        try {
          clz = Class.forName(scheduleClass);
          obj = beanFactory.getBean(clz);
        } catch (ClassNotFoundException | BeansException e) {
          log.warn("未找到统计调度类: {}, {}", scheduleClass, e.getClass().getName());
          continue;
        }
        if (!(obj instanceof ScheduleHelper)) {
          log.warn("统计调度类 {} 未实现ReportHelper接口，无法进行补数据操作", scheduleClass);
          continue;
        }
        ScheduleHelper target = ScheduleHelper.class.cast(obj);
        Method m = ReflectionUtils.findMethod(clz, "execute", new Class<?>[]{RoundInfo.class});
        Schedule schedule = AnnotationUtils.findAnnotation(m, Schedule.class);
        ScheduleHelper proxy = new LogScheduleHelperImpl(target, schedule);
        String unit = schedule.time().name();
        log.info("统计调度类: {} 需要执行补数据逻辑, ({}, {}, {})", scheduleClass, startTime, endTime, unit);
        List<RoundInfo> roundInfos = TimeFragment.valueOf(unit).fragment(startTime, endTime, FormatTime.YYYYMMDDHHMISS);
        for (RoundInfo roundInfo : roundInfos) {
          try {
            proxy.init(roundInfo);
            proxy.clean(roundInfo);
            proxy.execute(roundInfo);
            proxy.reform(roundInfo);
          } catch (Throwable ex) {
            log.error("", ex);
          }
        }
      }
    }
  }
}
