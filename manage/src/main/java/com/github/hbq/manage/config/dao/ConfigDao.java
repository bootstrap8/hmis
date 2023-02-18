package com.github.hbq.manage.config.dao;

import com.github.hbq.manage.config.pojo.HistoryOperate;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;

/**
 * @author hbq
 */
@Mapper
@Repository
public interface ConfigDao {

  void createConfigHistory();

  void saveHistoryOperate(HistoryOperate history);

  void deleteHistoryOperate(@Param("opTime") long opTime);

  List<HistoryOperate> queryHistoryOperates(Map map, RowBounds rb);
}
