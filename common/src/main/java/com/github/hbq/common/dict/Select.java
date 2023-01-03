package com.github.hbq.common.dict;

import java.util.List;
import java.util.Map;

/**
 * @author hbq
 */
public interface Select {

  List<Map<String, Object>> querySelectList(boolean multiSelect, String dicName, String showType, String queryOrSaveType);

  List<Map<String, Object>> querySelectList(boolean multiSelect, String dicName, String showType,
      String queryOrSaveType, String defaultSelectValue);
}
