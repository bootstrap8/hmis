package com.github.hbq.monitor.serv.impl;

import com.github.hbq.agent.app.pojo.QuotaData;
import com.github.hbq.common.spring.context.SpringContext;
import com.github.hbq.common.utils.Count;
import com.github.hbq.common.utils.FormatTime;
import com.github.hbq.monitor.dao.MonitorDao;
import com.github.hbq.monitor.dao.QuotaDataDao;
import com.github.hbq.monitor.event.WarnRuleUpdateEvent;
import com.github.hbq.monitor.notify.proxy.NotifyProxy;
import com.github.hbq.monitor.pojo.QuotaAndRule;
import com.github.hbq.monitor.pojo.QuotaWarnRuleInfo;
import com.github.hbq.monitor.serv.MonitorService;
import com.google.common.collect.ImmutableMap;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.ibatis.session.RowBounds;
import org.h2.tools.Server;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author hbq
 */
@Service("monitor-service-MonitorServiceImpl")
@Slf4j
public class MonitorServiceImpl implements MonitorService, InitializingBean, DisposableBean {

  public static final String H2_DRIVER = "org.h2.Driver";

  @Value("${hbq.monitor.dialect.store.max-batch-size:200}")
  public int storeMaxBatchSize;
  @Value("${hbq.monitor.dialect.store.timeout-mills:5000}")
  public long storeTimeoutMills = 5000L;
  @Value("${spring.datasource.quota.driver-class-name:org.h2.Driver}")
  private String dialectDriver;
  @Value("${hbq.monitor.notify.type}")
  private String identifyNotify;
  @Value("${hbq.monitor.quota.data-reserve-days:7}")
  private int quotaReserveDays;

  @Autowired
  private MonitorDao monitorDao;
  @Autowired
  private QuotaDataDao quotaDataDao;
  @Autowired
  private SpringContext context;
  @Autowired
  private NotifyProxy notifyProxy;

  private org.h2.tools.Server server;
  private Count count = Count.safe();
  private List<QuotaData> tmp = new ArrayList<>(500);
  // 应用名+指标名作为map的key
  private volatile Map<String, QuotaWarnRuleInfo> configRules = new ConcurrentHashMap<>(500);
  // 实例key+指标key作为map的key
  private Map<String, QuotaAndRule> instQuotas = new ConcurrentHashMap<>(500);

  @Override
  public void afterPropertiesSet() throws Exception {
    createQuotaWarnRule();
    loadQuotaWarnRules(null);
    initQuotaDatabase();
  }

  @Override
  public void destroy() throws Exception {
    if (Objects.nonNull(server)) {
      server.shutdown();
      log.info("停止h2数据库");
    }
  }

  @Override
  public void saveQuotaData(List<QuotaData> qd) {
    tmp.addAll(qd);
    count.incrementAndGet(qd.size());
    if (count.compareAndInit(storeMaxBatchSize, storeTimeoutMills)) {
      try {
        quotaDataDao.saveQuotaData(tmp);
        log.info("批量入库指标数据: {}", tmp.size());
      } catch (Exception e) {
        log.error("批量入库指标数据异常", e);
      } finally {
        tmp.clear();
      }
    }
  }

  @Override
  public List<QuotaWarnRuleInfo> queryQuotaWarnRules(Map map, int pageNum, int pageSize) {
    return monitorDao.queryQuotaWarnRules(map, new RowBounds(pageNum, pageSize));
  }

  @Override
  public List<QuotaWarnRuleInfo> queryQuotaWarnRules(Map map) {
    return monitorDao.queryQuotaWarnRules(map);
  }

  @Override
  public void saveQuotaWarnRule(Map map) {
    String appName = MapUtils.getString(map, "appName");
    String quotaName = MapUtils.getString(map, "quotaName");
    map.put("updateTime", FormatTime.nowSecs());
    monitorDao.deleteQuotaWarnRule(appName, quotaName);
    monitorDao.saveQuotaWarnRule(map);
    loadQuotaWarnRules(new WarnRuleUpdateEvent("保存/修改通知规则"));
  }

  @Override
  public void deleteQuotaWarnRule(String appName, String quotaName) {
    monitorDao.deleteQuotaWarnRule(appName, quotaName);
    loadQuotaWarnRules(new WarnRuleUpdateEvent("删除应用通知规则"));
  }

  @Override
  public void deleteQuotaWarnRules(List<Map> list) {
    String appName;
    String quotaName;
    for (Map map : list) {
      appName = MapUtils.getString(map, "appName");
      quotaName = MapUtils.getString(map, "quotaName");
      monitorDao.deleteQuotaWarnRule(appName, quotaName);
    }
  }

