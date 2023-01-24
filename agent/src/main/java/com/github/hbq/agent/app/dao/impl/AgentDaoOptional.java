package com.github.hbq.agent.app.dao.impl;

import com.github.hbq.agent.app.dao.AgentDao;
import com.github.hbq.common.spring.context.SpringContext;
import java.util.Optional;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author hbq
 */
public class AgentDaoOptional {

  @Autowired
  private SpringContext context;

  public Optional<AgentDao> getAgentDao() {
    SqlSessionTemplate stmt = context.getBean(SqlSessionTemplate.class);
    return Optional.ofNullable(stmt == null ? null : stmt.getMapper(AgentDao.class));
  }
}
