package com.github.hbq.monitor.serv;

import com.github.hbq.agent.app.pojo.QuotaData;
import com.github.hbq.monitor.event.WarnRuleUpdateEvent;
import com.github.hbq.monitor.pojo.QuotaWarnRuleInfo;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.ibatis.session.RowBounds;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author hbq
 */
public interface MonitorService {

  /**
   * 指标数据入库
   *
   * @param qd
   */
  void saveQuotaData(List<QuotaData> qd);

  /**
   * 查询指标通知规则列表
   *
   * @param map
   * @param pageNum
   * @param pageSize
   * @return
   */
  List<QuotaWarnRuleInfo> queryQuotaWarnRules(Map map, int pageNum, int pageSize);

  /**
   * 查询指标通知规则列表
   *
   * @param map
   * @return
   */
  List<QuotaWarnRuleInfo> queryQuotaWarnRules(Map map);

  /**
   * 重载指标通知规则
   *
   * @param event
   */
  void loadQuotaWarnRules(WarnRuleUpdateEvent event);

  /**
   * 查询指标通知规则
   *
   * @param key <appName,quotaName>
   * @return
   */
  Optional<QuotaWarnRuleInfo> queryQuotaWarnRuleInfo(String key);

  /**
   * 指标数据匹配规则
   *
   * @param qd
   */
  void checkWarnRule(QuotaData qd);

  /**
   * 保存指标通知规则
   *
   * @param map
   */
  @Transactional(rollbackFor = Exception.class)
  void saveQuotaWarnRule(Map map);

  /**
   * 删除应用指标通知规则
   *
   * @param appName
   * @param quotaName
   */
  void deleteQuotaWarnRule(String appName, String quotaName);

  /**
   * 批量删除应用指标通知规则
   *
   * @param list
   */
  @Transactional(rollbackFor = Exception.class)
  void deleteQuotaWarnRules(List<Map> list);

  /**
   * 更新应用指标通知规则的号码信息
   *
   * @param appName
   * @param phoneNums
   */
  void updatePhoneNumsOnApp(String appName, String phoneNums);

  /**
   * 更新所有记录的号码信息
   *
   * @param phoneNums
   */
  void updatePhoneNumsOnAll(String phoneNums);

  /**
   * 清理过期指标数据
   */
  void cleanQuotaData();

  /**
   * 查询指标数据
   *
   * @param params
   * @return
   */
  List<Map> queryQuotaDatas(Map params);
}
