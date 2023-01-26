package com.github.hbq.common.dict.map;

import com.alibaba.fastjson.JSON;
import com.github.hbq.common.dict.Dict;
import com.github.hbq.common.dict.DictInfo;
import com.github.hbq.common.dict.DictPair;
import com.github.hbq.common.dict.TranPolicy;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author hbq
 */
@Slf4j
public class MapDictImpl implements Dict<Map> {

  private volatile Map<String, DictInfo> dictMap = new LinkedHashMap<>(100);
  private volatile List<String> fns = new ArrayList<>(100);

  @Autowired
  private JdbcTemplate jt;

  @Scheduled(cron = "${hbq.common.dict.reload.cron:0 0 * * * *}")
  public void schedule() {
    reloadImmediately();
  }

  @PostConstruct
  @Override
  public synchronized void reloadImmediately() {
    log.debug("开始加载字典信息");
    dictMap.clear();
    fns = null;
    try {
      jt.query("select field_name,field_desc,enum_type from hbq_dic_info order by field_name", (rs) -> {
        DictInfo dict = new DictInfo();
        dict.setFieldName(rs.getString(1));
        dict.setFieldDesc(rs.getString(2));
        dict.setEnumType(rs.getString(3));
        dictMap.put(dict.getFieldName(), dict);
        try {
          if (dict.isSQLEnumType()) {
            String sql = "select enum_sql AS \"sql\" from hbq_dic_ext_sql where field_name=?";
            log.debug("加载[{}], [{}]字典信息", dict.getFieldName(), sql);
            try {
              Map sqlMap = jt.queryForMap(sql, new Object[]{dict.getFieldName()}, new int[]{Types.VARCHAR});
              jt.query(MapUtils.getString(sqlMap, "sql"), (resultSet) -> {
                DictPair pair = new DictPair();
                pair.setKey(resultSet.getString("key"));
                pair.setValue(resultSet.getString("value"));
                dict.addPair(pair);
              });
            } catch (DataAccessException e) {
              return;
            }
          } else if (dict.isOrdinaryEnumType()) {
            String sql = "select enum_key,enum_value from hbq_dic_ext_kv where field_name=?";
            log.debug("加载[{}], [{}]字典信息", dict.getFieldName(), sql);
            jt.query(sql, ps -> {
              ps.setString(1, dict.getFieldName());
            }, resultSet -> {
              DictPair pair = new DictPair();
              pair.setKey(resultSet.getString(1));
              pair.setValue(resultSet.getString(2));
              dict.addPair(pair);
            });
          }
          log.debug("加载[{}]字典信息, 枚举数量: {}", dict.getFieldName(), dict.getPairSize());
        } catch (Exception e) {
          log.error("加载字典[{}]异常: {}", dict.getFieldName(), e.getMessage());
        }
      });
    } catch (Exception e) {
      log.error("加载字典异常", e);
    }
    log.debug("加载字典信息结束");
    fns = dictMap.keySet().stream().collect(Collectors.toList());
    if (log.isDebugEnabled()) {
      log.debug("加载到的字典信息: {}", JSON.toJSONString(dictMap));
      log.debug("加载到的字典字段信息: {}", JSON.toJSONString(fns));
    }
  }

  @Override
  public Optional<DictInfo> queryDict(String fieldName) {
    return Optional.ofNullable(dictMap.get(fieldName));
  }

  @Override
  public boolean isDict(String fieldName) {
    return queryDict(fieldName).isPresent();
  }

  @Override
  public Set<DictInfo> allDict() {
    return Collections.unmodifiableSet(dictMap.values().stream().collect(Collectors.toSet()));
  }

  @Override
  public void tranForKey(Map data, String fieldName, boolean nullOver) {
    Optional<DictInfo> p = queryDict(fieldName);
    if (p.isPresent()) {
      DictInfo dict = p.get();
      dict.tranForKey(data, fieldName, nullOver);
    }
  }

  @Override
  public void tranForVal(Map data, String fieldName, boolean nullOver) {
    Optional<DictInfo> p = queryDict(fieldName);
    if (p.isPresent()) {
      DictInfo dict = p.get();
      dict.tranForVal(data, fieldName, nullOver);
    }
  }

  @Override
  public void tranAnyForKey(Map data, boolean nullOver) {
    for (String fn : fns) {
      if (data.containsKey(fn)) {
        Optional<DictInfo> p = queryDict(fn);
        if (p.isPresent()) {
          DictInfo dict = p.get();
          dict.tranForKey(data, fn, nullOver);
        }
      }
    }
  }

  @Override
  public void tranAnyForVal(Map data, boolean nullOver) {
    for (String fn : fns) {
      if (data.containsKey(fn)) {
        Optional<DictInfo> p = queryDict(fn);
        if (p.isPresent()) {
          DictInfo dict = p.get();
          dict.tranForVal(data, fn, nullOver);
        }
      }
    }
  }

  @Override
  public void tranAnyForPolicy(Map data, List<TranPolicy> policies, boolean nullOver) {
    Map<String, TranPolicy> pm = policies.stream()
        .collect(Collectors.toMap(t -> t.getFieldName(), t -> t, (t1, t2) -> t2));
    TranPolicy policy;
    for (String fn : fns) {
      if (!data.containsKey(fn)) {
        continue;
      }
      Optional<DictInfo> p = queryDict(fn);
      if (!p.isPresent()) {
        continue;
      }
      DictInfo dict = p.get();
      policy = pm.get(dict.getFieldName());
      if (Objects.nonNull(policy)) {
        if (policy.isTranWithKey()) {
          dict.tranForKey(data, fn, nullOver);
        } else {
          dict.tranForVal(data, fn, nullOver);
        }
      } else {
        dict.tranForVal(data, fn, nullOver);
      }
    }
  }

  public void setJt(JdbcTemplate jt) {
    this.jt = jt;
  }
}
