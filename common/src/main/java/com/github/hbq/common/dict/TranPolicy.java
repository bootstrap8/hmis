package com.github.hbq.common.dict;

import lombok.Data;

/**
 * @author hbq
 */
@Data
public class TranPolicy {

  private String fieldName;
  private TranType tranType;

  public boolean isTranWithKey() {
    return TranType.KEY == tranType;
  }

  public static enum TranType {
    KEY, VALUE
  }
}
