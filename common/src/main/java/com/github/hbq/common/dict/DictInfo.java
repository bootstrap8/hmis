package com.github.hbq.common.dict;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;

/**
 * @author hbq
 */
public class DictInfo {

  private String fieldName;
  private String fieldDesc;
  private String enumType;

  private String enumSql;
  private Set<DictPair> pairs = new LinkedHashSet<>(16);
  private Map<String, String> keyMap = new LinkedHashMap<>(16);
  private Map<String, String> valMap = new LinkedHashMap<>(16);

  public void addPair(DictPair pair) {
    pairs.add(pair);
    keyMap.put(pair.getKey(), pair.getValue());
    valMap.put(pair.getValue(), pair.getKey());
  }

  public void addPair(List<DictPair> pairs) {
    if (CollectionUtils.isNotEmpty(pairs)) {
      for (DictPair pair : pairs) {
        addPair(pair);
      }
    }
  }

  public void tranForKey(Map data, String fieldName, boolean nullOver) {
    String key = MapUtils.getString(data, fieldName);
    if (Objects.isNull(key)) {
      return;
    }
    String val = keyMap.get(key);
    if (Objects.isNull(val) && !nullOver) {
      return;
    }
    data.put(fieldName, val);
  }

  public void tranForVal(Map data, String fieldName, boolean nullOver) {
    String key = MapUtils.getString(data, fieldName);
    if (Objects.isNull(key)) {
      return;
    }
    String val = valMap.get(key);
    if (Objects.isNull(val) && !nullOver) {
      return;
    }
    data.put(fieldName, val);
  }

  public boolean isSQLEnumType() {
    return "sql".equals(enumType);
  }

  public boolean isOrdinaryEnumType() {
    return "enum".equals(enumType);
  }

  public boolean hasPairs() {
    return CollectionUtils.isNotEmpty(pairs);
  }

  public Set<DictPair> getPairs() {
    return pairs;
  }

  public int getPairSize() {
    return pairs.size();
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public String getFieldDesc() {
    return fieldDesc;
  }

  public void setFieldDesc(String fieldDesc) {
    this.fieldDesc = fieldDesc;
  }

  public String getEnumType() {
    return enumType;
  }

  public void setEnumType(String enumType) {
    this.enumType = enumType;
  }

  public String getEnumSql() {
    return enumSql;
  }

  public void setEnumSql(String enumSql) {
    this.enumSql = enumSql;
  }
}
