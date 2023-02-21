package com.github.hbq.common.lock.redis;

import com.alibaba.fastjson.JSON;
import com.github.hbq.common.lock.Lock;
import com.github.hbq.common.lock.LockManage;
import com.github.hbq.common.spring.context.SpringContext;
import com.github.hbq.common.utils.StrUtils;
import com.google.common.base.Splitter;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author hbq
 */
@RefreshScope
@Slf4j
public class RedisLockImpl implements Lock, InitializingBean {

  public static final String UNLOCK_LUA;

  public static final String REDIS_STANDALONE_HOST = "hbq.common.lock.redis.host";

  public static final String REDIS_SENTINEL_MASTER = "hbq.common.lock.redis.sentinel.master";

  public static final String REDIS_CLUSTER_NODES = "hbq.common.lock.redis.cluster.nodes";

  public static final String REDIS_PWD = "hbq.common.lock.redis.password";

  public static final String REDIS_SENTINEL_PWD = "hbq.common.lock.redis.sentinel.password";

  public static final Charset UTF8 = Charset.forName("utf-8");

  /**
   * 释放锁脚本，原子操作
   */
  static {
    StringBuilder sb = new StringBuilder();
    sb.append("if redis.call(\"get\",KEYS[1]) == ARGV[1] ");
    sb.append("then ");
    sb.append("    return redis.call(\"del\",KEYS[1]) ");
    sb.append("else ");
    sb.append("    return 0 ");
    sb.append("end ");
    UNLOCK_LUA = sb.toString();
  }

  private volatile RedisTemplate<Object, Object> redis;

  @Autowired
  private SpringContext context;

  @Autowired
  private LockManage lockManage;

  @EventListener
  public void envListener(EnvironmentChangeEvent event) {
    Set<String> set = event.getKeys();
    for (String key : set) {
      if (key.contains("hbq.common.lock.redis")) {
        try {
          log.info("分布式锁配置发生变化，重新初始化redis。{}", set);
          afterPropertiesSet();
        } catch (Exception e) {
        }
        break;
      }
    }
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (context.getEnvironment().containsProperty(REDIS_STANDALONE_HOST)) {
      initializeStandaloneRedis();
    } else if (context.getEnvironment().containsProperty(REDIS_SENTINEL_MASTER)) {
      initializeSentinelRedis();
    } else if (context.getEnvironment().containsProperty(REDIS_CLUSTER_NODES)) {
      initializeClusterRedis();
    } else {
      throw new UnsupportedOperationException("未配置redis, 不能启用分布式锁特性");
    }
    this.lockManage.registry("redis", this);
  }

  @Override
  public boolean tryLock(String lockKey, String requestId, long expire, TimeUnit unit) {
    checkInitializeRedis();
    try {
      RedisCallback<Boolean> callback = (connection) -> connection
          .set(lockKey.getBytes(Charset.forName("UTF-8")),
              requestId.getBytes(Charset.forName("UTF-8")),
              Expiration.milliseconds(unit.toMillis(expire))
              , RedisStringCommands.SetOption.SET_IF_ABSENT);
      return redis.execute(callback);
    } catch (Exception e) {
      String msg = String.format("尝试获取分布式锁异常(lockKey: %s, requestId: %s, expire: %s, unit: %s): %s",
          lockKey, requestId, expire, unit, e.getMessage());
      log.error(msg);
      if (e instanceof RuntimeException) {
        throw new RuntimeException(e.getCause());
      }
    }
    return false;
  }

