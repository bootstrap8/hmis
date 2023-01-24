package com.github.hbq.agent.app.service.impl;

import com.github.hbq.agent.app.pojo.CycleInfo;
import com.github.hbq.agent.app.service.QuotaDataGet;
import com.github.hbq.agent.app.service.QuotaSchedule;
import com.github.hbq.common.spring.context.SpringContext;
import com.github.hbq.common.utils.ThreadPoolUtils;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author hbq
 */
@Slf4j
public class QuotaScheduleImpl implements QuotaSchedule, InitializingBean, DisposableBean {

  @Autowired
  private SpringContext context;

  private DelayQueue<QuotaDelay> q = new DelayQueue<>();

  private Task task = new Task();

  private ThreadPoolExecutor pool = ThreadPoolUtils.pool("指标采集任务线程池",
      Runtime.getRuntime().availableProcessors() * 4, 5000);

  @Override
  public void afterPropertiesSet() throws Exception {
    task.setName("指标采集延时任务调度");
    task.start();
  }

  @Override
  public void destroy() throws Exception {
    if (task != null) {
      task.interrupt();
    }
  }

  @Override
  public boolean submit(QuotaDataGet g) {
    CycleInfo c = g.cycle();
    long delayMills = TimeUnit.MILLISECONDS.convert(c.getTime(), c.getUnit());
    QuotaDelay d = new QuotaDelay(delayMills, context, g);
    return q.offer(d);
  }

  class Task extends Thread {

    @Override
    public void run() {
      while (true) {
        QuotaDelay delay = q.poll();
        if (delay == null) {
          try {
            TimeUnit.MILLISECONDS.sleep(50);
          } catch (InterruptedException e) {
            log.info("线程被正常中断");
          }
          continue;
        }
        pool.submit(() -> {
          log.info("调度采集器: {}", delay.identify());
          delay.collect();
          delay.reset();
          q.offer(delay);
        });
      }
    }
  }
}
