package com.github.hbq.manage.agent.dao;

import com.github.hbq.agent.app.pojo.AppInfo;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;

/**
 * @author hbq
 */
@Repository
@Mapper
public interface KafkaInDao {

  /**
   * 查询应用列表
   *
   * @param params
   * @param rb
   * @return
   */
  List<Map> queryAppInfos(Map params, RowBounds rb);

  /**
   * 查询实例列表
   *
   * @param params
   * @param rb
   * @return
   */
  List<Map> queryInstanceList(Map params, RowBounds rb);
}
