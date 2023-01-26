package com.github.hbq.agent.app.serv.impl.kafka;

import com.github.hbq.agent.app.pojo.QuotaData;
import com.github.hbq.agent.app.serv.impl.KafkaQuotaGetImpl;
import java.util.List;
import java.util.Objects;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author hbq
 */
@Aspect
public class KafkaInAspect {

  @Autowired
  private KafkaInRateLimit kafkaInRateLimit;

  @Autowired
  private KafkaQuotaGetImpl kafkaQuotaGet;

  @Pointcut("@annotation(org.springframework.kafka.annotation.KafkaListener)")
  public void listenerPointCut() {
  }

  @Around("listenerPointCut()")
  public Object around(ProceedingJoinPoint point) throws Throwable {
    Object[] args = point.getArgs();
    if (Objects.isNull(args) || args.length < 1) {
      return point.proceed();
    }
    Object rec = args[0];
    if (rec instanceof List) {
      int size = ((List) rec).size();
      kafkaInRateLimit.acquire(size);
      kafkaQuotaGet.incrementAndGetIn(size);
    } else if (rec instanceof QuotaData) {
      kafkaInRateLimit.acquire(1);
      kafkaQuotaGet.incrementAndGetIn(1);
    }
    return point.proceed();
  }
}
