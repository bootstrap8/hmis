package com.github.hbq.manage.config.serv;

import com.github.hbq.common.spring.context.UserInfo;
import com.github.hbq.manage.config.pojo.Backup;
import com.github.hbq.manage.config.pojo.HistoryOperate;
import com.github.hbq.manage.config.pojo.LeafBean;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author hbq
 */
public interface NodeService {

  Map queryNodes(UserInfo ui, Map map);

  void setPropertyValue(UserInfo ui, Map map);

  void deleteLeaves(UserInfo ui, Map map);

  void createFolder(UserInfo ui, Map map);

  void createNode(UserInfo ui, Map map);

  void delete(UserInfo ui, Map map);

  Set<LeafBean> exportTree(UserInfo ui, Map map);

  void importData(UserInfo ui, MultipartFile file, boolean overwrite);

  Set<LeafBean> searchTree(UserInfo ui, Map map);

  void cleanHistoryOperate();

  List<HistoryOperate> queryHistoryOperates(Map map, int pageNum, int pageSize);

  void backup();

  void recovery(Map map);

  List<Backup> queryBackups(Map map, int pageNum, int pageSize);

  @Transactional(rollbackFor = Exception.class)
  void deleteBackupById(Map map);

  @Transactional(rollbackFor = Exception.class)
  void cleanBackups();
}
