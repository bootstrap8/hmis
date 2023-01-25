package com.github.hbq.agent.app.pojo;

import java.util.concurrent.TimeUnit;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hbq
 */
@Slf4j
@Data
public class CycleInfo {

  private TimeUnit unit;
  private long time;
  private String key;

  public CycleInfo() {
  }

  public CycleInfo(TimeUnit unit, long time) {
    this.unit = unit;
    this.time = time;
    this.key = String.join(":", unit.name(), String.valueOf(time));
  }

  public final static CycleInfo SECOND1 = new CycleInfo(TimeUnit.SECONDS, 1);

  public final static CycleInfo SECOND5 = new CycleInfo(TimeUnit.SECONDS, 5);

  public final static CycleInfo SECOND10 = new CycleInfo(TimeUnit.SECONDS, 10);

  public final static CycleInfo SECOND30 = new CycleInfo(TimeUnit.SECONDS, 30);

  public final static CycleInfo MINUTE1 = new CycleInfo(TimeUnit.MINUTES, 1);

  public final static CycleInfo MINUTE5 = new CycleInfo(TimeUnit.MINUTES, 5);

  public final static CycleInfo MINUTE10 = new CycleInfo(TimeUnit.MINUTES, 10);

  public final static CycleInfo MINUTE30 = new CycleInfo(TimeUnit.MINUTES, 30);

  public final static CycleInfo HOUR1 = new CycleInfo(TimeUnit.HOURS, 1);

  public final static CycleInfo DAY1 = new CycleInfo(TimeUnit.DAYS, 1);

}
