package com.github.hbq.monitor.dao.dialect;

import com.github.hbq.agent.app.pojo.QuotaData;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author hbq
 */
@Mapper
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
