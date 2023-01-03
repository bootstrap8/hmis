package com.github.hbq.common.utils;

import java.util.UUID;

/**
 * @author hbq
 */
public final class UidBox {

  public static synchronized String uid() {
    return UUID.randomUUID().toString().replaceAll("-", "");
  }
}
