package com.github.hbq.common.utils;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

/**
 * @author hbq
 */
@Slf4j
public class ResourceUtils {

  public static final String PROFILE_DEFAULT = "default";

  public static final String BIN_VM = "/bin/vm";
  public static final String BIN_DOCKER = "/bin/docker";

  public static InputStream read(String dir, String file, String profile) {
    dir = (dir.endsWith("/") ? dir : String.join("", dir, "/"));
    String path;
    if (StrUtils.strEmpty(profile) || PROFILE_DEFAULT.equals(profile)) {
      path = String.join("", dir, file);
      log.info("读取文件: {}", path);
      return ResourceUtils.class.getResourceAsStream(path);
    } else {
      String[] array = file.split("\\.");
      if (array.length != 2) {
        throw new IllegalArgumentException();
      }
      path = String.join("", dir, array[0], "-", profile, ".", array[1]);
      InputStream in = ResourceUtils.class.getResourceAsStream(path);
      if (null == in) {
        path = String.join("", dir, file);
        log.info("读取文件: {}", path);
        return ResourceUtils.class.getResourceAsStream(path);
      } else {
        log.info("读取文件: {}", path);
        return in;
      }
    }
  }

  public static List<String> lines(String dir, String file, String profile) {
    try (InputStream in = read(dir, file, profile)) {
      return IOUtils.readLines(in, "utf-8");
    } catch (Exception e) {
      log.error(String.format("读取脚本文件[%s%s]异常", dir, file), e);
      return Collections.emptyList();
    }
  }

  public static String getPathWithUserHome(String path) {
    String currentDir = System.getProperty("user.dir");
    if (currentDir.contains(BIN_VM)) {
      currentDir = StrUtils.replaceWithQuote(currentDir, BIN_VM, "");
    }
    if (currentDir.contains(BIN_DOCKER)) {
      currentDir = StrUtils.replaceWithQuote(currentDir, BIN_DOCKER, "");
    }
    if (currentDir.endsWith(File.separator)) {
      currentDir = currentDir.substring(0, currentDir.length() - 1);
    }
    if (StrUtils.strNotEmpty(path)) {
      currentDir = String.join(path.startsWith(File.separator) ? "" : File.separator, currentDir, path);
    }
    return currentDir;
  }

}
