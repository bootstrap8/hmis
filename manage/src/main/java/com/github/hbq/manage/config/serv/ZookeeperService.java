package com.github.hbq.manage.config.serv;

import com.github.hbq.manage.config.pojo.ZKNode;
import org.apache.zookeeper.KeeperException;

/**
 * @author hbq
 */
public interface ZookeeperService {

  ZKNode listNodeEntries( String path, String authRole) throws
      KeeperException, InterruptedException;
}
