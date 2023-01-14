package com.github.hbq.agent.app.pojo;

import com.alibaba.fastjson.JSON;
import com.github.hbq.common.utils.FormatTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.collections.MapUtils;

/**
 * @author hbq
 */
public class AppInfo {

  private String name;
  private String desc;
  private Map<String, Object> tags = new HashMap<>(4);
  private long regTime;
  private String key;

  public AppInfo(String name, String desc) {
    this.name = name;
    this.desc = desc;
    this.regTime = FormatTime.nowSecs();
  }

  public String getKey() {
    return this.name;
  }

  public void addTag(String key, String value) {
    this.tags.put(key, value);
  }

  public void addTags(Map<String, Object> map) {
    this.tags.putAll(map);
  }


  public String getName() {
    return name;
  }

  public String getDesc() {
    return desc;
  }


  public Map<String, Object> getTags() {
    return Collections.unmodifiableMap(this.tags);
  }

  public long getRegTime() {
    return regTime;
  }

  public String getFmtRegTime() {
    return FormatTime.YYYYMMDDHHMISS.withSecs(getRegTime());
  }

  public Map<String, Object> toMybatisMap() {
    Map<String, Object> map = new HashMap<>(8);
    map.put("name", name);
    map.put("desc", desc);
    map.put("reg_time", regTime);
    map.put("tags", JSON.toJSONString(tags));
    return map;
  }

}
