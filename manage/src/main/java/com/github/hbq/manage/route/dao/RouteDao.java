package com.github.hbq.manage.route.dao;

import com.github.hbq.manage.route.pojo.RouteConfig;
import com.github.hbq.manage.route.pojo.RouteInfo;
import com.github.hbq.manage.route.pojo.TemplateInfo;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;

/**
 * @author hbq
 */
@Repository("hbq-common-gateway-route-dao-RouteDao")
@Mapper
public interface RouteDao {

  void createRouteConfig();

  void saveRouteConfig(RouteConfig route);

  void deleteRouteConfig(@Param("id") String id);

  List<RouteConfig> queryAllRouteConfig(RowBounds rb,
      @Param("routeSelect") String routeSelect,
      @Param("routeKey") String routeKey);

  RouteConfig queryRoute(@Param("id") String id);

  void createRouteTemplate();

  List<TemplateInfo> queryRouteTemplateInfos();
}
