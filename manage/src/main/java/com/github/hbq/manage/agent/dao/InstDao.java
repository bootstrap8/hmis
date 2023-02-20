package com.github.hbq.manage.agent.dao;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;

/**
 * @author hbq
 */
@Repository("agent-dao-InstDao")
@Mapper
public interface InstDao {

  List<Map> queryAppInfos(Map map, RowBounds rb);
}
