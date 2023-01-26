package com.github.hbq.agent.app.dao;

import com.github.hbq.agent.app.pojo.InstInfo;
import java.util.List;
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
   * 更新实例的最新注册时间
   *
   * @param instInfo
   */
  void updateInstRegTime(InstInfo instInfo);

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

  /**
   * 创建kafka入口消息速率应用配置表
   */
  void createKafkaInAppRateLimiter();

  /**
   * 创建kafka入口消息速率应用实例配置表
   */
  void createKafkaInInstanceRateLimiter();

  /**
   * 保存kafka入口消息速率应用配置
   *
   * @param map
   */
  void saveKafkaInAppRateLimiter(Map map);

  /**
   * 保存kafka入口消息速率应用实例配置
   *
   * @param map
   */
  void saveKafkaInInstanceRateLimiter(Map map);

  /**
   * 更新应用下所有实例的kafka入口消息速率配置
   *
   * @param appName
   */
  void updateKafkaInInstanceRateLimiter(@Param("app_name") String appName, @Param("permits") int permits);

  /**
   * 删除kafka入口消息速率应用配置
   *
   * @param appName
   */
  void deleteKafkaInAppRateLimiter(@Param("app_name") String appName);

  /**
   * kafka入口消息速率应用实例配置
   *
   * @param dc
   * @param appName
   * @param ip
   * @param port
   */
  void deleteKafkaInInstanceRateLimiter(@Param("data_center") String dc,
      @Param("app_name") String appName, @Param("ip") String ip, @Param("port") int port);

  /**
   * 查询应用kafka入口消息速率配置
   *
   * @param params
   * @return
   */
  Map queryKafkaInAppRateLimiter(Map params);

  /**
   * 查询应用实例kafka入口消息速率配置
   *
   * @param params
   * @return
   */
  Map queryKafkaInInstanceRateLimiter(Map params);

  /**
   * 查询无效的实例
   *
   * @param expireTime
   * @return
   */
  List<Map> queryInvalidInstances(@Param("expireTime") long expireTime);

  /**
   * 清理无效实例
   *
   * @param instInfo
   */
  void cleanInstance(InstInfo instInfo);

  /**
   * 清理无效的实例kafka入口消息速率配置
   *
   * @param instInfo
   */
  void cleanKafkaInInstance(InstInfo instInfo);
}
