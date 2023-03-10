package com.github.hbq.manage.dict.serv;

import com.github.hbq.common.dict.DictInfo;
import com.github.hbq.common.dict.DictPair;
import com.github.hbq.manage.dict.pojo.DictKeyInfo;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author hbq
 */
public interface DictService {

  @Transactional(rollbackFor = Exception.class)
  void saveDict(DictInfo dict);

  @Transactional(rollbackFor = Exception.class)
  void saveDictEnumExt(DictInfo dict);

  @Transactional(rollbackFor = Exception.class)
  void saveDictSqlExt(DictInfo dict);

  void deleteDict(String fn);

  void deleteDictEnumExt(String fn);

  void deleteDictSqlExt(String fn);

  @Transactional(rollbackFor = Exception.class)
  void deleteAllDictConfig(String fn);

  List<DictInfo> queryAllDict(int pageNum, int pageSize, DictKeyInfo key);

  DictInfo queryDictPairs(String fn);

  DictInfo queryDict(String fn);
}
