package com.github.hbq.common.dict;

import com.github.hbq.common.dict.map.MapDictImpl;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author hbq
 */
public class SelectImpl implements Select {

  @Autowired
  private MapDictImpl mapDic;

  @Override
  public List<Map<String, Object>> querySelectList(boolean multiSelect, String dicName, String showType, String queryOrSaveType) {
    return querySelectList(multiSelect, dicName, showType, queryOrSaveType, "");
  }

  @Override
  public List<Map<String, Object>> querySelectList(boolean multiSelect, String dicName, String showType, String queryOrSaveType, String defaultSelectValue) {
    Optional<DictInfo> p = mapDic.queryDict(dicName);
    if (!p.isPresent()) {
      return Collections.emptyList();
    }
    DictInfo dict = p.get();
    List<Map<String, Object>> result = new ArrayList<>(dict.getPairSize());
    if (!multiSelect) {
      result.add(ImmutableMap.of("label", "请选择", "value", defaultSelectValue));
    }
    for (DictPair pair : dict.getPairs()) {
      result.add(ImmutableMap.of("label", "key".equals(showType) ? pair.getKey() : pair.getValue(), "value",
          "key".equals(queryOrSaveType) ? pair.getKey() : pair.getValue()));
    }
    return result;
  }
}
