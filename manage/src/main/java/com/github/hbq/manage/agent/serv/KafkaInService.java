package com.github.hbq.manage.agent.serv;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.session.RowBounds;

/**
 * @author hbq
 */
public interface KafkaInService {

  /**
   * 查询应用列表
   *
   * @param params
   * @param pageNum
   * @param pageSize
   * @return
   */
  List<Map> queryAppInfos(Map params, int pageNum, int pageSize);


  /**
   * 查询实例列表
   *
   * @param params
   * @param pageNum
   * @param pageSize
   * @return
   */
  List<Map> queryInstanceList(Map params, int pageNum, int pageSize);
}
