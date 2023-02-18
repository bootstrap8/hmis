package com.github.hbq.manage.config.serv.impl;

import com.github.hbq.common.spring.context.SpringContext;
import com.github.hbq.manage.config.pojo.LeafBean;
import com.github.hbq.manage.config.pojo.ZKNode;
import com.github.hbq.manage.config.serv.ZookeeperService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.ACL;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.data.Stat;
import org.apache.zookeeper.server.auth.DigestAuthenticationProvider;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author hbq
 */
@Component("zkui-service-ZookeeperServiveImpl")
@Slf4j
public class ZookeeperServiceImpl implements ZookeeperService, InitializingBean, DisposableBean {

  public final static int MAX_CONNECT_ATTEMPT = 5;
  public final static String ZK_ROOT_NODE = "/";
  public final static String ZK_SYSTEM_NODE = "zookeeper";
  public final static String ZK_HOSTS = "/appconfig/hosts";
  public final static String ROLE_USER = "USER";
  public final static String ROLE_ADMIN = "ADMIN";
  public final static String SOPA_PIPA = "SOPA/PIPA BLACKLISTED VALUE";

  @Value("${zkUrl:192.168.56.2:2181}")
  private String url;
  @Value("${zkSessionTimeoutMills:5000}")
  private int zkSessionTimeout;

  private String authScheme;
  private String authUser;
  private String authPwd;

  @Autowired
  private SpringContext context;

  private ZooKeeper zk;


  @Override
  public void afterPropertiesSet() throws Exception {
    connectionZookeeper();
  }

  @Override
  public void destroy() throws Exception {
    Optional.ofNullable(zk).ifPresent(z -> {
      try {
        z.close();
        log.info("断开与Zookeeper的连接");
      } catch (InterruptedException e) {
      }
    });
  }

  @Override
  public ZKNode listNodeEntries(String path, String authRole) throws Exception {
    List<String> folders = new ArrayList<>();
    List<LeafBean> leaves = new ArrayList<>();

    List<String> children = zk.getChildren(path, false);
    if (children != null) {
      for (String child : children) {
        if (!child.equals(ZK_SYSTEM_NODE)) {

          List<String> subChildren =
              zk.getChildren(path + ("/".equals(path) ? "" : "/") + child, false);
          boolean isFolder = subChildren != null && !subChildren.isEmpty();
          if (isFolder) {
            folders.add(child);
          } else {
            String childPath = getNodePath(path, child);
            leaves.add(this.getNodeValue(zk, path, childPath, child, authRole));
          }

        }

      }
    }

    Collections.sort(folders);
    Collections.sort(leaves, new Comparator<LeafBean>() {
      @Override
      public int compare(LeafBean o1, LeafBean o2) {
        return o1.getName().compareTo(o2.getName());
      }
    });

    ZKNode zkNode = new ZKNode();
    zkNode.setLeafBeanLSt(leaves);
    zkNode.setNodeLst(folders);
    return zkNode;
  }

  @Override
  public void setPropertyValue(String path, String name, String value) throws Exception {
    String nodePath = path + (path.endsWith("/") ? "" : '/') + name;
    if (log.isDebugEnabled()) {
      log.debug("Setting property {} to {}", nodePath, value);
    }
    zk.setData(nodePath, value.getBytes(), -1);
  }

  @Override
  public void deleteLeaves(List<String> leafNames) throws Exception {

    for (String leafPath : leafNames) {
      if (log.isDebugEnabled()) {
        log.debug("Deleting leaf {}", leafPath);
      }
      zk.delete(leafPath, -1);
    }
  }

  @Override
  public void createFolder(String folderPath, String propertyName, String propertyValue) throws Exception {
    ArrayList<ACL> acls = ZooDefs.Ids.OPEN_ACL_UNSAFE;
    zk.create(folderPath, "".getBytes(), acls, CreateMode.PERSISTENT);
    zk.create(folderPath + "/" + propertyName, propertyValue == null ? null : propertyValue
        .getBytes(), acls, CreateMode.PERSISTENT);
  }

  @Override
  public void createNode(String path, String name, String value) throws Exception {
    String nodePath = path.endsWith("/") ? path + name : path + "/" + name;
    ArrayList<ACL> acls = ZooDefs.Ids.OPEN_ACL_UNSAFE;
    zk.create(nodePath, value == null ? null : value.getBytes(), acls, CreateMode.PERSISTENT);
  }

  @Override
  public void deleteFolders(List<String> folderNames) throws Exception {
    for (String folderPath : folderNames) {
      deleteFolderInternal(folderPath, zk);
    }
  }

