package com.github.hbq.common.autoconfig;

import com.alibaba.fastjson.JSON;
import com.github.hbq.common.actuator.NetEndpoint;
import com.github.hbq.common.actuator.NetService;
import com.github.hbq.common.actuator.NetServiceImpl;
import com.github.hbq.common.actuator.ThreadStackEndpoint;
import com.github.hbq.common.dict.DictCtrl;
import com.github.hbq.common.dict.SelectImpl;
import com.github.hbq.common.dict.map.ListDictImpl;
import com.github.hbq.common.dict.map.MapDictImpl;
import com.github.hbq.common.ids.Snowflake;
import com.github.hbq.common.ids.SnowflakeCtrl;
import com.github.hbq.common.lock.Lock;
import com.github.hbq.common.lock.LockAspect;
import com.github.hbq.common.lock.LockManage;
import com.github.hbq.common.lock.redis.RedisLockImpl;
import com.github.hbq.common.schedule.RepairHelper;
import com.github.hbq.common.schedule.impl.RepairHelperImpl;
import com.github.hbq.common.schedule.impl.ScheduleProcessor;
import com.github.hbq.common.spring.boot.ctrl.VersionRegistrations;
import com.github.hbq.common.spring.boot.encrypt.advice.EncryptRequestBodyAdvice;
import com.github.hbq.common.spring.boot.encrypt.advice.EncryptResponseBodyAdvice;
import com.github.hbq.common.spring.boot.encrypt.config.AESConfig;
import com.github.hbq.common.spring.boot.encrypt.config.RSAConfig;
import com.github.hbq.common.spring.boot.encrypt.web.AESCtrl;
import com.github.hbq.common.spring.boot.encrypt.web.RSACtrl;
import com.github.hbq.common.spring.cache.CacheConfig;
import com.github.hbq.common.spring.cache.ParamsKeyGenerator;
import com.github.hbq.common.spring.cloud.CheckCtrl;
import com.github.hbq.common.spring.context.ProfileBox;
import com.github.hbq.common.spring.context.SpringContext;
import com.github.hbq.common.spring.context.SpringEnvImpl;
import com.github.hbq.common.spring.datasource.DynamicDataSource;
import com.github.hbq.common.spring.datasource.DynamicDataSourceAspect;
import com.github.hbq.common.spring.datasource.DynamicDataSourceBeanProcessor;
import com.github.hbq.common.utils.StrUtils;
import com.google.common.collect.ImmutableList;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.plugins.WebFluxRequestHandlerProvider;
import springfox.documentation.spring.web.plugins.WebMvcRequestHandlerProvider;

/**
 * @author hbq
 */
@Configuration
@Slf4j
@ComponentScan(basePackageClasses = {CheckCtrl.class, RSACtrl.class,
    AESCtrl.class, SnowflakeCtrl.class, DictCtrl.class})
public class HbqAutoConfiguration implements ApplicationContextAware {

  private ApplicationContext context;

  @Autowired
  private Environment environment;

  @ConditionalOnExpression("${hbq.common.swagger.enable:true}")
  @Bean("hbq-common-spring-boot-ctrl-Docket")
  Docket docket() {
    AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
    AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
    authorizationScopes[0] = authorizationScope;
    SecurityReference authorization = new SecurityReference("Authorization", authorizationScopes);

    ApiKey apiKey = new ApiKey("Authorization", "Authorization", "header");

    SecurityContext build = SecurityContext.builder().securityReferences(ImmutableList.of(authorization))
        .forPaths(PathSelectors.any()).build();

    return new Docket(DocumentationType.SWAGGER_2).groupName("default")
        .securitySchemes(ImmutableList.of(apiKey))
        .securityContexts(ImmutableList.of(build)).enable(true).select()
        .apis(RequestHandlerSelectors.any())
        .paths(PathSelectors.any()).build();
  }

  @ConditionalOnExpression("${hbq.common.swagger.enable:true}")
  @Bean("hbq-common-config-springfoxHandlerProviderBeanPostProcessor")
  public static BeanPostProcessor springfoxHandlerProviderBeanPostProcessor() {
    return new BeanPostProcessor() {

      @Override
      public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof WebMvcRequestHandlerProvider || bean instanceof WebFluxRequestHandlerProvider) {
          customizeSpringfoxHandlerMappings(getHandlerMappings(bean));
        }
        return bean;
      }

      private <T extends RequestMappingInfoHandlerMapping> void customizeSpringfoxHandlerMappings(List<T> mappings) {
        List<T> copy = mappings.stream()
            .filter(mapping -> mapping.getPatternParser() == null)
            .collect(Collectors.toList());
        mappings.clear();
        mappings.addAll(copy);
      }

