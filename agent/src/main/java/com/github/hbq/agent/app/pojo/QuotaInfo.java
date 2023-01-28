package com.github.hbq.agent.app.pojo;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.Data;

/**
 * @author hbq
 */
@Data
public class QuotaInfo {

  private InstInfo instInfo;
  private String name;
  private String desc;
  private String unit;
  private CycleInfo cycleInfo = CycleInfo.SECOND30;
  private Type type = Type.Data;
  private String instQuotaKey;
  private String quotaKey;

  public QuotaInfo() {
  }

  public QuotaInfo(InstInfo instInfo, String name, String desc, String unit) {
    this.instInfo = instInfo;
    this.name = name;
    this.desc = desc;
    this.unit = unit;
  }

  public QuotaInfo(InstInfo instInfo, String name, String desc, String unit, CycleInfo cycleInfo, Type type) {
    this(instInfo, name, desc, unit);
    this.cycleInfo = cycleInfo;
    this.type = type;
    initInstQuotaKey();
    initQuotaKey();
  }

  private void initQuotaKey() {
    this.quotaKey = String.join(",", this.name, cycleInfo.getKey(), type.name());
  }

  private void initInstQuotaKey() {
    this.instQuotaKey = String.join(",", instInfo.getKey(), name, cycleInfo.getKey(), type.name());
  }

  public String getInstQuotaKey() {
    if (Objects.isNull(instQuotaKey)) {
      initInstQuotaKey();
    }
    return instQuotaKey;
  }

  public String getQuotaKey() {
    if (Objects.isNull(quotaKey)) {
      initQuotaKey();
    }
    return quotaKey;
  }

  public enum Type {
    Data,
    Notify,
    Heartbeat;

    public boolean isMe(Type t) {
      return t == this;
    }
  }

  public Map toMybatisMap() {
    Map<String, Object> map = new HashMap<>(8);
    map.put("app_name", instInfo.getApp().getName());
    map.put("data_center", instInfo.getDataCenter());
    map.put("ip", instInfo.getIp());
    map.put("port", instInfo.getPort());
    map.put("quota_name", name);
    map.put("quota_desc", desc);
    map.put("quota_unit", unit);
    map.put("quota_type", type.name());
    map.put("cycle_time", cycleInfo.getTime());
    map.put("cycle_unit", cycleInfo.getUnit().name());
    return map;
  }
}
