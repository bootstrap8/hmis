package com.github.hbq.monitor.notify;

/**
 * @author hbq
 */
public interface INotify {

  /**
   * 发送通知消息
   *
   * @param phoneNums
   * @param msg
   * @return
   */
  boolean send(String[] phoneNums, String msg);

  /**
   * 定义标识
   *
   * @return
   */
  String identify();
}
