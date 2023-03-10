package com.github.hbq.manage.dict.serv.impl;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSON;
import com.github.hbq.common.dict.DictInfo;
import com.github.hbq.common.dict.DictPair;
import com.github.hbq.manage.dict.dao.DictDao;
import com.github.hbq.manage.dict.pojo.DictKeyInfo;
import com.github.hbq.manage.dict.serv.DictService;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * @author hbq
 */
@Service("hbq-manage-dict-serv-DictServiceImpl")
public class DictServiceImpl implements DictService, InitializingBean {

  @Autowired(required = false)
  private KafkaTemplate kafka;

  @Autowired
  private DictDao dao;

  @Override
  public void afterPropertiesSet() throws Exception {
    createDictTable();
  }

  @Override
  public void saveDict(DictInfo dict) {
    dao.deleteDictEnumExt(dict.getFieldName());
    dao.deleteDictSqlExt(dict.getFieldName());
    dao.deleteDict(dict.getFieldName());
    dao.saveDict(dict);
    if (dict.isOrdinaryEnumType()) {
      // 保存手工枚举配置
      if (dict.hasPairs()) {
        for (DictPair pair : dict.getPairs()) {
          dao.saveDictEnumExt(dict.getFieldName(), pair);
        }
      }
    }
    if (dict.isSQLEnumType()) {
      // 保存sql枚举配置
      dao.saveDictSqlExt(dict.getFieldName(), dict.getEnumSql());
    }
  }

  @Override
  public void saveDictEnumExt(DictInfo dict) {
    try {
      if (!dict.hasPairs()) {
        return;
      }
      dao.deleteDictEnumExt(dict.getFieldName());
      for (DictPair pair : dict.getPairs()) {
        dao.saveDictEnumExt(dict.getFieldName(), pair);
      }
      if (Objects.nonNull(kafka)) {
        kafka.send("HBQ-COMMON-DICT-CHANGE", JSON.toJSONString(dict));
      }
    } catch (Exception e) {
      throw new RuntimeException(e.getCause());
    }
  }

  @Override
  public void saveDictSqlExt(DictInfo dict) {
    try {
      dao.deleteDictSqlExt(dict.getFieldName());
      dao.saveDictSqlExt(dict.getFieldName(), dict.getEnumSql());
      if (Objects.nonNull(kafka)) {
        kafka.send("HBQ-COMMON-DICT-CHANGE", JSON.toJSONString(dict));
      }
    } catch (Exception e) {
      throw new RuntimeException(e.getCause());
    }
  }

  @Override
  public void deleteDict(String fn) {
    dao.deleteDict(fn);
    if (Objects.nonNull(kafka)) {
      kafka.send("HBQ-COMMON-DICT-CHANGE", fn);
    }
  }

  @Override
  public void deleteDictEnumExt(String fn) {
    dao.deleteDictEnumExt(fn);
    if (Objects.nonNull(kafka)) {
      kafka.send("HBQ-COMMON-DICT-CHANGE", fn);
    }
  }

  @Override
  public void deleteDictSqlExt(String fn) {
    dao.deleteDictSqlExt(fn);
    if (Objects.nonNull(kafka)) {
      kafka.send("HBQ-COMMON-DICT-CHANGE", fn);
    }
  }

  @Override
  public void deleteAllDictConfig(String fn) {
    try {
      dao.deleteDict(fn);
      dao.deleteDictEnumExt(fn);
      dao.deleteDictSqlExt(fn);
      if (Objects.nonNull(kafka)) {
        kafka.send("HBQ-COMMON-DICT-CHANGE", fn);
      }
    } catch (Exception e) {
      throw new RuntimeException(e.getCause());
    }
  }

  @Override
  public List<DictInfo> queryAllDict(int pageNum, int pageSize, DictKeyInfo key) {
    return dao.queryAllDict(new RowBounds(pageNum, pageSize), key);
  }

  @Override
  public DictInfo queryDictPairs(String fn) {
    DictInfo dict = queryDict(fn);
    Assert.notNull(dict, "未查询到字典基本信息");
    List<DictPair> pairs = dao.queryDictPairs(fn);
    dict.addPair(pairs);
    return dict;
  }

  @Override
  public DictInfo queryDict(String fn) {
    DictInfo dict = dao.queryDict(fn);
    Optional.ofNullable(dict).ifPresent(d -> {
      DictInfo ext = dao.queryDictSqlExt(fn);
      if (Objects.nonNull(ext)) {
        d.setEnumSql(ext.getEnumSql());
      }
    });
    return dict;
  }

  private void createDictTable() {
    try {
      dao.createDictInfo();
    } catch (Exception e) {
    }
    try {
      dao.createDictExtKv();
    } catch (Exception e) {
    }
    try {
      dao.createDictExtSql();
    } catch (Exception e) {
    }
  }
}