  @Override
  public Set<LeafBean> exportTree(String zkPath) throws Exception {
    // 1. Collect nodes
    long startTime = System.currentTimeMillis();
    Set<LeafBean> leaves = new TreeSet<>();
    exportTreeInternal(leaves, zkPath);
    long estimatedTime = System.currentTimeMillis() - startTime;
    log.info("导出目录: {} 下的配置耗时: {} ms", zkPath, (estimatedTime));
    return leaves;
  }

  @Override
  public void importData(List<String> importFile, boolean overwrite) throws Exception {
    for (String line : importFile) {
      // Delete Operation
      if (line.startsWith("-")) {
        String nodeToDelete = line.substring(1);
        deleteNodeIfExists(nodeToDelete, zk);
      } else {
        int firstEq = line.indexOf('=');
        int secEq = line.indexOf('=', firstEq + 1);

        String path = line.substring(0, firstEq);
        if ("/".equals(path)) {
          path = "";
        }
        String name = line.substring(firstEq + 1, secEq);
        String value = readExternalizedNodeValue(line.substring(secEq + 1));
        String fullNodePath = path + "/" + name;

        // Skip import of system node
        if (fullNodePath.startsWith(ZK_SYSTEM_NODE)) {
          continue;
        }
        boolean nodeExists = nodeExists(fullNodePath);

        if (!nodeExists) {
          //If node doesnt exist then create it.
          createPathAndNode(path, name, value.getBytes(), true);
        } else {
          //If node exists then update only if overwrite flag is set.
          if (overwrite) {
            setPropertyValue(path + "/", name, value);
          } else {
            log.info("Skipping update for existing property " + path + "/" + name + " as overwrite is not enabled!");
          }
        }
      }
    }
  }

  @Override
  public Set<LeafBean> searchTree(String path, String name, String value) throws Exception {
    //Export all nodes and then search.
    Set<LeafBean> searchResult = new TreeSet<>();
    Set<LeafBean> leaves = new TreeSet<>();
    exportTreeInternal(leaves, ZK_ROOT_NODE);
    for (LeafBean leaf : leaves) {
      if (leaf.containKey(path, name, value)) {
        searchResult.add(leaf);
      }
    }
    return searchResult;
  }

  private String externalizeNodeValue(byte[] value) {
    return value == null ? "" : new String(value).replaceAll("\\n", "\\\\n").replaceAll("\\r", "");
    // We might want to BASE64 encode it
  }

  private void createPathAndNode(String path, String name, byte[] data, boolean force) throws InterruptedException, KeeperException {
    // 1. Create path nodes if necessary
    StringBuilder currPath = new StringBuilder();
    for (String folder : path.split("/")) {
      if (folder.length() == 0) {
        continue;
      }
      currPath.append('/');
      currPath.append(folder);

      if (!nodeExists(currPath.toString())) {
        createIfDoesntExist(currPath.toString(), new byte[0], true);
      }
    }

    // 2. Create leaf node
    createIfDoesntExist(path + '/' + name, data, force);
  }

  private void createIfDoesntExist(String path, byte[] data, boolean force) throws InterruptedException, KeeperException {
    ArrayList<ACL> acls = ZooDefs.Ids.OPEN_ACL_UNSAFE;
    try {
      zk.create(path, data, acls, CreateMode.PERSISTENT);
    } catch (KeeperException ke) {
      //Explicit Overwrite
      if (KeeperException.Code.NODEEXISTS.equals(ke.code())) {
        if (force) {
          zk.delete(path, -1);
          zk.create(path, data, acls, CreateMode.PERSISTENT);
        }
      } else {
        throw ke;
      }
    }
  }

  public boolean nodeExists(String nodeFullPath) throws KeeperException,
      InterruptedException {
    return zk.exists(nodeFullPath, false) != null;
  }

  private String readExternalizedNodeValue(String raw) {
    return raw.replaceAll("\\\\n", "\n");
  }

  private void deleteNodeIfExists(String path, ZooKeeper zk) throws InterruptedException,
      KeeperException {
    zk.delete(path, -1);
  }

  private void exportTreeInternal(Set<LeafBean> entries, String path) throws InterruptedException, KeeperException {
    // 1. List leaves
    entries.addAll(this.listLeaves(path));
    // 2. Process folders
    for (String folder : this.listFolders(path)) {
      exportTreeInternal(entries, this.getNodePath(path, folder));
    }
  }

