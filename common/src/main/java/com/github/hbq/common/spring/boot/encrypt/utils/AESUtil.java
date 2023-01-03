package com.github.hbq.common.spring.boot.encrypt.utils;

import java.nio.charset.Charset;
import java.util.Objects;
import java.util.UUID;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

/**
 * @author hbq
 */
@Slf4j
public class AESUtil {

  public final static String KEY = "392D2FFC865C43D5B5B4988307D4C808"; //长度必须为16、24、32位，即128bit、192bit、256bit

  private final static Charset CHARSET = Charset.forName("utf-8");

  private final static int OFFSET = 16;

  private final static String TRANSFORMATION = "AES/CBC/PKCS5Padding";

  private final static String ALGORITHM = "AES";

  public static String encrypt(String content) {
    return encrypt(content, KEY, CHARSET);
  }

  public static String encrypt(String content, Charset charset) {
    return encrypt(content, KEY, charset);
  }

  public static String encrypt(String content, String key) {
    return encrypt(content, key, CHARSET);
  }

  public static String decrypt(String content) {
    return decrypt(content, KEY, CHARSET);
  }

  public static String decrypt(String content, String key) {
    return decrypt(content, key, CHARSET);
  }

  public static String decrypt(String content, Charset charset) {
    return decrypt(content, KEY, charset);
  }

  public static String encrypt(String content, String key, Charset charset) {
    if (StringUtils.isBlank(content) || StringUtils.isBlank(key) || Objects.isNull(charset)) {
      throw new IllegalArgumentException("content, key, charset 为空,请检查");
    }
    try {
      SecretKeySpec skey = new SecretKeySpec(key.getBytes(), ALGORITHM);
      IvParameterSpec iv = new IvParameterSpec(key.getBytes(), 0, OFFSET);
      Cipher cipher = Cipher.getInstance(TRANSFORMATION);
      byte[] byteContent = content.getBytes(charset.name());
      cipher.init(Cipher.ENCRYPT_MODE, skey, iv);// 初始化
      byte[] result = cipher.doFinal(byteContent);
      return Base64Util.encode(result);
    } catch (Exception e) {
      log.error("", e);
    }
    return null;
  }

  public static String decrypt(String content, String key, Charset charset) {
    if (StringUtils.isBlank(content) || StringUtils.isBlank(key) || Objects.isNull(charset)) {
      throw new IllegalArgumentException("content, key, charset 为空,请检查");
    }
    try {
      SecretKeySpec skey = new SecretKeySpec(key.getBytes(), ALGORITHM);
      IvParameterSpec iv = new IvParameterSpec(key.getBytes(), 0, OFFSET);
      Cipher cipher = Cipher.getInstance(TRANSFORMATION);
      cipher.init(Cipher.DECRYPT_MODE, skey, iv);// 初始化
      byte[] result = cipher.doFinal(Base64Util.decode(content));
      return new String(result, charset); // 解密
    } catch (Exception e) {
      log.error("", e);
    }
    return null;
  }

  public final static String randomKeyWithAES(Integer len) {
    String uuid = UUID.randomUUID().toString().toUpperCase().replaceAll("-", "");
    if (len == null) {
      return uuid.substring(0, 16);
    }
    if (len > 32) {
      return uuid;
    } else {
      return uuid.substring(0, len);
    }
  }
}
