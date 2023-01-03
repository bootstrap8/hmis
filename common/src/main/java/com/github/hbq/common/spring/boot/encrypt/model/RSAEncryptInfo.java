package com.github.hbq.common.spring.boot.encrypt.model;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;
import lombok.Data;
import org.apache.commons.lang.StringUtils;

/**
 * @author hbq
 */
@Data
public class RSAEncryptInfo<T> {

  private String publicKey;
  private T obj;
  private String charset = "utf-8";

  public boolean valid() {
    return StringUtils.isNotBlank(publicKey) && !Objects.isNull(obj);
  }

  @Override
  public String toString() {
    return JSON.toJSONString(this, true);
  }
}
