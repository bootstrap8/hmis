package com.github.hbq.monitor.dao.dialect.mysql;

import com.github.hbq.common.spring.context.SpringContext;
import com.github.hbq.monitor.dao.dialect.DialectHelper;
import com.github.hbq.monitor.dao.dialect.QuotaDataDao;
import com.github.hbq.monitor.dao.dialect.manage.DialectManage;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

/**
 * @author hbq
 */
@ConditionalOnProperty(prefix = "hbq.monitor", name = "dialect", havingValue = "mysql")
@Component("monitor-dao-dialect-MysqlDialectHelperImpl")
@Slf4j
public class MysqlDialectHelperImpl implements DialectHelper, InitializingBean {

  public static final String DIALECT_MYSQL = "mysql";

  @Autowired
  private SpringContext context;

  @Autowired
  private DialectManage dialectManage;

  private SqlSessionTemplate sqlSessionTemplate;

  @Override
  public void afterPropertiesSet() throws Exception {
    bootstrap();
    dialectManage.registry(getKey(), this);
    createQuotaData();
  }

  @Override
  public void bootstrap() throws Exception {
    log.info("初始化mysql数据库来存储指标数据");
    BasicDataSource ds = new BasicDataSource();
    ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
    ds.setUrl(context.getProperty("hbq.monitor.dialect.mysql.url"));
    ds.setUsername(context.getProperty("hbq.monitor.dialect.mysql.username"));
    ds.setPassword(context.getProperty("hbq.monitor.dialect.mysql.password"));
    ds.setMaxWaitMillis(context.getLongValue("hbq.monitor.dialect.mysql.max-wait-mills", 300000));
    ds.setMaxTotal(context.getIntValue("hbq.monitor.dialect.mysql.max-total", 20));
    ds.setValidationQuery("select 1");

    SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
    factoryBean.setDataSource(ds);
    factoryBean.setConfigLocation(new ClassPathResource("jpaConfig.xml"));
    PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    List<Resource> list = new ArrayList<>();
    String[] paths = {"classpath*:com/**/dialect/h2db/**/*Mapper.xml", "classpath*:com/**/dialect/common/**/*Mapper.xml"};
    for (String path : paths) {
      Resource[] resources = resolver.getResources(path);
      for (Resource resource : resources) {
        list.add(resource);
      }
    }
    Resource[] array = new Resource[list.size()];
    list.toArray(array);
    factoryBean.setMapperLocations(array);
    SqlSessionFactory factory = factoryBean.getObject();
    sqlSessionTemplate = new SqlSessionTemplate(factory);

    log.info("初始化mysql完成: {}", getMapper(QuotaDataDao.class).test());
  }

  @Override
  public String getKey() {
    return DIALECT_MYSQL;
  }

  @Override
  public <T> T getMapper(Class<T> clz) {
    return sqlSessionTemplate.getMapper(clz);
  }

  private void createQuotaData() {
    try {
      getMapper(QuotaDataDao.class).createQuotaData();
      log.info("创建指标数据表成功");
    } catch (Exception e) {
      log.info("指标数据表已经存在");
    }
  }
}
