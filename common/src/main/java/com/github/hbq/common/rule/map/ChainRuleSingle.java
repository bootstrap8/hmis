
package com.github.hbq.common.rule.map;

import com.github.hbq.common.rule.NodeInfo;
import com.github.hbq.common.utils.StrUtils;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

/**
 * 单个判断单元 字符型：检索双引号是否为-1，支持的操作符号:=、！=、%= 数字型：=、！=、>=、<=、>、<
 *
 * @author hbq
 */
@Slf4j
public class ChainRuleSingle implements Rule<Map> {

  public static final String LINE = "\n";

  final private Pattern strPtn = Pattern.compile("(\".*\"$)");

  final private Pattern numbPtn = Pattern.compile("(\\d+\\.*\\d*$)");

  final private Pattern strOpPtn = Pattern.compile("(%=$)|(!=$)|(=$)|(!%=$)");

  final private Pattern numbOpPtn = Pattern
      .compile("(>=$)|(<=$)|(!=$)|(>$)|(<$)|(=$)");

  final private Pattern nullOpPtn = Pattern.compile("(=$)|(!=$)");

  // 对比的key
  private String cmpKey = null;

  // 操作符号
  private CMP cmpOpr = null;

  // 对比的值
  private String cmpValue = null;

  // sql语句的key
  private String sqlKey = null;

  //原始sql语句的value值
  private Object orialSqlValue = null;

  // 原始sql语句的操作符号
  private String orialSqlOpr = null;

  // sql语句的value
  private String sqlValue = null;

  // sql语句的操作符号
  private String sqlOpr = null;

  // 此字段不是数据字段，而是一个逻辑字段
  final static String UN_DB_KEY_DELATIME = "delaytime";

  private boolean nullOnNotEqual = false;

  public ChainRuleSingle(String rule) {
    this(rule, false);
  }

  public ChainRuleSingle(String rule, boolean nullOnNotEqual) {
    this.nullOnNotEqual = nullOnNotEqual;
    if (rule == null) {
      throw new NullPointerException("规则内容为空");
    }
    Matcher m = null;
    String op = null;
    if ((m = nullOpPtn.matcher(rule)).find()) {
      op = m.group();
      orialSqlOpr = op;
      if (EQ.equals(op)) {
        cmpOpr = new NullEQ();
        sqlOpr = ISNULL;
      } else {
        cmpOpr = new NullNEQ();
        sqlOpr = ISNOTNULL;
      }
    } else if (rule.indexOf(DQ) != -1) {
      // 字符
      m = strPtn.matcher(rule);
      if (m.find()) {
        cmpValue = m.group();
        rule = rule.substring(0, rule.length() - cmpValue.length());
        cmpValue = cmpValue.substring(1, cmpValue.length() - 1)
            .replace("\\\\", "\\").replace("\\\"", "\"");// 去掉双引号
        orialSqlValue = cmpValue;
        sqlValue = cmpValue.replace("'", "''");
      }
      m = strOpPtn.matcher(rule);
      if (m.find()) {
        op = m.group();
        orialSqlOpr = op;
        if (EQ.equals(op)) {
          cmpOpr = new StrEq();
          sqlOpr = SQLEQ;
        } else if (NEQ.equals(op)) {
          cmpOpr = new StrNeq();
          sqlOpr = SQLNEQ;
        } else if (LIKE.equals(op)) {
          cmpOpr = new StrLike();
          sqlOpr = SQL_LIKE;
          sqlValue = PERCENT + sqlValue + PERCENT;
        } else if (NOT_LIKE.equals(op)) {
          cmpOpr = new StrNotLike(new StrLike());
          sqlOpr = SQL_NOT_LIKE;
          sqlValue = PERCENT + sqlValue + PERCENT;
        }
        sqlValue = SQ + sqlValue + SQ;
      }
    } else {
      // 数字
      m = numbPtn.matcher(rule);
      if (m.find()) {
        cmpValue = m.group();
        sqlValue = cmpValue;
        try {
          orialSqlValue = Integer.parseInt(cmpValue);
        } catch (NumberFormatException e) {
          try {
            orialSqlValue = Long.parseLong(cmpValue);
          } catch (NumberFormatException e2) {
            orialSqlValue = cmpValue;
          }
        }
        rule = rule.substring(0, rule.length() - cmpValue.length());
      }
      m = numbOpPtn.matcher(rule);
      if (m.find()) {
        op = m.group();
        orialSqlOpr = op;
        if (EQ.equals(op)) {
          cmpOpr = new NumbEq();
          sqlOpr = SQLEQ;
        } else if (NEQ.equals(op)) {
          cmpOpr = new NumbNeq();
          sqlOpr = SQLNEQ;
        } else if (GT.equals(op)) {
          cmpOpr = new NumbGT();
          sqlOpr = SQLGT;
        } else if (LT.equals(op)) {
          cmpOpr = new NumbLT();
          sqlOpr = SQLLT;
        } else if (GET.equals(op)) {
          cmpOpr = new NumbGE();
          sqlOpr = SQLGET;
        } else if (LET.equals(op)) {
          cmpOpr = new NumbLE();
          sqlOpr = SQLLET;
        }
      }
    }
    cmpKey = rule.substring(0, rule.length() - op.length());
    sqlKey = cmpKey;
    if (cmpKey == null || cmpOpr == null) {
      throw new NullPointerException("规则解析错误，无法争取的解析,规则内容:" + rule);
    }
  }

