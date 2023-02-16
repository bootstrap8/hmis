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
import lombok.extern.slf4j.Slf4j;
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
  public ZKNode listNodeEntries(String path, String authRole) throws
      KeeperException, InterruptedException {
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
