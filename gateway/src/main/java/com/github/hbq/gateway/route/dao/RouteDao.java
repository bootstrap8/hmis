package com.github.hbq.gateway.route.dao;

import com.github.hbq.gateway.route.RouteInfo;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * @author hbq
 */
@Repository("hbq-common-gateway-route-dao-RouteDao")
@Mapper
public interface RouteDao {

  void createRouteConfig();

  List<RouteInfo> queryAllRouteConfig();
}
