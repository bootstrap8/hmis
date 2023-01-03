package com.github.hbq.common.dict;

import com.github.hbq.common.dict.map.ListDictImpl;
import com.github.hbq.common.dict.map.MapDictImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author hbq
 */
public class DictTest {

  private static JdbcTemplate jt;
  private static MapDictImpl mapDict;
  private static ListDictImpl listDict;

  @BeforeClass
  public static void init() {
    BasicDataSource ds = new BasicDataSource();
    ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
    ds.setUrl("jdbc:mysql://192.168.56.2:3306/hbq?useUnicode=true&serverTimezone=GMT%2B8&characterEncoding=utf8");
    ds.setUsername("hbq");
    ds.setPassword("hbq");

    jt = new JdbcTemplate(ds);
    mapDict = new MapDictImpl();
    mapDict.setJt(jt);
    listDict = new ListDictImpl();
    listDict.setMapDict(mapDict);
    mapDict.reloadImmediately();
  }

  @Test
  public void testDictMap() {
    Map data = new HashMap<>();
    data.put("foo", 1);
    data.put("bar", 15);
    mapDict.tranAnyForKey(data, false);
    Assert.assertEquals("一", MapUtils.getString(data, "foo"));
    Assert.assertEquals("a", MapUtils.getString(data, "bar"));
  }

  @Test
  public void testDictList() {
    List<Map> list = new ArrayList<>();
    Map data = new HashMap<>();
    data.put("foo", 2);
    data.put("bar", 16);
    list.add(data);

    data = new HashMap<>();
    data.put("foo", 4);
    data.put("bar", 101);
    list.add(data);

    listDict.tranAnyForKey(list, false);

    Assert.assertEquals("二", MapUtils.getString(list.get(0), "foo"));
    Assert.assertEquals("b", MapUtils.getString(list.get(0), "bar"));
    Assert.assertEquals("四", MapUtils.getString(list.get(1), "foo"));
    Assert.assertEquals("c", MapUtils.getString(list.get(1), "bar"));
  }
}
