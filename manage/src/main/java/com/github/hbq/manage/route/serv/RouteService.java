package com.github.hbq.manage.route.serv;

import com.github.hbq.manage.route.pojo.RouteInfo;
import com.github.hbq.manage.route.pojo.TemplateInfo;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author hbq
 */
public interface RouteService {

  @Transactional(rollbackFor = Exception.class)
  void saveRouteConfig(RouteInfo route);

  void deleteRouteConfig(String id);

  List<RouteInfo> queryAllRouteConfig(int pageNum, int pageSize, String routeSelect, String routeKey);

  RouteInfo queryRoute(String id);

  void refreshRouteConfig();

  List<TemplateInfo> queryRouteTemplateInfos();
}
