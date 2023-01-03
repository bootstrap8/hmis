package com.github.hbq.common.schedule.impl;


import com.github.hbq.common.schedule.RoundTimeHelper;
import com.github.hbq.common.schedule.annotation.Schedule;
import com.github.hbq.common.utils.RoundInfo;
import com.github.hbq.common.utils.TimeInfo;

/**
 * @author hbq
 */
public class RoundTimeHelperImpl implements RoundTimeHelper {

  @Override
  public RoundInfo createRoundInfo(Schedule report) {
    TimeInfo startInfo = new TimeInfo();
    TimeInfo endInfo = new TimeInfo();
    long curSecs = System.currentTimeMillis() / 1000L;
    if (report.align()) {
      startInfo.setSecs(report.time().timeOffsetWithAlign(curSecs, report.offset()));
      endInfo.setSecs(report.time().timeOffsetWithAlign(curSecs, report.offset() + 1));
    } else {
      startInfo.setSecs(report.time().timeOffsetWithUnAlign(curSecs, report.offset()));
      endInfo.setSecs(report.time().timeOffsetWithUnAlign(curSecs, report.offset() + 1));
    }
    startInfo.format();
    endInfo.format();
    RoundInfo roundInfo = new RoundInfo();
    roundInfo.setStartTime(startInfo);
    roundInfo.setEndTime(endInfo);
    return roundInfo;
  }

}
