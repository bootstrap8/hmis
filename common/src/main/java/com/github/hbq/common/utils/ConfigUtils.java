package com.github.hbq.common.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.util.ResourceUtils;

/**
 * @author hbq
 */
@Slf4j
public class ConfigUtils {

  public static final String SPRING_PROFILE_ACTIVE_DEFAULT = "default";

  private String module = "";

  public ConfigUtils() {
  }

  public static ConfigUtils of() {
    return new ConfigUtils();
  }

  public static ConfigUtils of(String module) {
    ConfigUtils utils = new ConfigUtils();
    utils.module = module;
    return utils;
  }

  public void build() throws Exception {
    build("default");
  }

  public void build(String profile) throws Exception {
    String appName = null;
    try {
      ResourceUtils.getFile(String.format("classpath:bootstrap-%s.properties", profile));
      appName = getPropertyValue(String.format("/bootstrap-%s.properties", profile), "spring.application.name");
      if (appName == null) {
        appName = getPropertyValue("/bootstrap.properties", "spring.application.name");
      }
    } catch (FileNotFoundException e) {
      appName = getPropertyValue("/bootstrap.properties", "spring.application.name");
    }
    if (appName != null) {
      build(profile, appName);
    }
  }

  public void build(String profile, String appName) throws Exception {
    build(profile, appName, null);
  }


  public void build(String profile, String appName, Map<String, String> covers) throws Exception {
    String path = System.getProperty("user.dir") + "/" + module
        + String.format("/src/main/resources/%s%s.txt", appName, ("default".equals(profile) ? "" : "-" + profile));
    String decodedPath = URLDecoder.decode(path, Charset.defaultCharset().displayName());
    FileOutputStream out = new FileOutputStream(decodedPath);
    boolean isDefault = "default".equals(profile);
    String bootstrapFile = "bootstrap.properties";
    if (!isDefault) {
      bootstrapFile = String.format("bootstrap-%s.properties", profile);
      File file = null;
      try {
        file = ResourceUtils.getFile(String.format("classpath:%s", bootstrapFile));
      } catch (FileNotFoundException e) {
      }
      if (file == null || !file.exists()) {
        bootstrapFile = "bootstrap.properties";
      }
    }
    String zkRootPath = getPropertyValue("/" + bootstrapFile, "spring.cloud.zookeeper.config.root");
    List<String> fileNames = Lists.newArrayList();
    if (SPRING_PROFILE_ACTIVE_DEFAULT.equals(profile)) {
      fileNames.add("application.properties");
      profile = null;
    } else {
      fileNames.add("application.properties");
      fileNames.add("application-" + profile + ".properties");
    }
    profile = (profile == null ? "" : "," + profile);
    Map<String, String> lines = Maps.newLinkedHashMap();
    for (String fileName : fileNames) {
      String filePath = ConfigUtils.class.getResource("/" + fileName).getFile();
      String decodedFilePath = URLDecoder.decode(filePath, Charset.defaultCharset().displayName());
      File file = new File(decodedFilePath);
      List<String> props = IOUtils.readLines(new FileInputStream(file), Charset.defaultCharset());
      for (String prop : props) {
        if (prop == null || "".equals(prop.trim()) || prop.trim().startsWith("#")) {
          continue;
        }
        int idx = prop.indexOf('=');
        String key = prop.substring(0, idx);
        String val = prop.substring(idx);
        if (covers != null && covers.containsKey(key)) {
          lines.put(zkRootPath + "/" + appName + profile + "=" + key, "=" + covers.get(key));
        } else {
          lines.put(zkRootPath + "/" + appName + profile + "=" + key, val);
        }
      }
    }
    List<String> list = Lists.newArrayList();
    for (Map.Entry<String, String> entry : lines.entrySet()) {
      list.add(entry.getKey() + entry.getValue());
    }
    try {
      IOUtils.writeLines(list, "\n", out, Charset.defaultCharset());
    } finally {
      IOUtils.close(out);
    }
  }

  private static String getPropertyValue(String resFile, String key) throws IOException {
    Properties map = new Properties();
    map.load(ConfigUtils.class.getResourceAsStream(resFile));
    return (String) map.get(key);
  }

}
