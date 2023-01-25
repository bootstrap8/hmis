package com.github.hbq.agent.app.pojo;

import com.alibaba.fastjson.JSON;
import com.github.hbq.common.utils.FormatTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.Data;

/**
 * @author hbq
 */
@Data
public class InstInfo {

  private AppInfo app;
  private String dataCenter;
  private String ip;
  private String hostName;
  private int port;
  private String processNo;
  private Map<String, Object> tags = new HashMap<>(4);
  private long regTime;
  private String key;

  public InstInfo() {
  }

  public InstInfo(AppInfo app, String dataCenter, String ip, String hostName, int port, String processNo) {
    this.app = app;
    this.dataCenter = dataCenter;
    this.ip = ip;
    this.hostName = hostName;
    this.port = port;
    this.processNo = processNo;
    this.regTime = FormatTime.nowSecs();
    this.key = String.join(",", app.getKey(), dataCenter, ip, String.valueOf(port));
  }

  public String getFmtRegTime() {
    return FormatTime.YYYYMMDDHHMISS.withSecs(getRegTime());
  }

  public Map<String, Object> getTags() {
    return Collections.unmodifiableMap(this.tags);
  }

  public void addTag(String key, String value) {
    this.tags.put(key, value);
  }

  public void addTags(Map<String, Object> map) {
    this.tags.putAll(map);
  }

  public Map<String, Object> toMybatisMap() {
    Map<String, Object> map = new HashMap<>(8);
    map.put("data_center", dataCenter);
    map.put("ip", ip);
    map.put("host_name", hostName);
    map.put("port", port);
    map.put("process_no", processNo);
    map.put("tags", JSON.toJSONString(tags));
    map.put("reg_time", regTime);
    map.put("app_name", app.getName());
    return map;
  }
}
