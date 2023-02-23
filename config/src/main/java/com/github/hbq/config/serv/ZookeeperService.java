package com.github.hbq.config.serv;

import com.github.hbq.config.pojo.LeafBean;
import com.github.hbq.config.pojo.ZKNode;
import java.util.List;
import java.util.Set;

/**
 * @author hbq
 */
public interface ZookeeperService {

  ZKNode listNodeEntries(String path, String authRole) throws Exception;

  void setPropertyValue(String path, String name, String value) throws Exception;

  void deleteLeaves(List<String> leafNames) throws Exception;

  void createFolder(String folderPath, String propertyName, String propertyValue)
      throws Exception;

  void createNode(String path, String name, String value)
      throws Exception;

  void deleteFolders(List<String> folderNames) throws Exception;

  Set<LeafBean> exportTree(String zkPath) throws Exception;

  void importData(List<String> importFile, boolean overwrite) throws Exception;

  Set<LeafBean> searchTree(String path, String name, String value) throws Exception;
}
