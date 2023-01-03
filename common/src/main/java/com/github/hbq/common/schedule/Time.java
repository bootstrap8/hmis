package com.github.hbq.common.schedule;

import org.joda.time.DateTime;

/**
 * @author hbq
 */
public enum Time {
  /**
   * 秒
   */
  SECONDS {
    @Override
    public long timeOffsetWithUnAlign(long curSecs, int offset) {
      return curSecs + offset;
    }

    @Override
    public long timeOffsetWithAlign(long curSecs, int offset) {
      return curSecs + offset;
    }
  },
  /**
   * 分钟
   */
  MINUTES {
    @Override
    public long timeOffsetWithUnAlign(long curSecs, int offset) {
      return curSecs + offset * 60L;
    }

    @Override
    public long timeOffsetWithAlign(long curSecs, int offset) {
      DateTime dateTime = new DateTime(curSecs * 1000L);
      return dateTime.plusMinutes(offset).secondOfMinute().setCopy(0).getMillis() / 1000L;
    }
  },
  /**
   * 小时
   */
  HOURS {
    @Override
    public long timeOffsetWithUnAlign(long curSecs, int offset) {
      return curSecs + offset * 3600L;
    }

    @Override
    public long timeOffsetWithAlign(long curSecs, int offset) {
      DateTime dateTime = new DateTime(curSecs * 1000L);
      return
          dateTime.plusHours(offset).minuteOfHour().setCopy(0).secondOfMinute().setCopy(0)
              .getMillis() / 1000L;
    }
  },
  /**
   * 天
   */
  DAYS {
    @Override
    public long timeOffsetWithUnAlign(long curSecs, int offset) {
      return curSecs + offset * 24 * 3600L;
    }

    @Override
    public long timeOffsetWithAlign(long curSecs, int offset) {
      DateTime dateTime = new DateTime(curSecs * 1000L);
      return dateTime.plusDays(offset).hourOfDay().setCopy(0).minuteOfHour().setCopy(0)
          .secondOfMinute().setCopy(0).getMillis() / 1000L;
    }
  },
  /**
   * 周
   */
  WEEKS {
    @Override
    public long timeOffsetWithUnAlign(long curSecs, int offset) {
      return curSecs + offset * 7 * 24 * 3600L;
    }

    @Override
    public long timeOffsetWithAlign(long curSecs, int offset) {
      DateTime dateTime = new DateTime(curSecs * 1000L);
      return dateTime.plusWeeks(offset).dayOfWeek().setCopy(1).hourOfDay().setCopy(0)
          .minuteOfHour().setCopy(0)
          .secondOfMinute().setCopy(0).getMillis() / 1000L;
    }
  },
  /**
   * 月
   */
  MONTHS {
    @Override
    public long timeOffsetWithUnAlign(long curSecs, int offset) {
      return new DateTime(curSecs * 1000L).plusMonths(offset).getMillis() / 1000L;
    }

    @Override
    public long timeOffsetWithAlign(long curSecs, int offset) {
      DateTime dateTime = new DateTime(curSecs * 1000L);
      return dateTime.plusMonths(offset).dayOfMonth().setCopy(1).hourOfDay().setCopy(0)
          .minuteOfHour().setCopy(0)
          .secondOfMinute().setCopy(0).getMillis() / 1000L;
    }
  },
  /**
   * 年
   */
  YEARS {
    @Override
    public long timeOffsetWithUnAlign(long curSecs, int offset) {
      return curSecs + offset * 365 * 24 * 3600L;
    }

    @Override
    public long timeOffsetWithAlign(long curSecs, int offset) {
      DateTime dateTime = new DateTime(curSecs * 1000L);
      return dateTime.plusYears(offset).dayOfYear().setCopy(1).hourOfDay().setCopy(0)
          .minuteOfHour().setCopy(0)
          .secondOfMinute().setCopy(0).getMillis() / 1000L;
    }
  };

  public long timeOffsetWithUnAlign(long curSecs, int offset) {
    throw new AbstractMethodError();
  }

  public long timeOffsetWithAlign(long curSecs, int offset) {
    throw new AbstractMethodError();
  }
}
