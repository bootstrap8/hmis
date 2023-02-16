package com.github.hbq.common.spring.context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;

/**
 * @author hbq
 */
public class SpringContext {

  private ApplicationContext context;

  private Environment environment;

  @Autowired
  private SpringEnvImpl sei;

  public SpringContext(@NonNull ApplicationContext context, @NonNull Environment environment) {
    this.context = context;
    this.environment = environment;
  }

  public <T> T getBean(@NonNull Class<T> requiredType) {
    return this.context.getBean(requiredType);
  }

  public <T> T getBean(@NonNull String id, @NonNull Class<T> requiredType) {
    return requiredType.cast(this.context.getBean(id));
  }

  public <T> Map<String, T> getBeanMapOfType(@NonNull Class<T> requiredType) {
    return this.context.getBeansOfType(requiredType);
  }

  public <T> List<BeanInfo<T>> getBeanListOfType(@NonNull Class<T> requiredType) {
    Map<String, T> map = getBeanMapOfType(requiredType);
    if (map == null) {
      return Collections.emptyList();
    }
    List<BeanInfo<T>> list = new ArrayList<>(map.size());
    map.entrySet().forEach(e -> list.add(new BeanInfo<>(e.getKey(), e.getValue())));
    return list;
  }

  public <T> Optional<T> optional(String key, Class<T> valueType) {
    return Optional.ofNullable(this.environment.getProperty(key, valueType));
  }

  public String getProperty(String key) {
    return this.environment.getProperty(key);
  }

  public String getProperty(String key, String defaultValue) {
    return this.environment.getProperty(key, defaultValue);
  }

  public short getShortValue(String key, short defaultValue) {
    String v = this.environment.getProperty(key);
    return Objects.isNull(v) ? defaultValue : Short.valueOf(v).shortValue();
  }

  public int getIntValue(String key, int defaultValue) {
    String v = this.environment.getProperty(key);
    return Objects.isNull(v) ? defaultValue : Integer.valueOf(v).intValue();
  }

  public long getLongValue(String key, long defaultValue) {
    String v = this.environment.getProperty(key);
    return Objects.isNull(v) ? defaultValue : Long.valueOf(v).longValue();
  }

  public float getFloatValue(String key, float defaultValue) {
    String v = this.environment.getProperty(key);
    return Objects.isNull(v) ? defaultValue : Float.valueOf(v).floatValue();
  }

  public double getDoubleValue(String key, double defaultValue) {
    String v = this.environment.getProperty(key);
    return Objects.isNull(v) ? defaultValue : Double.valueOf(v).doubleValue();
  }

  public boolean getBoolValue(String key, boolean defaultValue) {
    String v = this.environment.getProperty(key);
    return Objects.isNull(v) ? defaultValue : Boolean.valueOf(v).booleanValue();
  }

  /**
   * 返回spring所有配置属性key，包括SystemProperties和系统env
   *
   * @return
   */
  public Set<String> getEnvironmentPropertyKeys() {
    return this.sei.getEnvironmentPropertyKeys();
  }

  public ApplicationContext getContext() {
    return context;
  }

  public Environment getEnvironment() {
    return environment;
  }
}
