package com.github.hbq.monitor.serv.impl;

import com.alibaba.fastjson.JSON;
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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
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
  public static final String[] TIME_FORMAT = {"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"};

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
  @Value("${hbq.monitor.dialect.h2db.tcpPort:21003}")
  private String tcpPort;

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
  // ?????????+???????????????map???key
  private volatile Map<String, QuotaWarnRuleInfo> configRules = new ConcurrentHashMap<>(500);
  // ??????key+??????key??????map???key
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
      log.info("??????h2?????????");
    }
  }

  @Override
  public void saveQuotaData(List<QuotaData> qd) {
    tmp.addAll(qd);
    count.incrementAndGet(qd.size());
    if (count.compareAndInit(storeMaxBatchSize, storeTimeoutMills)) {
      try {
        quotaDataDao.saveQuotaData(tmp);
        log.info("????????????????????????: {}", tmp.size());
      } catch (Exception e) {
        log.error("??????????????????????????????", e);
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
    loadQuotaWarnRules(new WarnRuleUpdateEvent("??????/??????????????????"));
  }

  @Override
  public void deleteQuotaWarnRule(String appName, String quotaName) {
    monitorDao.deleteQuotaWarnRule(appName, quotaName);
    loadQuotaWarnRules(new WarnRuleUpdateEvent("????????????????????????"));
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
    loadQuotaWarnRules(new WarnRuleUpdateEvent("?????????????????????????????????"));
  }

  @Override
  public void updatePhoneNumsOnAll(String phoneNums) {
    monitorDao.updatePhoneNumsOnAll(phoneNums, FormatTime.nowSecs());
    loadQuotaWarnRules(new WarnRuleUpdateEvent("???????????????????????????"));
  }

  @EventListener({WarnRuleUpdateEvent.class})
  @Override
  public void loadQuotaWarnRules(WarnRuleUpdateEvent event) {
    try {
      log.info("????????????????????????, {}", Objects.isNull(event) ? "" : event.getSource());
      List<QuotaWarnRuleInfo> allWarnRule = queryQuotaWarnRules(ImmutableMap.of("enable", -1));
      Map<String, QuotaWarnRuleInfo> _configRules = new ConcurrentHashMap<>();
      for (QuotaWarnRuleInfo warnRule : allWarnRule) {
        _configRules.put(warnRule.getKey(), warnRule);
      }
      this.configRules = _configRules;
    } catch (Exception e) {
      log.error("??????????????????????????????", e);
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

    // ???????????????????????????
    qwrOptional.ifPresent(warnRule -> {

      String instQuotaKey = qd.getQuota().getInstQuotaKey();
      Optional<QuotaAndRule> qarOptional = queryQuotaAndRule(instQuotaKey);

      // ?????????????????????
      if (!qarOptional.isPresent()) {
        QuotaAndRule quotaAndRule = new QuotaAndRule(qd.getQuota(), warnRule);
        instQuotas.put(instQuotaKey, quotaAndRule);
      }

      // ??????????????????????????????
      queryQuotaAndRule(instQuotaKey).ifPresent(qar -> {
        if (qar.accept(context, qd)) {
          // ??????
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
    log.info("??????[{}]???????????????????????????", FormatTime.YYYYMMDDHHMISS.withSecs(time));
    try {
      quotaDataDao.cleanQuotaData(time);
      log.info("????????????");
    } catch (Exception e) {
      log.error("????????????", e);
    }
  }

  @Override
  public List<Map> queryQuotaDatas(Map params) {
    String timeType = MapUtils.getString(params, "timeType");
    long endTimeSec = 0;
    long startTimeSec = 0;
    if (StringUtils.equals("custom", timeType)) {
      List time = (List) MapUtils.getObject(params, "time");
      String startTime = time.get(0).toString();
      String endTime = time.get(1).toString();
      try {
        startTimeSec = DateUtils.parseDate(startTime, TIME_FORMAT).getTime() / 1000L + 8 * 3600;
        endTimeSec = DateUtils.parseDate(endTime, TIME_FORMAT).getTime() / 1000L + 8 * 3600;
      } catch (ParseException e) {
      }
    } else {
      int hour = Integer.valueOf(timeType);
      endTimeSec = FormatTime.nowSecs();
      startTimeSec = endTimeSec - hour * 3600L;
    }
    // FIXED ??????element-ui???????????????8??????????????????
    params.put("startTime", startTimeSec);
    params.put("endTime", endTimeSec);
    log.info("????????????????????????: {}", JSON.toJSONString(params));
    List<Map> list = quotaDataDao.queryQuotaDatas(params);
    long collectTime;
    for (Map map : list) {
      collectTime = MapUtils.getLongValue(map, "collectTime");
      map.put("fmtCollectTime", FormatTime.HHMI.withSecs(collectTime));
    }
    return list;
  }

  private void createQuotaWarnRule() {
    try {
      monitorDao.createQuotaWarnRule();
      log.info("?????????????????????????????????");
    } catch (Exception e) {
      log.info("??????????????????????????????");
    }
  }

  private Optional<QuotaAndRule> queryQuotaAndRule(String key) {
    return Optional.ofNullable(instQuotas.get(key));
  }

  private void initQuotaDatabase() {
    try {
      quotaDataDao.createQuotaData();
      log.info("???????????????????????????");
    } catch (Exception e) {
      log.info("???????????????????????????");
    }
    if (H2_DRIVER.equals(dialectDriver)) {
      CompletableFuture.runAsync(() -> {
        String[] args = new String[]{"-tcpPort", tcpPort, "-tcpAllowOthers"};
        try {
          server = Server.createTcpServer(args).start();
          log.info("??????h2????????????");
        } catch (SQLException ex) {
          log.error("??????h2?????????tcp????????????", ex);
        }
      });
    }
  }

}
