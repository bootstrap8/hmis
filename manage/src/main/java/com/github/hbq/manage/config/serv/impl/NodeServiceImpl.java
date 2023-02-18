package com.github.hbq.manage.config.serv.impl;

import com.github.hbq.common.spring.context.SpringContext;
import com.github.hbq.common.spring.context.UserInfo;
import com.github.hbq.common.utils.Count;
import com.github.hbq.common.utils.FormatTime;
import com.github.hbq.manage.config.dao.ConfigDao;
import com.github.hbq.manage.config.pojo.HistoryOperate;
import com.github.hbq.manage.config.pojo.LeafBean;
import com.github.hbq.manage.config.pojo.ZKNode;
import com.github.hbq.manage.config.serv.NodeService;
import com.github.hbq.manage.config.serv.ZookeeperService;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.ibatis.session.RowBounds;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author hbq
 */
@Service("zkui-service-NodeServiceImpl")
@Slf4j
public class NodeServiceImpl implements NodeService, InitializingBean {

  public static final String ADMIN = "admin";

  @Autowired
  private ZookeeperService zookeeperService;

  @Autowired
  private ConfigDao configDao;

  @Autowired
  private SpringContext context;

  @Override
  public void afterPropertiesSet() throws Exception {
    createConfigHistory();
  }

  @Override
  public Map queryNodes(UserInfo ui, Map map) {
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
  public void setPropertyValue(UserInfo ui, Map map) {
    String zkPath = MapUtils.getString(map, "zkPath");
    String name = MapUtils.getString(map, "name");
    String value = MapUtils.getString(map, "value");
    try {
      zookeeperService.setPropertyValue(zkPath, name, value);
    } catch (Exception e) {
      log.error("", e);
      throw new RuntimeException(e.getCause());
    }
    configDao.saveHistoryOperate(new HistoryOperate(ui.getDefaultUserName(ADMIN), "更新配置"));
  }

  @Override
  public void deleteLeaves(UserInfo ui, Map map) {
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
    configDao.saveHistoryOperate(new HistoryOperate(ui.getDefaultUserName(ADMIN), "删除属性"));
  }

  @Override
  public void createFolder(UserInfo ui, Map map) {
    String zkPath = MapUtils.getString(map, "zkPath");
    String folderName = MapUtils.getString(map, "folderName");
    zkPath = zkPath.endsWith("/") ? zkPath + folderName : zkPath + "/" + folderName;
    try {
      zookeeperService.createFolder(zkPath,
          "email", "hbq969@gmail.com");
    } catch (Exception e) {
      if (e instanceof NodeExistsException) {
        log.error("目录已存在");
        throw new RuntimeException("目录已存在");
      } else {
        log.error("", e);
        throw new RuntimeException(e.getCause());
      }
    }
    configDao.saveHistoryOperate(new HistoryOperate(ui.getDefaultUserName(ADMIN), "创建目录"));
  }

  @Override
  public void createNode(UserInfo ui, Map map) {
    String zkPath = MapUtils.getString(map, "zkPath");
    String name = MapUtils.getString(map, "propertyName");
    String value = MapUtils.getString(map, "propertyValue");
    try {
      zookeeperService.createNode(zkPath, name, value);
    } catch (Exception e) {
      if (e instanceof NodeExistsException) {
        log.error("路径已存在");
        throw new RuntimeException("路径已存在");
      } else {
        log.error("", e);
        throw new RuntimeException(e.getCause());
      }
    }
    configDao.saveHistoryOperate(new HistoryOperate(ui.getDefaultUserName(ADMIN), "创建配置"));
  }

  @Override
  public void delete(UserInfo ui, Map map) {
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
    configDao.saveHistoryOperate(new HistoryOperate(ui.getDefaultUserName(ADMIN), "批量删除配置"));
  }

  @Override
  public Set<LeafBean> exportTree(UserInfo ui, Map map) {
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
  public synchronized void importData(UserInfo ui, MultipartFile file, boolean overwrite) {
    // FIXED 如果部署多个manage实例时，此方法要使用分布式锁
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
    configDao.saveHistoryOperate(new HistoryOperate(ui.getDefaultUserName(ADMIN), "导入配置"));
  }

  @Override
  public Set<LeafBean> searchTree(UserInfo ui, Map map) {
    String path = MapUtils.getString(map, "path");
    String name = MapUtils.getString(map, "name");
    String value = MapUtils.getString(map, "value");
    try {
      return zookeeperService.searchTree(path, name, value);
    } catch (Exception e) {
      log.error("", e);
      throw new RuntimeException(e.getCause());
    }
  }

  @Scheduled(cron = "${hbq.config.cron.clean-history-operate:0 0 0 * * *}")
  @Override
  public void cleanHistoryOperate() {
    try {
      long time = context.getLongValue("hbq.config.clean-history-operate.expire.time", 30);
      String unit = context.getProperty("hbq.config.clean-history-operate.expire.unit", "DAYS");
      long expire = TimeUnit.SECONDS.convert(time, TimeUnit.valueOf(unit));
      long opTime = FormatTime.nowSecs() - expire;
      configDao.deleteHistoryOperate(opTime);
      log.info("定时清理[{}]前的历史操作记录", FormatTime.YYYYMMDDHHMISS.withSecs(opTime));
    } catch (Exception e) {
      log.error("", e);
    }
  }

  @Override
  public List<HistoryOperate> queryHistoryOperates(Map map, int pageNum, int pageSize) {
    try {
      List<String> time = (List<String>) MapUtils.getObject(map, "time");
      String start = time.get(0);
      String end = time.get(1);
      map.put("startTime", DateUtils.parseDate(start, new String[]{"yyyy-MM-dd"}).getTime() / 1000L);
      map.put("endTime", DateUtils.parseDate(end, new String[]{"yyyy-MM-dd"}).getTime() / 1000L);
      log.info("查询历史记录参数: {}", map);
      return configDao.queryHistoryOperates(map, new RowBounds(pageNum, pageSize));
    } catch (Exception e) {
      log.error("", e);
      throw new RuntimeException(e.getCause());
    }
  }

  private void createConfigHistory() {
    try {
      configDao.createConfigHistory();
      log.info("创建配置中心操作记录表成功");
    } catch (Exception e) {
      log.info("配置中心操作记录表已存在");
    }
  }
}
