package com.github.hbq.manage.config.serv;

import com.github.hbq.manage.config.pojo.LeafBean;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author hbq
 */
public interface NodeService {

  Map queryNodes(Map map);

  void setPropertyValue(Map map);

  void deleteLeaves(Map map);

  void createFolder(Map map);

  void createNode(Map map);

  void delete(Map map);

  Set<LeafBean> exportTree(Map map);

  void importData(MultipartFile file, boolean overwrite);
}
