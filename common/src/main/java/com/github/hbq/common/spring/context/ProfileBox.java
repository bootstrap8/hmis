package com.github.hbq.common.spring.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

/**
 * @author hbq
 */
@Slf4j
public class ProfileBox {

  public final static String PROFILE_DEFAULT = "default";

  @Value("${spring.profiles.active:default}")
  private String activeProfile;

  public boolean accept(String profile) {
    return activeProfile.equals(profile) || PROFILE_DEFAULT.equals(profile);
  }

  public String getActiveProfile() {
    return activeProfile;
  }
}
