package com.github.hbq.common.help;

import java.util.Map;

/**
 * @author hbq
 */
public final class MDC {

  public final static void put(String key, String val) throws IllegalArgumentException {
    org.slf4j.MDC.put(key, val);
  }


  public final static String get(String key) throws IllegalArgumentException {
    String val = org.slf4j.MDC.get(key);
    org.slf4j.MDC.remove(key);
    return val;
  }

  public final static void clear() {
    org.slf4j.MDC.clear();
  }

  public final static Map<String, String> getCopyOfContextMap() {
    return org.slf4j.MDC.getCopyOfContextMap();
  }

  public final static void setContextMap(Map<String, String> contextMap) {
    org.slf4j.MDC.setContextMap(contextMap);
  }
}
