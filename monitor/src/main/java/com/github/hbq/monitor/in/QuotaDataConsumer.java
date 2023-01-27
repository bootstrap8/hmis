package com.github.hbq.monitor.in;

import com.github.hbq.agent.app.pojo.QuotaData;
import com.github.hbq.common.utils.Count;
import com.github.hbq.monitor.dao.dialect.QuotaDataDao;
import com.github.hbq.monitor.dao.dialect.manage.DialectManage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author hbq
 */
@Component("monitor-in-QuotaDataConsumer")
@Slf4j
public class QuotaDataConsumer {

  public static final int MAX_BATCH_SIZE = 20;

  public static final long SECONDS5 = 5000L;

  private List<QuotaData> qData = new ArrayList<>(500);

  private Count count = Count.safe();

  @Autowired
  private DialectManage dialectManage;

  @KafkaListener(topics = {"HBQ-AGENT-QUOTA-DATA"})
  void consume(List<QuotaData> list) {
    log.info("接收到指标数据: {} 条", list.size());
    // TODO
    qData.addAll(list);
    count.incrementAndGet(list.size());
    if (count.compareAndInit(MAX_BATCH_SIZE, SECONDS5)) {
      dialectManage.query().ifPresent(dialect -> {
        try {
          dialect.getMapper(QuotaDataDao.class).saveQuotaData(qData);
          log.info("批量入库指标数据: {}", qData.size());
        } catch (Exception e) {
          log.error("批量入库指标数据异常", e);
        } finally {
          qData.clear();
        }
      });
    }
  }

  public static void main(String[] args) throws Exception {
    Count c = Count.safe();
    int i = 100000;
    for (int i1 = 0; i1 < i; i1++) {
      c.incrementAndGet(1);
      if (c.compareAndInit(MAX_BATCH_SIZE, SECONDS5)) {
        log.info("....");
      }
      TimeUnit.MILLISECONDS.sleep(50);
    }
  }
}
