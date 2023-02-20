package com.github.hbq.manage.config.serv.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.github.hbq.common.spring.context.SpringContext;
import com.github.hbq.common.spring.context.UserInfo;
import com.github.hbq.common.utils.Count;
import com.github.hbq.common.utils.FormatTime;
import com.github.hbq.common.utils.UidBox;
import com.github.hbq.manage.config.dao.ConfigDao;
import com.github.hbq.manage.config.pojo.Backup;
import com.github.hbq.manage.config.pojo.BackupDetail;
import com.github.hbq.manage.config.pojo.HistoryOperate;
import com.github.hbq.manage.config.pojo.LeafBean;
import com.github.hbq.manage.config.pojo.ZKNode;
import com.github.hbq.manage.config.serv.ConfigService;
import com.github.hbq.manage.config.serv.ZookeeperService;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
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
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.ibatis.session.RowBounds;
import org.apache.zookeeper.KeeperException.NodeExistsException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author hbq
 */
@Service("config-service-ConfigServiceImpl")
@Slf4j
public class ConfigServiceImpl implements ConfigService, InitializingBean {

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
    createBackupMain();
    createBackupDetail();
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
        if (StringUtils.isEmpty(inputLine) || inputLine.trim().startsWith("#")) {
          continue;
        }
        if (inputLine.trim().startsWith("-")) {
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

  @Scheduled(cron = "${hbq.config.clean-history-operate.cron:0 0 0 * * *}")
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

  @Override
  public void backup() {
    long eclapse = FormatTime.nowMills();
    Set<LeafBean> set = searchTree(UserInfo.of(null), ImmutableMap.of("path", "", "name", "", "value", ""));
    log.info("查询到需要备份的所有配置信息: {} 条", set.size());
    context.getOptionalBean(JdbcTemplate.class).ifPresent(jt -> {
      PlatformTransactionManager tranManager = new DataSourceTransactionManager(jt.getDataSource());
      DefaultTransactionDefinition def = new DefaultTransactionDefinition();
      def.setTimeout(60);
      TransactionStatus status = tranManager.getTransaction(def);
      try {
        long mills = FormatTime.nowMills();
        String id = String.join("_", FormatTime.YYYYMMDDHHMISS_NO_JOINER.withMills(mills), UidBox.uid());
        String sql = "insert into hbq_config_bk_main(id,bk_time,size) values(?,?,?)";
        log.info("保存备份记录: {}, ({}, {}, {})", sql, id, mills / 1000L, set.size());
        jt.update(sql, ps -> {
          ps.setString(1, id);
          ps.setLong(2, mills / 1000L);
          ps.setInt(3, set.size());
        });
        sql = "insert into hbq_config_bk_detail(id,path,name,value,bk_time) values(?,?,?,?,?)";
        log.info("保存备份记录: {}", sql);
        String fql = sql;
        CollectionUtil.split(set, 200).forEach(list -> {
          jt.batchUpdate(fql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
              LeafBean leaf = list.get(i);
              ps.setString(1, id);
              ps.setString(2, leaf.getPath());
              ps.setString(3, leaf.getName());
              String strValue = leaf.getStrValue();
              if (null == strValue) {
                ps.setNull(4, Types.VARCHAR);
              } else {
                ps.setString(4, strValue);
              }
              ps.setLong(5, mills / 1000L);
            }

            @Override
            public int getBatchSize() {
              return list.size();
            }
          });
          log.info("批量保存备份记录: {} 条", list.size());
        });
        tranManager.commit(status);
        long time = FormatTime.nowMills() - eclapse;
        log.info("备份成功: {}, 耗时: {} ms", id, time);
      } catch (Exception e) {
        log.info("备份异常", e);
        tranManager.rollback(status);
      }
    });

  }

  @Override
  public void recovery(Map map) {
    String id = MapUtils.getString(map, "id");
    List<BackupDetail> details = configDao.queryBackupDetails(id);
    log.info("读取备份数据[{}], {} 条", id, details.size());
    try {
      try {
        zookeeperService.deleteFolders(Lists.newArrayList("/"));
      } catch (Exception e) {
      }
      log.info("删除[/]下所有配置成功");
      List<String> importData = new ArrayList<>(details.size());
      for (BackupDetail detail : details) {
        importData.add(detail.getImportData());
      }
      zookeeperService.importData(importData, true);
      log.info("恢复[/]下所有配置成功， 恢复 {} 条", importData.size());
    } catch (Exception e) {
      log.error("", e);
      throw new RuntimeException(e.getCause());
    }
  }

  @Override
  public List<Backup> queryBackups(Map map, int pageNum, int pageSize) {
    List<String> time = (List<String>) MapUtils.getObject(map, "time");
    String start = time.get(0);
    String end = time.get(1);
    try {
      long startTime = DateUtils.parseDate(start, new String[]{"yyyy-MM-dd"}).getTime() / 1000L;
      long endTime = DateUtils.parseDate(end, new String[]{"yyyy-MM-dd"}).getTime() / 1000L;
      return configDao.queryBackups(startTime, endTime, new RowBounds(pageNum, pageSize));
    } catch (Exception e) {
      log.error("", e);
      throw new RuntimeException(e.getCause());
    }
  }

  @Override
  public void deleteBackupById(Map map) {
    String id = MapUtils.getString(map, "id");
    configDao.deleteBackupById(id);
    configDao.deleteBackupDetailById(id);
  }

  @Scheduled(cron = "${hbq.config.clean-backup.cron:0 0 0 * * *}")
  @Override
  public void cleanBackups() {
    try {
      long time = context.getLongValue("hbq.config.clean-backup.expire.time", 180);
      String unit = context.getProperty("hbq.config.clean-backup.expire.unit", "DAYS");
      long backTime = FormatTime.nowSecs() - TimeUnit.SECONDS.convert(time, TimeUnit.valueOf(unit));
      String fmtBackTime = FormatTime.YYYYMMDDHHMISS.withSecs(backTime);
      log.info("清理[]前的备份数据", fmtBackTime);
      configDao.deleteBackupByTime(backTime);
      configDao.deleteBackupDetailByTime(backTime);
      log.info("清理[]前的备份数据成功", fmtBackTime);
    } catch (Exception e) {
      log.error("", e);
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

  private void createBackupMain() {
    try {
      configDao.createBackupMain();
      log.info("创建备份数据主表成功");
    } catch (Exception e) {
      log.info("备份数据主表已存在");
    }
  }

  private void createBackupDetail() {
    try {
      configDao.createBackupDetail();
      log.info("创建备份数据明细表");
    } catch (Exception e) {
      log.error("备份数据明细表已存在");
    }
  }
}