  @Override
  public boolean accept(Map msg) {
    String actual = MapUtils.getString(msg, cmpKey);
    return cmpOpr.cmp(actual, cmpValue);
  }

  @Override
  public boolean anyOne(Map msg) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Set<String> conditionKeySet() {
    Set<String> set = new HashSet<>();
    set.add(cmpKey);
    return set;
  }

  /**
   * 对比操作接口
   *
   * @author hbq
   */
  interface CMP {

    boolean cmp(String actual, String expected);
  }

  /**
   * 字符串等于
   *
   * @author hbq
   */
  class StrEq implements CMP {

    @Override
    public boolean cmp(String actual, String expected) {
      if (actual == null && expected == null) {
        return true;
      }
      if (StringUtils.equals(LINE, actual)) {
        return true;
      }
      return StringUtils.equals(actual, expected);
    }
  }

  /**
   * 字符串不等于
   *
   * @author hbq
   */
  class StrNeq extends StrEq {

    @Override
    public boolean cmp(String actual, String expected) {
      if (actual == null && expected == null) {
        return false;
      }
      if (StringUtils.equals(LINE, actual)) {
        return true;
      }
      return !StringUtils.equals(actual, expected);
    }
  }

  /**
   * 字符串包含 :src 包含 target
   *
   * @author hbq
   */
  class StrLike implements CMP {

    @Override
    public boolean cmp(String actual, String expected) {
      if (StringUtils.equals(LINE, actual)) {
        return true;
      }
      return StringUtils.contains(actual, expected);
    }
  }

  class StrNotLike implements CMP {

    private CMP strLike;

    public StrNotLike(CMP strLike) {
      this.strLike = strLike;
    }

    @Override
    public boolean cmp(String actual, String expected) {
      if (StringUtils.equals(LINE, actual)) {
        return true;
      }
      return !StringUtils.contains(actual, expected);
    }
  }

  /**
   * 数字等于
   *
   * @author hbq
   */
  class NumbEq implements CMP {

    @Override
    public boolean cmp(String actual, String expected) {
      if (actual == null && expected == null) {
        return true;
      }
      if (StrUtils.strEmpty(actual)) {
        return false;
      }
      if (StrUtils.strEmpty(expected)) {
        return false;
      }
      if (StringUtils.equals(LINE, actual)) {
        return true;
      }
      try {
        return Double.parseDouble(actual) == Double.parseDouble(expected);
      } catch (NumberFormatException e) {
        return false;
      }
    }
  }

  /**
   * 数字不等于
   *
   * @author hbq
   */
  class NumbNeq extends NumbEq {

    @Override
    public boolean cmp(String actual, String expected) {

      if (actual == null && expected == null) {
        return false;
      }
      if (StrUtils.strEmpty(actual)) {
        return true;
      }
      if (StrUtils.strEmpty(expected)) {
        return true;
      }
      if (StringUtils.equals(LINE, actual)) {
        return true;
      }
      try {
        return Double.parseDouble(actual) != Double.parseDouble(expected);
      } catch (NumberFormatException e) {
        return false;
      }
    }
  }

  /**
   * 数字大于
   *
   * @author hbq
   */
  class NumbGT implements CMP {

