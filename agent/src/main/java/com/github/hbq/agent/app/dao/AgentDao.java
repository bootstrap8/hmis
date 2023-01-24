package com.github.hbq.agent.app.dao;

import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author hbq
 */
@Mapper
public interface AgentDao {

  /**
   * 创建应用注册表
   */
  void createAppInfo();

  /**
   * 创建实例注册表
   */
  void createInstInfo();

  /**
   * 注册保存应用信息
   *
   * @param map
   */
  void saveAppInfo(Map map);

  /**
   * 删除应用信息
   *
   * @param name
   */
  void deleteAppInfo(@Param("name") String name);

  /**
   * 保存注册的实例信息
   *
   * @param map
   */
  void saveInstInfo(Map map);

  /**
   * 删除实例信息
   *
   * @param data_center
   * @param app_name
   * @param ip
   * @param port
   */
  void deleteInstInfo(@Param("data_center") String data_center, @Param("app_name") String app_name,
      @Param("ip") String ip, @Param("port") int port);

  /**
   * 创建指标注册信息表
   */
  void createQuotaInfo();

  /**
   * 保存注册的指标信息
   *
   * @param map
   */
  void saveQuotaInfo(Map map);

  /**
   * 删除指标信息
   *
   * @param data_center
   * @param app_name
   * @param ip
   * @param port
   * @param quota_name
   */
  void deleteQuotaInfo(@Param("data_center") String data_center, @Param("app_name") String app_name,
      @Param("ip") String ip, @Param("port") int port, @Param("quota_name") String quota_name);
}
