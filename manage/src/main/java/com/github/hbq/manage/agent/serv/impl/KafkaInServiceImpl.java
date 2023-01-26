package com.github.hbq.manage.agent.serv.impl;

import com.github.hbq.common.utils.FormatTime;
import com.github.hbq.manage.agent.dao.KafkaInDao;
import com.github.hbq.manage.agent.serv.KafkaInService;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author hbq
 */
@Service
@Slf4j
public class KafkaInServiceImpl implements KafkaInService {

  @Autowired
  private KafkaInDao kafkaInDao;

  @Override
  public List<Map> queryAppInfos(Map params, int pageNum, int pageSize) {
    List<Map> list = kafkaInDao.queryAppInfos(params, new RowBounds(pageNum, pageSize));
    for (Map map : list) {
      long regTime = MapUtils.getLongValue(map, "regTime");
      map.put("fmtRegTime", FormatTime.YYYYMMDDHHMISS.withSecs(regTime));
    }
    return list;
  }

  @Override
  public List<Map> queryInstanceList(Map params, int pageNum, int pageSize) {
    List<Map> list = kafkaInDao.queryInstanceList(params, new RowBounds(pageNum, pageSize));
    for (Map map : list) {
      long regTime = MapUtils.getLongValue(map, "regTime");
      map.put("fmtRegTime", FormatTime.YYYYMMDDHHMISS.withSecs(regTime));
    }
    return list;
  }
}
