package com.github.hbq.agent.app.dao;

import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author hbq
 */
@Mapper
public interface AgentDao {

  void createAppInfo();

  void createInstInfo();

  void saveAppInfo(Map map);

  void deleteAppInfo(@Param("name") String name);

  void saveInstInfo(Map map);

  void deleteInstInfo(@Param("data_center") String dc, @Param("ip") String ip, @Param("port") int port);
}
