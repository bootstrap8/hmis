package com.github.hbq.common.spring.context;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

/**
 * @author hbq
 */
@Slf4j
public class SpringEnvImpl implements SpringEnv, InitializingBean {

  public static final String SPRING_PROFILES_ACTIVE = "spring.profiles.active";

  @Autowired
  private Environment environment;

  @Value("${spring.profiles.active:default}")
  private String profiles;

  private Map<String, Object> variables = Maps.newHashMapWithExpectedSize(256);

  private Set<String> varKeys = new LinkedHashSet<>(256);

  @Override
  public void afterPropertiesSet() throws Exception {
    if (environment != null) {

      AbstractEnvironment abstractEnvironment = AbstractEnvironment.class.cast(environment);
      MutablePropertySources mutablePropertySources = abstractEnvironment.getPropertySources();
      for (PropertySource<?> propertySource : mutablePropertySources) {
        if (propertySource instanceof EnumerablePropertySource) {
          EnumerablePropertySource source = EnumerablePropertySource.class.cast(propertySource);
          for (String propertyName : source.getPropertyNames()) {
            variables.put(propertyName, source.getProperty(propertyName));
          }
        }
      }
      if (!variables.containsKey(SPRING_PROFILES_ACTIVE)) {
        variables.put("spring.profiles.active", profiles);
      }
      if (log.isDebugEnabled()) {
        log.debug("Spring配置的所有属性: {}", variables);
      }
      List<String> keyList = new ArrayList<>(variables.size());
      variables.keySet().forEach(k -> keyList.add(k));
      Collections.sort(keyList);
      keyList.forEach(k -> varKeys.add(k));
    }
  }

  @Override
  public Map<String, Object> getEnvironmentVariables() {
    return Collections.unmodifiableMap(variables);
  }

  @Override
  public Set<String> getEnvironmentPropertyKeys() {
    return Collections.unmodifiableSet(varKeys);
  }
}
