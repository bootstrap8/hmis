package com.github.hbq.common.dict.map;

import com.github.hbq.common.dict.Dict;
import com.github.hbq.common.dict.DictInfo;
import com.github.hbq.common.dict.TranPolicy;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author hbq
 */
public class ListDictImpl implements Dict<List<Map>> {

  @Autowired
  private MapDictImpl mapDict;

  @Override
  public void reloadImmediately() {
    mapDict.reloadImmediately();
  }

  @Override
  public Optional<DictInfo> queryDict(String fieldName) {
    return mapDict.queryDict(fieldName);
  }

  @Override
  public boolean isDict(String fieldName) {
    return mapDict.isDict(fieldName);
  }

  @Override
  public Set<DictInfo> allDict() {
    return mapDict.allDict();
  }

  @Override
  public void tranForKey(List<Map> data, String fieldName, boolean nullOver) {
    for (Map d : data) {
      mapDict.tranForKey(d, fieldName, nullOver);
    }
  }

  @Override
  public void tranForVal(List<Map> data, String fieldName, boolean nullOver) {
    for (Map d : data) {
      mapDict.tranForVal(d, fieldName, nullOver);
    }
  }

  @Override
  public void tranAnyForKey(List<Map> data, boolean nullOver) {
    for (Map d : data) {
      mapDict.tranAnyForKey(d, nullOver);
    }
  }

  @Override
  public void tranAnyForVal(List<Map> data, boolean nullOver) {
    for (Map d : data) {
      mapDict.tranAnyForVal(d, nullOver);
    }
  }

  @Override
  public void tranAnyForPolicy(List<Map> data, List<TranPolicy> policies, boolean nullOver) {
    for (Map d : data) {
      mapDict.tranAnyForPolicy(d, policies, nullOver);
    }
  }

  public void setMapDict(MapDictImpl mapDict) {
    this.mapDict = mapDict;
  }
}
