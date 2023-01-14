package com.github.hbq.agent.app.pojo;

/**
 * @author hbq
 */
public class QuotaInfo {

  private AppInfo appInfo;
  private String name;
  private String desc;
  private CycleInfo cycleInfo = CycleInfo.SECOND30;
  private Type type = Type.Data;
  private String key;

  public QuotaInfo(AppInfo appInfo, String name, String desc) {
    this.appInfo = appInfo;
    this.name = name;
    this.desc = desc;
  }

  public QuotaInfo(AppInfo appInfo, String name, String desc, CycleInfo cycleInfo, Type type) {
    this(appInfo, name, desc);
    this.cycleInfo = cycleInfo;
    this.type = type;
    this.key = String.join(",", appInfo.getKey(), name, cycleInfo.getKey(), type.name());
  }

  public enum Type {
    Data {
      @Override
      public boolean isMe(Type t) {
        return t == Data;
      }
    },
    Notify {
      @Override
      public boolean isMe(Type t) {
        return t == Notify;
      }
    },
    Heartbeat {
      @Override
      public boolean isMe(Type t) {
        return t == Heartbeat;
      }
    };

    public boolean isMe(Type t) {
      throw new AbstractMethodError();
    }
  }

  public AppInfo getAppInfo() {
    return appInfo;
  }

  public String getName() {
    return name;
  }

  public String getDesc() {
    return desc;
  }

  public CycleInfo getCycleInfo() {
    return cycleInfo;
  }

  public Type getType() {
    return type;
  }
}
