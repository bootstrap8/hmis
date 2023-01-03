package com.github.hbq.common.lock;

import com.github.hbq.common.utils.StrUtils;
import com.github.hbq.common.utils.UidBox;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;

/**
 * @author hbq
 */
@Aspect
@Slf4j
public class LockAspect {

  @Autowired
  private LockManage lockManage;

  @Pointcut("@annotation(com.github.hbq.common.lock.DLock)")
  public void distributedLockPointCut() {

  }

  @Around("distributedLockPointCut()")
  public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
    DLock ds = AnnotationUtils.findAnnotation(methodSignature.getMethod(), DLock.class);
    if (Objects.isNull(ds)) {
      return joinPoint.proceed();
    }
    String lockKey = ds.lockKey();
    if (StrUtils.strEmpty(lockKey)) {
      lockKey = String.join(".", joinPoint.getTarget().getClass().getName(), joinPoint.getSignature().getName());
    }
    int retryCount = ds.retryCount();
    int initRetryCount = retryCount;
    String requestId = UidBox.uid();
    try {
      while (!lockManage.query().tryLock(lockKey, requestId, ds.expire(), ds.unit())) {
        if (retryCount-- == 0) {
          throw new UnsupportedOperationException(String.format("重试了%d次，都未获取到锁[%s][%s]，请检查",
              initRetryCount, lockKey, requestId));
        }
        try {
          ds.retryUnit().sleep(ds.retryCycle());
        } catch (InterruptedException e) {
          break;
        }
      }
      log.debug("第[{}]次重试获取到锁: [{}][{}]", initRetryCount - retryCount, lockKey, requestId);
      return joinPoint.proceed();
    } finally {
      try {
        lockManage.query().releaseLock(lockKey, requestId);
      } catch (Exception e) {
      }
    }
  }
}
