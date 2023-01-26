package com.github.hbq.common.utils;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hbq
 */
@Slf4j
public class ThreadPoolUtils {

  /**
   * 创建线程池模板
   *
   * @param poolName 线程池名称
   * @param pooSize  线程池大小
   * @param qc       队列最大深度
   * @return
   */
  public static ThreadPoolExecutor pool(String poolName, int pooSize, int qc, RejectedExecutionHandler reject) {
    return reject == null ?
        new ThreadPoolExecutor(pooSize, pooSize, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(qc),
            (r) -> new Thread(r, poolName), (r, executor) ->
        {
          log.error("线程池队列溢出了，丢弃该任务，请检查！当前队列深度: {}", executor.getQueue().size());
        }) :
        new ThreadPoolExecutor(pooSize, pooSize, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(qc),
            (r) -> new Thread(r, poolName), reject);
  }

  /**
   * 创建定时任务调度器模板
   *
   * @param poolName
   * @param pooSize
   * @return
   */
  public static ScheduledThreadPoolExecutor scheduler(String poolName, int pooSize) {
    return new ScheduledThreadPoolExecutor(pooSize, r -> new Thread(r, poolName));
  }
}
