package com.github.hbq.manage.config.serv.impl;

import com.github.hbq.common.utils.Count;
import com.github.hbq.manage.config.pojo.LeafBean;
import com.github.hbq.manage.config.pojo.ZKNode;
import com.github.hbq.manage.config.serv.NodeService;
import com.github.hbq.manage.config.serv.ZookeeperService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author hbq
 */
@Service("zkui-service-NodeServiceImpl")
@Slf4j
public class NodeServiceImpl implements NodeService {

  @Autowired
  private ZookeeperService zookeeperService;

  @Override
  public Map queryNodes(Map map) {
    String zkPath = MapUtils.getString(map, "zkPath");
    Map result = new HashMap();
    try {
      ZKNode zkNode = zookeeperService.listNodeEntries(zkPath, "ADMIN");
      result.put("tree", zkNode.createTree());
      result.put("leaf", zkNode.getLeafBeanLSt());
    } catch (Exception e) {
      log.error("", e);
    }
    return result;
  }

  @Override
  public void setPropertyValue(Map map) {
    String zkPath = MapUtils.getString(map, "zkPath");
    String name = MapUtils.getString(map, "name");
    String value = MapUtils.getString(map, "value");
    try {
      zookeeperService.setPropertyValue(zkPath, name, value);
    } catch (Exception e) {
      log.error("", e);
      throw new RuntimeException(e.getCause());
    }
  }

  @Override
  public void deleteLeaves(Map map) {
    String zkPath = MapUtils.getString(map, "zkPath");
    List<String> leafNames = (List<String>) MapUtils.getObject(map, "leafNames");
    List<String> list = leafNames.stream().map(name -> {
      if ("/".equals(zkPath)) {
        return String.join("", zkPath, name);
      } else {
        return String.join("/", zkPath, name);
      }
    }).collect(Collectors.toList());
    try {
      zookeeperService.deleteLeaves(list);
    } catch (Exception e) {
      log.error("", e);
      throw new RuntimeException(e.getCause());
    }
  }

  @Override
  public void createFolder(Map map) {
    String zkPath = MapUtils.getString(map, "zkPath");
    String folderName = MapUtils.getString(map, "folderName");
    zkPath = zkPath.endsWith("/") ? zkPath + folderName : zkPath + "/" + folderName;
    try {
      zookeeperService.createFolder(zkPath,
          "email", "hbq969@gmail.com");
    } catch (Exception e) {
      log.error("", e);
      throw new RuntimeException(e.getCause());
    }
  }

  @Override
  public void createNode(Map map) {
    String zkPath = MapUtils.getString(map, "zkPath");
    String name = MapUtils.getString(map, "propertyName");
    String value = MapUtils.getString(map, "propertyValue");
    try {
      zookeeperService.createNode(zkPath, name, value);
    } catch (Exception e) {
      log.error("", e);
      throw new RuntimeException(e.getCause());
    }
  }

  @Override
  public void delete(Map map) {
    List<String> leafNames = (List<String>) MapUtils.getObject(map, "leafNames");
    try {
      zookeeperService.deleteLeaves(leafNames);
    } catch (Exception e) {
      log.error("", e);
      throw new RuntimeException(e.getCause());
    }
    List<String> folderNames = (List<String>) MapUtils.getObject(map, "folderNames");
    try {
      zookeeperService.deleteFolders(folderNames);
    } catch (Exception e) {
      log.error("", e);
      throw new RuntimeException(e.getCause());
    }
  }

  @Override
  public Set<LeafBean> exportTree(Map map) {
    String zkPath = MapUtils.getString(map, "zkPath");
    Set<LeafBean> set = null;
    try {
      set = zookeeperService.exportTree(zkPath);
    } catch (Exception e) {
      log.error("", e);
      throw new RuntimeException(e.getCause());
    }
    return set;
  }

  @Override
  public void importData(MultipartFile file, boolean overwrite) {
    try {
      List<String> lines = IOUtils.readLines(file.getInputStream(), "utf-8");
      List<String> importFile = new ArrayList<>(lines.size());
      Count count = Count.unsafe();
      for (String inputLine : lines) {
        count.incrementAndGet();
        // Empty or comment?
        if (inputLine.trim().equals("") || inputLine.trim().startsWith("#")) {
          continue;
        }
        if (inputLine.startsWith("-")) {
          //DO nothing.
        } else if (!inputLine.matches("/.+=.+=.*")) {
          throw new IOException("Invalid format at line " + count.intValue() + ": " + inputLine);
        }
        importFile.add(inputLine);
      }
      zookeeperService.importData(importFile, overwrite);
    } catch (Exception e) {
      log.error("", e);
      throw new RuntimeException(e.getCause());
    }
  }
}
