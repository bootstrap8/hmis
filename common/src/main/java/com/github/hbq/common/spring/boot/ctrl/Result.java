package com.github.hbq.common.spring.boot.ctrl;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.hbq.common.utils.FormatTime;
import java.util.Objects;

/**
 * @author hbq
 */
public class Result<T> {

  private String ver = "v1.0";
  private String time;
  private int code;
  private String msg;
  private T body;

  @JsonIgnore
  public boolean isSuccessful() {
    return 1 == code;
  }

  public boolean hasBody() {
    return Objects.nonNull(body);
  }

  public static <T> Result<T> suc(T body) {
    Result<T> result = new Result<>();
    result.code = 1;
    result.msg = "调用成功";
    result.body = body;
    result.time = FormatTime.YYYYMMDDHHMISS.withMills();
    return result;
  }

  public static <T> Result<T> fail(String msg) {
    Result<T> result = new Result<>();
    result.code = 0;
    result.msg = msg;
    result.body = null;
    result.time = FormatTime.YYYYMMDDHHMISS.withMills();
    return result;
  }

  @Override
  public String toString() {
    return JSONObject.toJSONString(this);
  }

  public String getVer() {
    return ver;
  }

  public Result<T> setVer(String ver) {
    this.ver = ver;
    return this;
  }

  public String getTime() {
    return time;
  }

  public Result<T> setTime(String time) {
    this.time = time;
    return this;
  }

  public int getCode() {
    return code;
  }

  public Result<T> setCode(int code) {
    this.code = code;
    return this;
  }

  public String getMsg() {
    return msg;
  }

  public Result<T> setMsg(String msg) {
    this.msg = msg;
    return this;
  }

  public T getBody() {
    return body;
  }

  public Result<T> setBody(T body) {
    this.body = body;
    return this;
  }
}
