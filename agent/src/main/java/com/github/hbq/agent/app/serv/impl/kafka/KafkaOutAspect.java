package com.github.hbq.agent.app.serv.impl.kafka;

import com.github.hbq.agent.app.serv.impl.KafkaQuotaGetImpl;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * @author hbq
 */
@Aspect
public class KafkaOutAspect {

  @Autowired
  private KafkaQuotaGetImpl kafkaQuotaGet;

  @Pointcut("execution(* org.springframework.kafka.core.KafkaTemplate.send*(..))")
  public void sendPointCut() {
  }

  @Around("sendPointCut()")
  public Object around(ProceedingJoinPoint point) throws Throwable {
    kafkaQuotaGet.incrementAndGetOut(1);
    return point.proceed();
  }
}