  @Override
  public boolean getLock(String lockKey, String requestId, long expire, long checkTime, TimeUnit unit) {
    checkInitializeRedis();
    long allTime = 0;
    while (!tryLock(lockKey, requestId, expire, unit)) {
      try {
        unit.sleep(checkTime);
        allTime += checkTime;
        if (allTime >= expire) {
          return false;
        }
      } catch (InterruptedException e) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean getLock(String lockKey, String requestId, long expire, TimeUnit unit) {
    checkInitializeRedis();
    long expireSecs = TimeUnit.SECONDS.convert(expire, unit);
    long allTime = 0;
    while (!tryLock(lockKey, requestId, expire, unit)) {
      try {
        TimeUnit.SECONDS.sleep(1);
        if ((++allTime) >= expireSecs) {
          return false;
        }
      } catch (InterruptedException e) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean releaseLock(String lockKey, String requestId) {
    checkInitializeRedis();
    try {
      RedisCallback<Boolean> callback = (connection) -> connection
          .eval(UNLOCK_LUA.getBytes(), ReturnType.BOOLEAN, 1,
              lockKey.getBytes(Charset.forName("UTF-8")),
              requestId.getBytes(Charset.forName("UTF-8")));
      return redis.execute(callback);
    } catch (Exception e) {
      String msg = String.format("释放分布式锁异常(lockKey: %s, requestId: %s): %s",
          lockKey, requestId, e.getMessage());
      log.error(msg);
    }
    return false;
  }

  @Override
  public String get(String lockKey) {
    checkInitializeRedis();
    try {
      RedisCallback<String> callback = (connection) -> new String(
          connection.get(lockKey.getBytes()), Charset.forName("UTF-8"));
      return redis.execute(callback);
    } catch (Exception e) {
      String msg = String.format("获取分布式锁值异常(lockKey: %s): %s", lockKey, e.getMessage());
      log.error(msg);
      if (e instanceof RuntimeException) {
        throw new RuntimeException(e.getCause());
      }
    }
    return null;
  }

  private void checkInitializeRedis() {
    if (redis == null) {
      throw new UnsupportedOperationException("未启用redis自动装配,不支持分布式锁");
    }
  }

  private void initializeStandaloneRedis() {
    try {
      LettuceConnectionFactory factory = null;
      GenericObjectPoolConfig genericObjectPoolConfig = initializeGenericObjectPoolConfig();

      long readTimeout = Long.valueOf(context.getProperty("hbq.common.lock.redis.timeout", "5"));
      LettucePoolingClientConfiguration lettuceClientConfiguration = LettucePoolingClientConfiguration.builder()
          .commandTimeout(Duration.ofSeconds(readTimeout))
          .poolConfig(genericObjectPoolConfig).build();

      String ip = context.getProperty(REDIS_STANDALONE_HOST);
      int port = Integer.valueOf(context.getProperty("hbq.common.lock.redis.port", "6379"));
      RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(ip, port);
      String pwd = context.getProperty(REDIS_PWD);
      if (StrUtils.strNotEmpty(pwd)) {
        redisStandaloneConfiguration.setPassword(RedisPassword.of(pwd.toCharArray()));
      }
      log.info("初始化单点redis, 命令读超时: {} 秒, reids节点ip: {}, redis节点端口: {}, 密码: {}",
          readTimeout, ip, port, StrUtils.desensitive(pwd, 1));
      factory = initializeLettuceConnectionFactory(lettuceClientConfiguration, redisStandaloneConfiguration);

      initializeRedisTemplate(factory);
    } catch (Throwable e) {
      log.error("初始化单点redis异常", e);
    }
  }

  private void initializeSentinelRedis() {
    try {
      LettuceConnectionFactory factory = null;
      GenericObjectPoolConfig genericObjectPoolConfig = initializeGenericObjectPoolConfig();

      long readTimeout = Long.valueOf(context.getProperty("hbq.common.lock.redis.timeout", "5"));
      LettucePoolingClientConfiguration lettuceClientConfiguration = LettucePoolingClientConfiguration.builder()
          .commandTimeout(Duration.ofSeconds(readTimeout))
          .poolConfig(genericObjectPoolConfig).build();

      RedisSentinelConfiguration redisSentinelConfiguration = new RedisSentinelConfiguration();
      String master = context.getProperty(REDIS_SENTINEL_MASTER, "mymaster");
      redisSentinelConfiguration.setMaster(master);
      String servers = context.getProperty("hbq.common.lock.redis.sentinel.nodes");
      List<RedisNode> nodes = getRedisNodes(servers);
      for (RedisNode node : nodes) {
        redisSentinelConfiguration.addSentinel(node);
      }
      String pwd = context.getProperty(REDIS_SENTINEL_PWD);
      if (StrUtils.strNotEmpty(pwd)) {
        redisSentinelConfiguration.setPassword(RedisPassword.of(pwd.toCharArray()));
      }
      log.info("初始化哨兵redis, 命令读超时: {} 秒, master: {}, 哨兵集群列表: {}, 密码: {}",
          readTimeout, master, JSON.toJSONString(nodes), StrUtils.desensitive(pwd, 1));
      factory = initializeLettuceConnectionFactory(lettuceClientConfiguration, redisSentinelConfiguration);

      initializeRedisTemplate(factory);
    } catch (Throwable e) {
      log.error("初始化哨兵模式redis异常", e);
    }
  }

  private GenericObjectPoolConfig initializeGenericObjectPoolConfig() {
    GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
    genericObjectPoolConfig.setMaxIdle(Integer.valueOf(context.getProperty("hbq.common.lock.redis.lettuce.pool.max-idle", "8")));
    genericObjectPoolConfig.setMinIdle(Integer.valueOf(context.getProperty("hbq.common.lock.redis.lettuce.pool.min-idle", "0")));
    genericObjectPoolConfig.setMaxTotal(Integer.valueOf(context.getProperty("hbq.common.lock.redis.lettuce.pool.max-active", "20")));
    genericObjectPoolConfig.setMaxWaitMillis(Long.valueOf(context.getProperty("hbq.common.lock.redis.lettuce.pool.max-wait", "-1")));
    return genericObjectPoolConfig;
  }

  private void initializeClusterRedis() {
    try {
      LettuceConnectionFactory factory = null;
      GenericObjectPoolConfig genericObjectPoolConfig = initializeGenericObjectPoolConfig();

      long readTimeout = Long.valueOf(context.getProperty("common.lock.redis.timeout", "5"));
      LettucePoolingClientConfiguration lettuceClientConfiguration = LettucePoolingClientConfiguration.builder()
          .commandTimeout(Duration.ofSeconds(readTimeout))
          .poolConfig(genericObjectPoolConfig).build();

      RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
      String servers = context.getProperty(REDIS_CLUSTER_NODES);
      List<RedisNode> nodes = getRedisNodes(servers);
      for (RedisNode node : nodes) {
        redisClusterConfiguration.addClusterNode(node);
      }
      String pwd = context.getProperty(REDIS_PWD);
      if (StrUtils.strNotEmpty(pwd)) {
        redisClusterConfiguration.setPassword(RedisPassword.of(pwd.toCharArray()));
      }
      log.info("初始化集群redis, 命令读超时: {} 秒, 集群列表: {}, 密码: {}",
          readTimeout, JSON.toJSONString(nodes), StrUtils.desensitive(pwd, 1));
      factory = initializeLettuceConnectionFactory(lettuceClientConfiguration, redisClusterConfiguration);

      initializeRedisTemplate(factory);
    } catch (Throwable e) {
      log.error("初始化集群模式redis异常", e);
    }
  }

  private LettuceConnectionFactory initializeLettuceConnectionFactory(LettucePoolingClientConfiguration lettuceClientConfiguration,
      RedisConfiguration redisClusterConfiguration) {
    LettuceConnectionFactory factory;
    factory = new LettuceConnectionFactory(redisClusterConfiguration, lettuceClientConfiguration);
    factory.afterPropertiesSet();
    return factory;
  }

  private List<RedisNode> getRedisNodes(String servers) {
    return Splitter.on(",").splitToList(servers).stream().map(server -> {
      int lastIndex = server.lastIndexOf(":");
      String ip = server.substring(0, lastIndex);
      int port = Integer.valueOf(server.substring(lastIndex + 1));
      RedisNode node = new RedisNode(ip, port);
      return node;
    }).collect(Collectors.toList());
  }

  private void initializeRedisTemplate(LettuceConnectionFactory factory) {
    StringRedisSerializer serializer = new StringRedisSerializer();
    redis = new RedisTemplate<>();
    redis.setKeySerializer(serializer);
    redis.setValueSerializer(serializer);
    redis.setHashKeySerializer(serializer);
    redis.setHashValueSerializer(serializer);
    redis.setConnectionFactory(factory);
    redis.afterPropertiesSet();
  }
}
