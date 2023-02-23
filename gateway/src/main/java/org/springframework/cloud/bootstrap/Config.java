package org.springframework.cloud.bootstrap;

import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.zookeeper.ConditionalOnZookeeperEnabled;
import org.springframework.cloud.zookeeper.ZookeeperProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

/**
 * @author hbq
 */
@ConditionalOnZookeeperEnabled
@BootstrapConfiguration
@Slf4j
public class Config {

  @Autowired(required = false)
  private ZookeeperProperties zookeeperProperties;

  @Autowired(required = false)
  private Environment environment;

  @Bean
  public CuratorFramework curatorFramework() throws Exception {

    log.info("自动装配配置中心认证加密功能");
    if (zookeeperProperties == null || environment == null) {
      return null;
    }

    String schema = environment.getProperty("spring.cloud.zookeeper.auth.schema");
    if (schema == null || "".equals(schema.trim())) {
      schema = "digest";
    }

    String authInfo = environment.getProperty("spring.cloud.zookeeper.auth.info");
    String secretKey = this.environment.getProperty("spring.cloud.zookeeper.auth.secky");

    CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder();

    builder.connectString(zookeeperProperties.getConnectString());

    builder.retryPolicy(new RetryNTimes(zookeeperProperties.getMaxRetries(), zookeeperProperties.getMaxSleepMs()));

    if (StringUtils.isNotBlank(authInfo)) {
      if (StringUtils.isNotBlank(secretKey)) {
        authInfo = decrypt(authInfo, secretKey);
      }
      builder.authorization(schema, authInfo.getBytes());
    }
    log.info("配置中心, 地址: {}, 重试策略: <{},{}>, 连接等待超时: <{},{}>",
        zookeeperProperties.getConnectString(), zookeeperProperties.getMaxRetries(),
        zookeeperProperties.getMaxSleepMs(), zookeeperProperties.getBlockUntilConnectedWait(),
        zookeeperProperties.getBlockUntilConnectedUnit());

    CuratorFramework curator = builder.build();
    curator.start();

    curator.blockUntilConnected(zookeeperProperties.getBlockUntilConnectedWait(),
        zookeeperProperties.getBlockUntilConnectedUnit());

    return curator;
  }

  public static String encrypt(String content, String encryptPass) {
    try {
      Cipher cipher = Cipher.getInstance("AES/GCM/PKCS5Padding");
      cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(encryptPass));
      byte[] iv = cipher.getIV();
      assert iv.length == 12;
      byte[] encryptData = cipher.doFinal(content.getBytes());
      assert encryptData.length == content.getBytes().length + 16;
      byte[] message = new byte[12 + content.getBytes().length + 16];
      System.arraycopy(iv, 0, message, 0, 12);
      System.arraycopy(encryptData, 0, message, 12, encryptData.length);
      return Base64.encodeBase64String(message);
    } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static String decrypt(String base64Content, String encryptPass) {
    byte[] content = Base64.decodeBase64(base64Content);
    if (content.length < 12 + 16) {
      throw new IllegalArgumentException();
    }
    GCMParameterSpec params = new GCMParameterSpec(128, content, 0, 12);
    try {
      Cipher cipher = Cipher.getInstance("AES/GCM/PKCS5Padding");
      cipher.init(Cipher.DECRYPT_MODE, getSecretKey(encryptPass), params);
      byte[] decryptData = cipher.doFinal(content, 12, content.length - 12);
      return new String(decryptData);
    } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
      e.printStackTrace();
    }
    return null;
  }

  private static SecretKeySpec getSecretKey(String encryptPass) throws NoSuchAlgorithmException {
    KeyGenerator kg = KeyGenerator.getInstance("AES");
    SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
    secureRandom.setSeed(encryptPass.getBytes(StandardCharsets.UTF_8));
    kg.init(256, secureRandom);
    SecretKey secretKey = kg.generateKey();
    return new SecretKeySpec(secretKey.getEncoded(), "AES");// 转换为AES专用密钥
  }

}
