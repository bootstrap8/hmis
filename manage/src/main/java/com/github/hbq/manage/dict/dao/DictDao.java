package com.github.hbq.manage.dict.dao;

import com.github.hbq.common.dict.DictInfo;
import com.github.hbq.common.dict.DictPair;
import com.github.hbq.manage.dict.pojo.DictKeyInfo;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;

/**
 * @author hbq
 */
@Repository("hbq-manage-dict-dao-DictDao")
@Mapper
public interface DictDao {

  void saveDict(DictInfo dict);

  void saveDictEnumExt(@Param("fn") String fn, @Param("pair") DictPair pair);

  void saveDictSqlExt(@Param("fn") String fn, @Param("sql") String sql);

  void deleteDict(@Param("fn") String fn);

  void deleteDictEnumExt(@Param("fn") String fn);

  void deleteDictSqlExt(@Param("fn") String fn);

  List<DictInfo> queryAllDict(RowBounds rb, @Param("key") DictKeyInfo key);

  List<DictPair> queryDictPairs(@Param("fn") String fn);

  DictInfo queryDict(@Param("fn") String fn);

  DictInfo queryDictSqlExt(@Param("fn") String fn);

  void createDictInfo();

  void createDictExtKv();

  void createDictExtSql();
}
