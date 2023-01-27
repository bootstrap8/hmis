package com.github.hbq.monitor.dao.dialect;


/**
 * @author hbq
 */
public interface DialectHelper {

  /**
   * 初始化数据库ORM模板
   *
   * @throws Exception
   */
  void bootstrap() throws Exception;

  /**
   * 返回数据库ORM标识
   *
   * @return
   */
  String getKey();

  /**
   * 返回mybatis模板接口
   *
   * @param clz
   * @param <T>
   * @return
   */
  <T> T getMapper(Class<T> clz);

}