  private List<String> listFolders(String path) throws KeeperException,
      InterruptedException {
    List<String> folders = new ArrayList<>();
    List<String> children = zk.getChildren(path, false);
    if (children != null) {
      for (String child : children) {
        if (!child.equals(ZK_SYSTEM_NODE)) {
          List<String> subChildren =
              zk.getChildren(path + ("/".equals(path) ? "" : "/") + child, false);
          boolean isFolder = subChildren != null && !subChildren.isEmpty();
          if (isFolder) {
            folders.add(child);
          }
        }

      }
    }

    Collections.sort(folders);
    return folders;
  }

  private List<LeafBean> listLeaves(String path) throws
      InterruptedException, KeeperException {
    List<LeafBean> leaves = new ArrayList<>();

    List<String> children = zk.getChildren(path, false);
    if (children != null) {
      for (String child : children) {
        String childPath = getNodePath(path, child);
        List<String> subChildren = Collections.emptyList();
        subChildren = zk.getChildren(childPath, false);
        boolean isFolder = subChildren != null && !subChildren.isEmpty();
        if (!isFolder) {
          leaves.add(this.getNodeValue(zk, path, childPath, child, "ADMIN"));
        }
      }
    }

    Collections.sort(leaves, new Comparator<LeafBean>() {
      @Override
      public int compare(LeafBean o1, LeafBean o2) {
        return o1.getName().compareTo(o2.getName());
      }
    });

    return leaves;
  }

  private void deleteFolderInternal(String folderPath, ZooKeeper zk) throws KeeperException,
      InterruptedException {
    for (String child : zk.getChildren(folderPath, false)) {
      deleteFolderInternal(getNodePath(folderPath, child), zk);
    }
    zk.delete(folderPath, -1);
  }

  private void connectionZookeeper() throws IOException, InterruptedException {
    int connectAttempt = 0;
    this.zk = new ZooKeeper(url, zkSessionTimeout / 1000, event -> log.info("成功连接到Zookeeper。"));
    context.optional("authScheme", String.class).ifPresent(v -> authScheme = v);
    context.optional("authUser", String.class).ifPresent(u -> {
      this.authUser = u;
      this.authPwd = context.getProperty("authPwd");
      zk.addAuthInfo(authScheme, (authUser + ":" + authPwd).getBytes());
      try {
        List<ACL> acls = new ArrayList<ACL>();
        Id zkUser = new Id("digest", DigestAuthenticationProvider
            .generateDigest(authUser + ":" + authPwd));
        ACL acl = new ACL(ZooDefs.Perms.ALL, zkUser);
        acls.add(acl);
        // 漏洞扫描的路径，必须加密
        String[] paths = {"/", "/zookeeper", "/zookeeper/quota", "/zookeeper/config"};
        for (String path : paths) {
          try {
            zk.setACL(path, acls, -1);
          } catch (Exception e) {
            log.error("提示信息: 目录 [{}] 不存在, 添加认证失败。", path);
          }
        }
      } catch (Exception e) {
        log.error("", e);
      }
    });
    //Wait till connection is established.
    while (zk.getState() != ZooKeeper.States.CONNECTED) {
      Thread.sleep(30);
      connectAttempt++;
      if (connectAttempt == MAX_CONNECT_ATTEMPT) {
        break;
      }
    }
  }

  private String getNodePath(String path, String name) {
    return path + ("/".equals(path) ? "" : "/") + name;

  }

  private LeafBean getNodeValue(ZooKeeper zk, String path, String childPath, String child,
      String authRole) {
    //Reason exception is caught here is so that lookup can continue to happen if a particular property is not found at parent level.
    try {
      if (log.isDebugEnabled()) {
        log.debug("Lookup: path=" + path + ",childPath=" + childPath + ",child=" + child + ",authRole=" + authRole);
      }
      byte[] dataBytes = zk.getData(childPath, false, new Stat());
      if (!authRole.equals(ROLE_ADMIN)) {
        if (checkIfPwdField(child)) {
          return (new LeafBean(path, child, SOPA_PIPA.getBytes()));
        } else {
          return (new LeafBean(path, child, dataBytes));
        }
      } else {
        return (new LeafBean(path, child, dataBytes));
      }
    } catch (KeeperException | InterruptedException ex) {
      log.error(ex.getMessage());
    }
    return null;

  }

  private Boolean checkIfPwdField(String property) {
    if (property.contains("PWD") || property.contains("pwd") || property
        .contains("PASSWORD") || property.contains("password") || property
        .contains("PASSWD") || property.contains("passwd")) {
      return true;
    } else {
      return false;
    }
  }
}
