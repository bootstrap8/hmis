package com.github.hbq.event.kafka;

import java.util.UUID;

/**
 * @author hbq
 */
public class IDBox {

  public final static String GID = uid();

  public synchronized static String uid() {
    return UUID.randomUUID().toString().replaceAll("-", "");
  }
}
