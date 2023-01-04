package com.github.hbq.common.utils;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author hbq
 */
public enum FormatTable {

  /**
   * <code>表名前缀_2019</code>
   */
  YYYY {
    @Override
    public List<TableInfo> tables(long startTime, long endTime, TimeUnit unit, String prefix) {
      return TimeFragment.YEARS.fragment(startTime, endTime, unit).stream().map(r ->
      {
        TableInfo table = new TableInfo();
        table.setRoundInfo(r);
        table.setTablePrefix(prefix);
        table.setTableSuffix("yyyy");
        return table.format();
      }).collect(Collectors.toList());
    }

    @Override
    public List<TableInfo> histories(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<TableInfo> relations(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<TableInfo> groups(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }
  },

  /**
   * <code>表名前缀_201901</code>
   */
  YYYYMM {
    @Override
    public List<TableInfo> tables(long startTime, long endTime, TimeUnit unit, String prefix) {
      return TimeFragment.MONTHS.fragment(startTime, endTime, unit).stream().map(r ->
      {
        TableInfo table = new TableInfo();
        table.setRoundInfo(r);
        table.setTablePrefix(prefix);
        table.setTableSuffix("yyyyMM");
        return table.format();
      }).collect(Collectors.toList());
    }

    @Override
    public List<TableInfo> histories(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<TableInfo> relations(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<TableInfo> groups(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }
  },

  /**
   * <code>表名前缀_2019_01</code>
   */
  YYYY_LINE_MM {
    @Override
    public List<TableInfo> tables(long startTime, long endTime, TimeUnit unit, String prefix) {
      return TimeFragment.MONTHS.fragment(startTime, endTime, unit).stream().map(r ->
      {
        TableInfo table = new TableInfo();
        table.setRoundInfo(r);
        table.setTablePrefix(prefix);
        table.setTableSuffix("yyyy_MM");
        return table.format();
      }).collect(Collectors.toList());
    }

    @Override
    public List<TableInfo> histories(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<TableInfo> relations(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<TableInfo> groups(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }
  },

  /**
   * <code>表名前缀_20190131</code>
   */
  YYYYMMDD {
    @Override
    public List<TableInfo> tables(long startTime, long endTime, TimeUnit unit, String prefix) {
      return TimeFragment.DAYS.fragment(startTime, endTime, unit).stream().map(r ->
      {
        TableInfo table = new TableInfo();
        table.setRoundInfo(r);
        table.setTablePrefix(prefix);
        table.setTableSuffix("yyyyMMdd");
        return table.format();
      }).collect(Collectors.toList());
    }

    @Override
    public List<TableInfo> histories(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<TableInfo> relations(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<TableInfo> groups(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }
  },

  /**
   * <code>表名前缀_2019_01_31</code>
   */
  YYYY_LINE_MM_LINE_DD {
    @Override
    public List<TableInfo> tables(long startTime, long endTime, TimeUnit unit, String prefix) {
      return TimeFragment.DAYS.fragment(startTime, endTime, unit).stream().map(r ->
      {
        TableInfo table = new TableInfo();
        table.setRoundInfo(r);
        table.setTablePrefix(prefix);
        table.setTableSuffix("yyyy_MM_dd");
        return table.format();
      }).collect(Collectors.toList());
    }

    @Override
    public List<TableInfo> histories(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<TableInfo> relations(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<TableInfo> groups(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }
  },

  /**
   * <code>表名前缀_201901_31</code>
   */
  YYYYMM_LINE_DD {
    @Override
    public List<TableInfo> tables(long startTime, long endTime, TimeUnit unit, String prefix) {
      return TimeFragment.DAYS.fragment(startTime, endTime, unit).stream().map(r ->
      {
        TableInfo table = new TableInfo();
        table.setRoundInfo(r);
        table.setTablePrefix(prefix);
        table.setTableSuffix("yyyyMM_dd");
        return table.format();
      }).collect(Collectors.toList());
    }

    @Override
    public List<TableInfo> histories(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<TableInfo> relations(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<TableInfo> groups(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }
  },

  /**
   * <code>表名前缀_201952</code>
   */
  YYYYWW {
    @Override
    public List<TableInfo> tables(long startTime, long endTime, TimeUnit unit, String prefix) {
      return TimeFragment.WEEKS.fragment(startTime, endTime, unit).stream().map(r ->
      {
        TableInfo table = new TableInfo();
        table.setRoundInfo(r);
        table.setTablePrefix(prefix);
        table.setTableSuffix("yyyyww");
        table.setTableName(
            String.join("", prefix, FormatTime.YYYYWW_NO_JOIN.withSecs(r.getStartTime().getSecs())));
        return table;
      }).collect(Collectors.toList());
    }

    @Override
    public List<TableInfo> histories(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<TableInfo> relations(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<TableInfo> groups(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }
  },

  /**
   * <code>表名前缀_2019_52</code>
   */
  YYYY_LINE_WW {
    @Override
    public List<TableInfo> tables(long startTime, long endTime, TimeUnit unit, String prefix) {
      return TimeFragment.WEEKS.fragment(startTime, endTime, unit).stream().map(r ->
      {
        TableInfo table = new TableInfo();
        table.setRoundInfo(r);
        table.setTablePrefix(prefix);
        table.setTableSuffix("yyyy_ww");
        table.setTableName(
            String.join("", prefix, FormatTime.YYYYWW_UNDERSCORE.withSecs(r.getStartTime().getSecs())));
        return table;
      }).collect(Collectors.toList());
    }

    @Override
    public List<TableInfo> histories(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<TableInfo> relations(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<TableInfo> groups(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }
  },

  /**
   * <code>表名前缀_201912_52</code>
   */
  YYYYMM_LINE_WW {
    @Override
    public List<TableInfo> tables(long startTime, long endTime, TimeUnit unit, String prefix) {
      return TimeFragment.WEEKS.fragment(startTime, endTime, unit).stream().map(r ->
      {
        TableInfo table = new TableInfo();
        table.setRoundInfo(r);
        table.setTablePrefix(prefix);
        table.setTableSuffix("yyyyMM_ww");
        table.setTableName(
            String.join("", prefix, FormatTime.YYYYMMWW_UNDERSCORE.withSecs(r.getStartTime().getSecs())));
        return table;
      }).collect(Collectors.toList());
    }

    @Override
    public List<TableInfo> histories(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<TableInfo> relations(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<TableInfo> groups(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }
  },

  /**
   * <code>表名前缀_2019_12_52</code>
   */
  YYYY_LINE_MM_LINE_WW {
    @Override
    public List<TableInfo> tables(long startTime, long endTime, TimeUnit unit, String prefix) {
      return TimeFragment.WEEKS.fragment(startTime, endTime, unit).stream().map(r ->
      {
        TableInfo table = new TableInfo();
        table.setRoundInfo(r);
        table.setTablePrefix(prefix);
        table.setTableSuffix("yyyy_MM_ww");
        table.setTableName(
            String.join("", prefix, FormatTime.YYYYMMWW_UNDERSCORE.withSecs(r.getStartTime().getSecs())));
        return table;
      }).collect(Collectors.toList());
    }

    @Override
    public List<TableInfo> histories(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<TableInfo> relations(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<TableInfo> groups(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }
  },

  YYYY_MM {
    @Override
    public List<TableInfo> tables(long startTime, long endTime, TimeUnit unit, String prefix) {
      return TimeFragment.MONTHS.fragment(startTime, endTime, unit).stream().map(r ->
      {
        TableInfo table = new TableInfo();
        table.setRoundInfo(r);
        table.setTablePrefix(prefix);
        table.setTableSuffix("yyyy_MM");
        table.setTableName(
            String.join("", prefix, FormatTime.YYYYMM_UNDERSCORE.withSecs(r.getStartTime().getSecs())));
        return table;
      }).collect(Collectors.toList());
    }
  },

  YYYY_MM_DD {
    @Override
    public List<TableInfo> tables(long startTime, long endTime, TimeUnit unit, String prefix) {
      return TimeFragment.DAYS.fragment(startTime, endTime, unit).stream().map(r ->
      {
        TableInfo table = new TableInfo();
        table.setRoundInfo(r);
        table.setTablePrefix(prefix);
        table.setTableSuffix("yyyy_MM_dd");
        table.setTableName(
            String.join("", prefix, FormatTime.YYYYMMDD_UNDERSCORE.withSecs(r.getStartTime().getSecs())));
        return table;
      }).collect(Collectors.toList());
    }

    @Override
    public List<TableInfo> histories(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<TableInfo> relations(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<TableInfo> groups(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }
  },

  MM_DD {
    @Override
    public List<TableInfo> tables(long startTime, long endTime, TimeUnit unit, String prefix) {
      return TimeFragment.DAYS.fragment(startTime, endTime, unit).stream().map(r ->
      {
        TableInfo table = new TableInfo();
        table.setRoundInfo(r);
        table.setTablePrefix(prefix);
        table.setTableSuffix("MM_dd");
        table.setTableName(
            String.join("", prefix, FormatTime.MMDD_UNDERSCORE.withSecs(r.getStartTime().getSecs())));
        return table;
      }).collect(Collectors.toList());
    }

    @Override
    public List<TableInfo> histories(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<TableInfo> relations(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<TableInfo> groups(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }
  },

  MMDD {
    @Override
    public List<TableInfo> tables(long startTime, long endTime, TimeUnit unit, String prefix) {
      return TimeFragment.DAYS.fragment(startTime, endTime, unit).stream().map(r ->
      {
        TableInfo table = new TableInfo();
        table.setRoundInfo(r);
        table.setTablePrefix(prefix);
        table.setTableSuffix("MMdd");
        table.setTableName(
            String.join("", prefix, FormatTime.MMDD_NO_JOINER.withSecs(r.getStartTime().getSecs())));
        return table;
      }).collect(Collectors.toList());
    }

    @Override
    public List<TableInfo> histories(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<TableInfo> relations(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }

    @Override
    public List<TableInfo> groups(long startTime, long endTime, TimeUnit unit) {
      throw new UnsupportedOperationException();
    }
  };

  /**
   * 获取表名列表
   *
   * @param startTime 开始时间
   * @param endTime   结束时间
   * @param unit      开始、结束时间单位
   * @param prefix    表名前缀
   * @return
   */
  public List<TableInfo> tables(long startTime, long endTime, TimeUnit unit, String prefix) {
    throw new AbstractMethodError();
  }

  /**
   * 获取表名列表
   *
   * @param startTime 开始时间字符串
   * @param endTime   结束时间字符串
   * @param format    开始、结束时间格式化对象
   * @param prefix    表名前缀
   * @return
   */
  public List<TableInfo> tables(String startTime, String endTime, FormatTime format, String prefix) {
    return tables(format.toSecs(startTime), format.toSecs(endTime), TimeUnit.SECONDS, prefix);
  }

  public List<TableInfo> histories(String startTime, String endTime, FormatTime format) {
    return histories(format.toSecs(startTime), format.toSecs(endTime), TimeUnit.SECONDS);
  }

  public List<TableInfo> relations(String startTime, String endTime, FormatTime format) {
    return relations(format.toSecs(startTime), format.toSecs(endTime), TimeUnit.SECONDS);
  }

  public List<TableInfo> groups(String startTime, String endTime, FormatTime format) {
    return groups(format.toSecs(startTime), format.toSecs(endTime), TimeUnit.SECONDS);
  }

  public List<TableInfo> histories(long startTime, long endTime, TimeUnit unit) {
    return tables(startTime, endTime, unit, "ta_historyalarm_");
  }

  public List<TableInfo> relations(long startTime, long endTime, TimeUnit unit) {
    return tables(startTime, endTime, unit, "ta_hisalarm_relation_");
  }

  public List<TableInfo> groups(long startTime, long endTime, TimeUnit unit) {
    return tables(startTime, endTime, unit, "ta_hisalarm_cust_");
  }
}
