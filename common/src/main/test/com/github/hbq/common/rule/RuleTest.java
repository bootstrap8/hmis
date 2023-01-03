package com.github.hbq.common.rule;

import com.github.hbq.common.rule.map.ChainRule;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author hbq
 */
public class RuleTest {

  @Test
  public void testMapRule() {
    String rc = "(p1=1|p1=2)&(p2=\"abc\"|p2%=\"xx\")";
    ChainRule rule = new ChainRule(rc);

    Map map = new HashMap();
    map.put("p1", 1);
    map.put("p2", "xxabc");
    Assert.assertTrue(rule.accept(map));

    map.clear();
    map.put("p1", 3);
    map.put("p2", "abc");
    Assert.assertFalse(rule.accept(map));

    map.clear();
    map.put("p1", 2);
    map.put("p2", "zzz");
    Assert.assertFalse(rule.accept(map));
  }

  @Test
  public void testSymbolRule() {
    String rc = "(^p1^=^1^ or ^p1^=^2^)(^p2^=^abc^ or ^p2^like^xx^)";
    com.github.hbq.common.rule.symbol.ChainRule rule
        = new com.github.hbq.common.rule.symbol.ChainRule(rc);

    Map map = new HashMap();
    map.put("p1", 1);
    map.put("p2", "xxabc");
    Assert.assertTrue(rule.accept(map));

    map.clear();
    map.put("p1", 3);
    map.put("p2", "abc");
    Assert.assertFalse(rule.accept(map));

    map.clear();
    map.put("p1", 2);
    map.put("p2", "zzz");
    Assert.assertFalse(rule.accept(map));
  }
}