      @SuppressWarnings("unchecked")
      private List<RequestMappingInfoHandlerMapping> getHandlerMappings(Object bean) {
        try {
          Field field = ReflectionUtils.findField(bean.getClass(), "handlerMappings");
          field.setAccessible(true);
          return (List<RequestMappingInfoHandlerMapping>) field.get(bean);
        } catch (IllegalArgumentException | IllegalAccessException e) {
          throw new IllegalStateException(e);
        }
      }
    };
  }

  @Bean("hbq-common-spring-context-ProfileBox")
  ProfileBox profileBox() {
    return new ProfileBox();
  }

  @Bean("hbq-common-srping-context-SpringEnvImpl")
  SpringEnvImpl springEnvImpl() {
    return new SpringEnvImpl();
  }

  @Bean("hbq-common-spring-context-SpringContext")
  SpringContext context() {
    return new SpringContext(context, environment);
  }

  @Bean("hbq-common-actuator-NetServiceImpl")
  NetService netService() {
    return new NetServiceImpl();
  }

  @Bean("hbq-common-actuator-NetStatusEndpoint")
  NetEndpoint netEndpoint() {
    return new NetEndpoint();
  }

  @Bean("hbq-common-actuator-ThreadStackEndpoint")
  ThreadStackEndpoint threadStackEndpoint() {
    return new ThreadStackEndpoint();
  }

  @ConditionalOnExpression("${hbq.common.dynamic.datasource.enable:false}")
  @Bean("hbq-common-dynamic-DynamicDataSourceBeanProcessor")
  DynamicDataSourceBeanProcessor dynamicDataSourceBeanProcessor() {
    return new DynamicDataSourceBeanProcessor();
  }

  @ConditionalOnExpression("${hbq.common.dynamic.datasource.enable:false}")
  @Bean("hbq-common-dynamic-DynamicDataSourceAspect")
  DynamicDataSourceAspect dynamicDataSourceAspect() {
    return new DynamicDataSourceAspect();
  }

  @ConditionalOnExpression("${hbq.common.dynamic.datasource.enable:false}")
  @Bean("hbq-common-dynamic-DynamicDataSource")
  @Primary
  DataSource dynamicDataSource(SpringContext context) {

    Set<String> pKeys = context.getEnvironmentPropertyKeys();

    Set<String> dbKeySet = pKeys.stream().filter(k ->
    {
      boolean r = (k.startsWith("spring.datasource.") && k.endsWith(".url"));
      return r;
    }).map(k ->
    {
      int startIndex = "spring.datasource.".length();
      String dbKey = k.substring(startIndex, k.length() - ".url".length());
      return dbKey;
    }).collect(Collectors.toSet());
    log.info("解析到多数据源: {}", JSON.toJSONString(dbKeySet));
    if (!dbKeySet.contains("dbcp2")) {
      throw new UnsupportedOperationException("必须要提供默认的动态数据源[dbcp2]");
    }

    Map<Object, Object> dataSourceMap = new HashMap<>(dbKeySet.size());
    dbKeySet.forEach(dbKey -> dataSourceMap.put(dbKey, createBasicDataSource(context, dbKey)));

    DynamicDataSource dynamicDataSource = new DynamicDataSource();
    dynamicDataSource.setTargetDataSources(dataSourceMap);
    dynamicDataSource.setDefaultTargetDataSource(dataSourceMap.get("dbcp2"));

    return dynamicDataSource;
  }

  @ConditionalOnExpression("${hbq.common.swagger.enable:true}")
  @Bean("hbq-common-spring-boot-ctrl-WebMvcRegistrations")
  WebMvcRegistrations registrations() {
    return new VersionRegistrations();
  }

  @Bean("apiKeyGenerator")
  ParamsKeyGenerator keyGenerator() {
    return new ParamsKeyGenerator();
  }

  @Primary
  @Bean("hbq-common-spring-cache-CacheManager")
  @ConditionalOnExpression("${hbq.common.restful.cache.enable:true}")
  CacheConfig apiCacheConfig() {
    return new CacheConfig();
  }

  @Bean("jasyptStringEncryptor")
  StringEncryptor stringEncryptor() {
    PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
    SimpleStringPBEConfig config = new SimpleStringPBEConfig();
    String pwd = environment.getProperty("jasypt.encryptor.password", "hbq969@gmail.com");
    config.setPassword(pwd);
    config.setAlgorithm("PBEWITHHMACSHA512ANDAES_256");
    config.setKeyObtentionIterations("1000");
    config.setPoolSize("1");
    config.setProviderName("SunJCE");
    config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
    config.setIvGeneratorClassName("org.jasypt.iv.RandomIvGenerator");
    config.setStringOutputType("base64");
    encryptor.setConfig(config);
    return encryptor;
  }

  @Bean("hbq-common-spring-encrypt-RSAConfig")
  RSAConfig encryptConfig() {
    return new RSAConfig();
  }

  @Bean("hbq-common-spring-encrypt-AESConfig")
  AESConfig aesConfig() {
    return new AESConfig();
  }

  @ConditionalOnExpression("${hbq.common.restful.encrypt.aes.enable:false} || ${hbq.common.restful.encrypt.rsa.enable:false}")
  @Bean("hbq-common-spring-encrypt-EncryptRequestBodyAdvice")
  EncryptRequestBodyAdvice encryptRequestBodyAdvice(RSAConfig rsaConfig, AESConfig aesConfig) {
    return new EncryptRequestBodyAdvice();
  }

  @ConditionalOnExpression("${hbq.common.restful.encrypt.aes.enable:false} || ${hbq.common.restful.encrypt.rsa.enable:false}")
  @Bean("hbq-common-spring-encrypt-EncryptResponseBodyAdvice")
  EncryptResponseBodyAdvice encryptResponseBodyAdvice(RSAConfig rsaConfig, AESConfig aesConfig) {
    return new EncryptResponseBodyAdvice();
  }

  @Bean("hbq-common-ids-SnowFlake")
  Snowflake snowFlake() {
    return new Snowflake();
  }

  @ConditionalOnExpression("${hbq.common.lock.redis.enable:false}")
  @Bean("hbq-common-lock-redis-RedisLockImpl")
  Lock redisLockImpl() {
    return new RedisLockImpl();
  }

  @ConditionalOnExpression("${hbq.common.lock.redis.enable:false}")
  @Bean("hbq-common-lock-LockManage")
  LockManage lockManage() {
    return new LockManage();
  }

  @ConditionalOnExpression("${hbq.common.lock.redis.enable:false}")
  @Bean("hbq-common-lock-LockAspect")
  LockAspect lockAspect() {
    return new LockAspect();
  }

  @Bean("hbq-common-schedule-ScheduleProcessor")
  ScheduleProcessor scheduleProcessor() {
    return new ScheduleProcessor();
  }

  @Bean("hbq-common-schedule-RepairHelperImpl")
  RepairHelper repairService() {
    return new RepairHelperImpl();
  }

  @ConditionalOnExpression("${hbq.common.dict.enable:false}")
  @Bean("hbq-common-dict-MapDictImpl")
  MapDictImpl mapDictImpl() {
    return new MapDictImpl();
  }

  @ConditionalOnExpression("${hbq.common.dict.enable:false}")
  @Bean("hbq-common-dict-ListDictImpl")
  ListDictImpl listDictImpl() {
    return new ListDictImpl();
  }

  @ConditionalOnExpression("${hbq.common.dict.enable:false}")
  @Bean("hbq-common-dict-SelectImpl")
  SelectImpl selectImpl() {
    return new SelectImpl();
  }

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    this.context = applicationContext;
  }

  private BasicDataSource createBasicDataSource(SpringContext context, String dbKey) {
    String driverClassName = context.getProperty(String.join(".", "spring.datasource", dbKey, "driver-class-name"));
    String url = context.getProperty(String.join(".", "spring.datasource", dbKey, "url"));
    String user = context.getProperty(String.join(".", "spring.datasource", dbKey, "username"));
    String pwd = context.getProperty(String.join(".", "spring.datasource", dbKey, "password"));
    String total = context.getProperty(String.join(".", "spring.datasource", dbKey, "max-total"), "10");
    String idle = context.getProperty(String.join(".", "spring.datasource", dbKey, "max-idle"), "5");
    String waitMills = context
        .getProperty(String.join(".", "spring.datasource", dbKey, "max-wait-millis"), "300000");
    String testSql = context.getProperty(String.join(".", "spring.datasource", dbKey, "validation-query"));
    if (StrUtils.strEmpty(testSql)) {
      testSql = driverClassName.contains("oracle") ? "select 1 from dual" : "select 1";
    }
    log.info("构建动态数据源: {}, 驱动类: {}, url: {}, 账号: {}, 密码: {}, 连接总数: {}, 空闲上限: {}, 等待超时: {} ms, 测试sql: {}", dbKey,
        driverClassName, url, StrUtils.desensitive(user, 1), StrUtils.desensitive(pwd, 1), total, idle, waitMills,
        testSql);
    BasicDataSource ds = new BasicDataSource();
    ds.setDriverClassName(driverClassName);
    ds.setUrl(url);
    ds.setUsername(user);
    ds.setPassword(pwd);
    ds.setMaxTotal(Integer.valueOf(total));
    ds.setMaxIdle(Integer.valueOf(idle));
    ds.setMaxWaitMillis(Long.valueOf(waitMills));
    ds.setValidationQuery(testSql);
    return ds;
  }

}