    @Override
    public boolean cmp(String actual, String expected) {

      if (actual == null && expected == null) {
        return false;
      }
      if (StrUtils.strEmpty(actual)) {
        return false;
      }
      if (StrUtils.strEmpty(expected)) {
        return false;
      }
      if (StringUtils.equals(LINE, actual)) {
        return true;
      }
      try {
        return Double.parseDouble(actual) > Double.parseDouble(expected);
      } catch (NumberFormatException e) {
        return false;
      }
    }
  }

  /**
   * 数字小于
   *
   * @author hbq
   */
  class NumbLT implements CMP {

    @Override
    public boolean cmp(String actual, String expected) {

      if (actual == null && expected == null) {
        return false;
      }
      if (StrUtils.strEmpty(actual)) {
        return false;
      }
      if (StrUtils.strEmpty(expected)) {
        return false;
      }
      if (StringUtils.equals(LINE, actual)) {
        return true;
      }
      try {
        return Double.parseDouble(actual) < Double.parseDouble(expected);
      } catch (NumberFormatException e) {
        return false;
      }
    }
  }

  /**
   * 大于等于
   *
   * @author hbq
   */
  class NumbGE extends NumbLT {

    @Override
    public boolean cmp(String actual, String expected) {

      if (actual == null && expected == null) {
        return false;
      }
      if (StrUtils.strEmpty(actual)) {
        return false;
      }
      if (StrUtils.strEmpty(expected)) {
        return false;
      }
      if (StringUtils.equals(LINE, actual)) {
        return true;
      }
      try {
        return Double.parseDouble(actual) >= Double.parseDouble(expected);
      } catch (NumberFormatException e) {
        return false;
      }
    }
  }

  /**
   * 小于等于
   *
   * @author hbq
   */
  class NumbLE extends NumbGT {

    @Override
    public boolean cmp(String actual, String expected) {

      if (actual == null && expected == null) {
        return false;
      }
      if (StrUtils.strEmpty(actual)) {
        return false;
      }
      if (StrUtils.strEmpty(expected)) {
        return false;
      }
      if (StringUtils.equals(LINE, actual)) {
        return true;
      }
      try {
        return Double.parseDouble(actual) <= Double.parseDouble(expected);
      } catch (NumberFormatException e) {
        return false;
      }
    }
  }

  /**
   * null的相等
   *
   * @author hbq
   */
  class NullEQ implements CMP {

    @Override
    public boolean cmp(String actual, String expected) {
      if (LINE.equals(actual)) {
        return true;
      }
      return actual == null || "".equals(actual);
    }
  }

  /**
   * null的不等判断
   *
   * @author hbq
   */
  class NullNEQ implements CMP {

    @Override
    public boolean cmp(String actual, String expected) {
      if (LINE.equals(actual)) {
        return true;
      }
      return actual != null && !"".equals(actual);
    }
  }

  @Override
  public String toSql() {
    StringBuilder sb;
    if ((cmpOpr instanceof StrNeq || cmpOpr instanceof NumbNeq) && nullOnNotEqual) {
      sb = new StringBuilder();
      sb.append("( ").append(sqlKey);
      sb.append(sqlOpr);
      if (sqlValue != null) {
        sb.append(sqlValue);
      }
      sb.append(" or ").append(sqlKey).append(" is null ");
      sb.append(")");
    } else {
      sb = new StringBuilder(sqlKey);
      sb.append(sqlOpr);
      if (sqlValue != null) {
        sb.append(sqlValue);
      }
    }
    return sb.toString();
  }

  @Override
  public String toSqlIncludeKeys(Set<String> inkeys) {
    if (inkeys.contains(sqlKey)) {
      return toSql();
    } else {
      return null;
    }
  }

  @Override
  public String toSqlExcludeKeys(Set<String> exkeys) {
    if (exkeys.contains(sqlKey)) {
      return null;
    } else {
      return toSql();
    }
  }

  @Override
  public List<NodeInfo> toNodeInfos() {
    List<NodeInfo> nodes = new ArrayList<>();
    NodeInfo node = new NodeInfo(sqlKey, orialSqlValue == null ? "" : orialSqlValue.toString(), orialSqlOpr);
    nodes.add(node);
    return nodes;
  }

  public static void main(String[] args) throws Exception {
    System.out.println(StrUtils.strEmpty("\n"));
  }
}
