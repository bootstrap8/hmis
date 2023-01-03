package com.github.hbq.common.dict;

import java.util.Objects;
import lombok.Data;

/**
 * @author hbq
 */
@Data
public class DictPair {

  private String key;
  private String value;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DictPair dictPair = (DictPair) o;
    return Objects.equals(getKey(), dictPair.getKey()) &&
        Objects.equals(getValue(), dictPair.getValue());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getKey(), getValue());
  }

}
