package com.github.hbq.common.spring.context;

import com.alibaba.fastjson.JSON;
import java.io.Serializable;
import java.util.Optional;

/**
 * @author hbq
 */
public class UserInfo implements Serializable {

  private static final long serialVersionUID = -1;

  private String userName;

  public UserInfo() {
  }

  public UserInfo(String userName) {
    this.userName = userName;
  }

  public static UserInfo of(String userInfo) {

    return null == userInfo ? new UserInfo()
        : JSON.parseObject(userInfo, UserInfo.class);
  }

  public String getDefaultUserName(String defaultValue) {
    return Optional.ofNullable(userName).orElse(defaultValue);
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }
}
