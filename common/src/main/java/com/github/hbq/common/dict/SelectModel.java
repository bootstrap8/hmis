package com.github.hbq.common.dict;

import com.github.hbq.common.utils.StrUtils;
import lombok.Data;

/**
 * @author hbq
 */
@Data
public class SelectModel {

  private boolean multiSelect = true;
  private String dicName;
  private String showType = "value";
  private String queryOrSaveType = "key";
  private String defaultSelectValue = "";

  public void check() {
    if (StrUtils.strEmpty(dicName)) {
      throw new IllegalArgumentException("dicName不能为空");
    }
  }
}