  @Override
  public void updatePhoneNumsOnApp(String appName, String phoneNums) {
    monitorDao.updatePhoneNumsOnApp(appName, phoneNums, FormatTime.nowSecs());
    loadQuotaWarnRules(new WarnRuleUpdateEvent("更新应用通知规则的号码"));
  }

  @Override
  public void updatePhoneNumsOnAll(String phoneNums) {
    monitorDao.updatePhoneNumsOnAll(phoneNums, FormatTime.nowSecs());
    loadQuotaWarnRules(new WarnRuleUpdateEvent("更新通知规则的号码"));
  }

  @EventListener({WarnRuleUpdateEvent.class})
  @Override
  public void loadQuotaWarnRules(WarnRuleUpdateEvent event) {
    try {
      log.info("重载指标通知规则, {}", Objects.isNull(event) ? "" : event.getSource());
      List<QuotaWarnRuleInfo> allWarnRule = queryQuotaWarnRules(ImmutableMap.of("enable", -1));
      Map<String, QuotaWarnRuleInfo> _configRules = new ConcurrentHashMap<>();
      for (QuotaWarnRuleInfo warnRule : allWarnRule) {
        _configRules.put(warnRule.getKey(), warnRule);
      }
      this.configRules = _configRules;
    } catch (Exception e) {
      log.error("重载指标通知规则异常", e);
    }
  }

  @Override
  public Optional<QuotaWarnRuleInfo> queryQuotaWarnRuleInfo(String key) {
    return Optional.ofNullable(configRules.get(key));
  }

  @Override
  public void checkWarnRule(QuotaData qd) {
    String key = String.join(",", qd.queryAppName(), qd.queryQuotaName());
    Optional<QuotaWarnRuleInfo> qwrOptional = queryQuotaWarnRuleInfo(key);

    // 查询是否配置了阈值
    qwrOptional.ifPresent(warnRule -> {

      String instQuotaKey = qd.getQuota().getInstQuotaKey();
      Optional<QuotaAndRule> qarOptional = queryQuotaAndRule(instQuotaKey);

      // 是否是首次匹配
      if (!qarOptional.isPresent()) {
        QuotaAndRule quotaAndRule = new QuotaAndRule(qd.getQuota(), warnRule);
        instQuotas.put(instQuotaKey, quotaAndRule);
      }

      // 计算规则是否满足要求
      queryQuotaAndRule(instQuotaKey).ifPresent(qar -> {
        if (qar.accept(context, qd)) {
          // 通知
          notifyProxy.query(identifyNotify).ifPresent(notify -> {
            notify.send(qar.getWarn().arrayOfPhoneNums(), qar.notifyContent(qd));
          });
        }
      });
    });
  }

  @Scheduled(cron = "${hbq.monitor.quota.clean-cron:0 0 * * * *}")
  @Override
  public void cleanQuotaData() {
    long time = FormatTime.nowSecs() - quotaReserveDays * 24 * 3600L;
    log.info("清理[{}]时间点前的指标数据", FormatTime.YYYYMMDDHHMISS.withSecs(time));
    try {
      quotaDataDao.cleanQuotaData(time);
      log.info("清理成功");
    } catch (Exception e) {
      log.error("清理异常", e);
    }
  }

  private void createQuotaWarnRule() {
    try {
      monitorDao.createQuotaWarnRule();
      log.info("创建指标通知规则表成功");
    } catch (Exception e) {
      log.info("指标通知规则表已存在");
    }
  }

  private Optional<QuotaAndRule> queryQuotaAndRule(String key) {
    return Optional.ofNullable(instQuotas.get(key));
  }

  private void initQuotaDatabase() {
    try {
      quotaDataDao.createQuotaData();
      log.info("创建指标数据表成功");
    } catch (Exception e) {
      log.info("指标数据表已经存在");
    }
    if (H2_DRIVER.equals(dialectDriver)) {
      CompletableFuture.runAsync(() -> {
        String tcpPort = context.getProperty("hbq.monitor.dialect.h2db.tcpPort", "21003");
        String[] args = new String[]{"-tcpPort", tcpPort, "-tcpAllowOthers"};
        try {
          server = Server.createTcpServer(args).start();
          log.info("启动h2数据成功");
        } catch (SQLException ex) {
          log.error("启动h2数据库tcp监听异常", ex);
        }
      });
    }
  }

}
