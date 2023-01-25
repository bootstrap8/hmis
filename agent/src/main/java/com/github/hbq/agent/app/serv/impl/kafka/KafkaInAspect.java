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
      kafkaQuotaGet.incrementAndGetIn(((List) rec).size());
    } else if (rec instanceof QuotaData) {
      kafkaQuotaGet.incrementAndGetIn(1);
    }
    return point.proceed();
  }
}
