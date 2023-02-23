package com.github.hbq.config.pojo;

import java.io.UnsupportedEncodingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

@Slf4j
public class LeafBean implements Comparable<LeafBean> {

  private String path;
  private String name;
  private byte[] value;
  private String strValue;

  public LeafBean(String path, String name, byte[] value) {
    super();
    this.path = path;
    this.name = name;
    this.value = value;
  }

  public boolean containKey(String path, String name, String value) {
    boolean check = true;
    if (!StringUtils.contains(this.path, path)) {
      check = false;
    }
    if (!StringUtils.contains(this.name, name)) {
      check = false;
    }
    String strValue = getStrValue();
    if (!StringUtils.contains(strValue, value)) {
      check = false;
    }
    return check;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public byte[] getValue() {
    return value;
  }

  public void setValue(byte[] value) {
    this.value = value;
  }

  public String getStrValue() {
    if (this.value == null) {
      return this.strValue;
    }
    try {
      this.strValue = new String(this.value, "UTF-8");
      return this.strValue;
    } catch (UnsupportedEncodingException ex) {
      log.error("", ex);
    }
    return null;
  }

  public void setStrValue(String strValue) {
    this.strValue = strValue;
  }

  public LeafBean tranHtml() {
    if (this.strValue == null) {
      getStrValue();
    }
    if (this.strValue != null) {
      this.strValue = this.strValue.replaceAll(">", "&gt;").replaceAll("<", "&lt;").replaceAll("\"", "&quot;");
    }
    return this;
  }

  @Override
  public int compareTo(LeafBean o) {
    return (this.path + this.name).compareTo((o.path + o.name));
  }
}
