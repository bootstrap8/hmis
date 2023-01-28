package com.github.hbq.monitor.dao;

import com.github.hbq.monitor.pojo.QuotaWarnRuleInfo;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;

/**
 * @author hbq
 */
@Repository
@Mapper
public interface MonitorDao {

  /**
   * 创建指标通知规则表
   */
  void createQuotaWarnRule();

  /**
   * 查询指标通知规则列表
   *
   * @param map
   * @return
   */
  List<QuotaWarnRuleInfo> queryQuotaWarnRules(Map map, RowBounds rb);

  /**
   * 查询所有指标通知规则列表
   *
   * @param map
   * @return
   */
  List<QuotaWarnRuleInfo> queryQuotaWarnRules(Map map);

  /**
   * 保存指标通知规则
   *
   * @param map
   */
  void saveQuotaWarnRule(Map map);

  /**
   * 删除应用指标通知规则
   *
   * @param appName
   * @param quotaName
   */
  void deleteQuotaWarnRule(@Param("appName") String appName, @Param("quotaName") String quotaName);

  /**
   * 删除应用所有指标通知规则
   *
   * @param appName
   */
  void deleteAppAllQuotaWarnRule(@Param("appName") String appName);

  /**
   * 更新应用指标通知规则的号码信息
   *
   * @param appName
   * @param phoneNums
   * @param updateTime
   */
  void updatePhoneNumsOnApp(@Param("appName") String appName, @Param("phoneNums") String phoneNums
      , @Param("updateTime") long updateTime);

  /**
   * 更新所有记录的号码信息
   *
   * @param phoneNums
   * @param updateTime
   */
  void updatePhoneNumsOnAll(@Param("phoneNums") String phoneNums, @Param("updateTime") long updateTime);
}
