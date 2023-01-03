
package com.github.hbq.common.rule.map;

import com.github.hbq.common.rule.NodeInfo;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author hbq
 */
public class ORChainRule implements Rule<Map> {

  private List<Rule> rules;

  public ORChainRule() {

    this.rules = new ArrayList<Rule>();
  }

  public ORChainRule(List<Rule> rules) {

    this.rules = rules;
  }

  @Override
  public boolean accept(Map msg) {
    for (Rule r : rules) {
      if (r.accept(msg)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean anyOne(Map msg) {
    throw new UnsupportedOperationException();
  }

  /**
   * 增加规则
   *
   * @param rule
   */
  public void addRule(Rule rule) {
    rules.add(rule);
  }

  @Override
  public String toSql() {
    StringBuilder sb = new StringBuilder();
    boolean isfirst = true;
    for (Rule rule : rules) {
      String ssql = rule.toSql();
      if (ssql != null) {
        if (isfirst) {
          sb.append(ssql);
          isfirst = false;
        } else {
          sb.append(OR).append(ssql);
        }
      }
    }
    if (sb.length() == 0) {
      return null;
    } else {
      return sb.toString();
    }
  }

  @Override
  public String toSqlIncludeKeys(Set<String> inkeys) {
    StringBuilder sb = new StringBuilder();
    boolean isfirst = true;
    for (Rule rule : rules) {
      String ssql = rule.toSqlIncludeKeys(inkeys);
      if (ssql != null) {
        if (isfirst) {
          sb.append(ssql);
          isfirst = false;
        } else {
          sb.append(OR).append(ssql);
        }
      }
    }
    if (sb.length() == 0) {
      return null;
    } else {
      return sb.toString();
    }
  }

  @Override
  public String toSqlExcludeKeys(Set<String> exkeys) {
    StringBuilder sb = new StringBuilder();
    boolean isfirst = true;
    for (Rule rule : rules) {
      String ssql = rule.toSqlExcludeKeys(exkeys);
      if (ssql != null) {
        if (isfirst) {
          sb.append(ssql);
          isfirst = false;
        } else {
          sb.append(OR).append(ssql);
        }
      }
    }
    if (sb.length() == 0) {
      return null;
    } else {
      return sb.toString();
    }
  }

  @Override
  public List<NodeInfo> toNodeInfos() {
    List<NodeInfo> nodes = new ArrayList<>();
    for (Rule rule : rules) {
      nodes.addAll(rule.toNodeInfos());
    }
    return nodes;
  }

  @Override
  public Set<String> conditionKeySet() {
    Set<String> set = new HashSet<>(rules.size());
    for (Rule rule : rules) {
      set.addAll(rule.conditionKeySet());
    }
    return set;
  }

  public List<Rule> getRules() {
    return rules;
  }
}
