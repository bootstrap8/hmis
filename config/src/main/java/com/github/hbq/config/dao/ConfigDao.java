package com.github.hbq.config.dao;

import com.github.hbq.config.pojo.Backup;
import com.github.hbq.config.pojo.BackupDetail;
import com.github.hbq.config.pojo.HistoryOperate;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;

/**
 * @author hbq
 */
@Repository("config-dao-ConfigDao")
@Mapper
public interface ConfigDao {

  void createConfigHistory();

  void saveHistoryOperate(HistoryOperate history);

  void deleteHistoryOperate(@Param("opTime") long opTime);

  List<HistoryOperate> queryHistoryOperates(Map map, RowBounds rb);

  void createBackupMain();

  void createBackupDetail();

  List<Backup> queryBackups(@Param("startTime") long startTime,
      @Param("endTime") long endTime, RowBounds rb);

  List<BackupDetail> queryBackupDetails(@Param("id") String id);

  void deleteBackupById(@Param("id") String id);

  void deleteBackupDetailById(@Param("id") String id);

  void deleteBackupByTime(@Param("backTime") long backTime);

  void deleteBackupDetailByTime(@Param("backTime") long backTime);
}
