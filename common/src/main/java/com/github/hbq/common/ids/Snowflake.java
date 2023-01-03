package com.github.hbq.common.ids;

import com.github.hbq.common.utils.StrUtils;
import com.github.hbq.common.utils.UidBox;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hbq
 * @see <a href="https://github.com/beyondfengyu/SnowFlake">基于Twitter的雪花算法SnowFlake实现重新优化改造实现</a>
 */
@Slf4j
public class Snowflake {

  /**
   * 起始的时间戳，可以修改为服务第一次启动的时间 一旦服务已经开始使用，起始时间戳就不应该改变
   */
  private long startMill = 1262275200000L;

  /**
   * 序列号占用的位数
   */
  private final static long SEQUENCE_BIT = 12;

  /**
   * 机器标识占用的位数
   */
  private final static int WORK_ID_BIT = 10;

  /**
   * 最大服务器ID
   */
  public final static long MAX_WORK_ID_NUM = 0x3FF;

  /**
   * 单位毫秒、服务器的最大序列号
   */
  private final static long MAX_SEQUENCE = 0xFFF;

  /**
   * 服务器ID存放左移位数
   */
  private final static long WORK_ID_LEFT = SEQUENCE_BIT;

  /**
   * 时间值存放左移位数
   */
  private final static long TIMESTMP_LEFT = WORK_ID_BIT + SEQUENCE_BIT;

  /**
   * 服务器ID
   */
  private long workId;

  /**
   * 序列号
   */
  private volatile long sequence = 0L;

  /**
   * 上一次时间戳
   */
  private volatile long lastStmp = -1L;

  public Snowflake() {
    try {
      InetAddress ip = InetAddress.getLocalHost();
      NetworkInterface network = NetworkInterface.getByInetAddress(ip);
      byte[] mb = network.getHardwareAddress();
      String mac = StrUtils.byteArrToHex(mb);
      long mid = mac.hashCode() & MAX_WORK_ID_NUM;
      log.info("机器mac: {}, 机器编号: {}, 最大机器数: {}", mac, mid, MAX_WORK_ID_NUM);
      this.workId = mid;
    } catch (UnknownHostException | SocketException e) {
      this.workId = UidBox.uid().hashCode() & MAX_WORK_ID_NUM;
    }
  }

  public Snowflake(long startMill) {
    this();
    if (startMill < 0) {
      throw new IllegalArgumentException("startMill cat't be less than 0");
    }
    this.startMill = startMill;
  }

  /**
   * 通过单例模式来获取实例 分布式部署服务时，数据节点标识和机器标识作为联合键必须唯一
   *
   * @param workId 服务器ID
   */
  public Snowflake(int workId) {
    if (workId > MAX_WORK_ID_NUM || workId < 0) {
      throw new IllegalArgumentException("workId can't be greater than MAX_WORKID_NUM or less than 0");
    }
    this.workId = workId;
  }

  /**
   * 通过单例模式来获取实例 分布式部署服务时，数据节点标识和机器标识作为联合键必须唯
   *
   * @param startMill 开始时间戳
   * @param workId    数据节点标识ID
   */
  public Snowflake(long startMill, int workId) {
    this(workId);
    if (startMill < 0) {
      throw new IllegalArgumentException("startMill cat't be less than 0");
    }
    this.startMill = startMill;
  }

  /**
   * 产生下一个ID
   *
   * @return
   */
  public synchronized long nextId() {
    long currStmp = getNewstmp();
    if (currStmp < lastStmp) {
      throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
    }

    if (currStmp == lastStmp) {
      //相同毫秒内，序列号自增
      sequence = (sequence + 1) & MAX_SEQUENCE;
      //同一毫秒的序列数已经达到最大
      if (sequence == 0L) {
        currStmp = getNextMill();
      }
    } else {
      //不同毫秒内，序列号置为0
      sequence = 0L;
    }

    lastStmp = currStmp;

    return (currStmp - startMill) << TIMESTMP_LEFT
        | workId << WORK_ID_LEFT
        | sequence;
  }

  private long getNextMill() {
    long mill = getNewstmp();
    while (mill <= lastStmp) {
      mill = getNewstmp();
    }
    return mill;
  }

  private long getNewstmp() {
    return System.currentTimeMillis();
  }
}
