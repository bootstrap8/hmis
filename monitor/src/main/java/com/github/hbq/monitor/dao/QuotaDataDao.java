package com.github.hbq.monitor.dao;

import com.github.hbq.agent.app.pojo.QuotaData;
import com.github.hbq.common.spring.datasource.HDS;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author hbq
 */
@Repository
@Mapper
@HDS("quota")
public interface QuotaDataDao {

  /**
   * 测试
   *
   * @return
   */
  Map test();

  /**
   * 创建指标数据表
   */
  void createQuotaData();

  /**
   * 保存指标数据
   *
   * @param list
   */
  void saveQuotaData(List<QuotaData> list);

  /**
   * 清理过期的指标数据
   *
   * @param collectTime
   */
  void cleanQuotaData(@Param("collectTime") long collectTime);

  /**
   * 查询指标数据
   *
   * @param params
   * @return
   */
  List<Map> queryQuotaDatas(Map params);
}
