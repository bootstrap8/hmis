package com.github.hbq.common.utils;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author hbq
 */
public abstract class Count {

  public static Count safe() {
    return new SafeCount();
  }

  public static Count unsafe() {
    return new UnSafeCount();
  }

  public abstract void reset();

  /**
   * 是否初始值
   *
   * @return
   */
  public abstract boolean isInitValue();

  /**
   * 累加并返回计算后的值
   *
   * @return
   */
  public abstract long incrementAndGet();

  /**
   * 减值并返回计算后的值
   *
   * @return
   */
  public abstract long decrementAndGet();

  /**
   * 累加指定值并返回计算后的值
   *
   * @param c
   * @return
   */
  public abstract long incrementAndGet(long c);

  /**
   * 减少指定值并返回计算后的值
   *
   * @param c
   * @return
   */
  public abstract long decrementAndGet(long c);

  /**
   * 比较并且重置初始值
   *
   * @param actual
   * @return
   */
  public abstract boolean compareAndInit(long actual);

  /**
   * 比较并且重置初始值
   *
   * @param actual
   * @param elapsedMills
   * @return
   */
  public abstract boolean compareAndInit(long actual, long elapsedMills);

  /**
   * 比较余数是否为0
   *
   * @param actual
   * @return
   */
  public abstract boolean compare(long actual);

  /**
   * 比较余数是否为0
   *
   * @param actual
   * @param elapsedMills
   * @return
   */
  public abstract boolean compare(long actual, long elapsedMills);

  /**
   * 返回当前值
   *
   * @return
   */
  public abstract long longValue();

  /**
   * 返回当前值整数
   *
   * @return
   */
  public abstract int intValue();

  /**
   * 是否在分页范围内
   *
   * @param startRow
   * @param endRow
   * @return
   */
  public abstract boolean pageScope(int startRow, int endRow);

  private static class SafeCount extends Count {

    private AtomicLong count = new AtomicLong(0);

    private long time = FormatTime.nowMills();

    private synchronized void updateTime() {
      this.time = FormatTime.nowMills();
    }

    private synchronized long elapsed() {
      return FormatTime.nowMills() - this.time;
    }

    @Override
    public void reset() {
      count.set(0);
      updateTime();
    }

    @Override
    public boolean isInitValue() {
      return count.get() == 0;
    }

    @Override
    public long incrementAndGet() {
      return count.incrementAndGet();
    }

    @Override
    public long incrementAndGet(long c) {
      return count.addAndGet(c);
    }

    @Override
    public long decrementAndGet() {
      return count.decrementAndGet();
    }

    @Override
    public long decrementAndGet(long c) {
      return count.addAndGet(-c);
    }

    @Override
    public boolean compareAndInit(long actual) {
      return count.compareAndSet(actual, 0);
    }

    @Override
    public boolean compareAndInit(long actual, long elapsed) {
      boolean r = (elapsed() >= elapsed);
      if (r) {
        updateTime();
      }
      return r || compareAndInit(actual);
    }

    @Override
    public boolean compare(long actual) {
      return count.get() % actual == 0;
    }

    @Override
    public boolean compare(long actual, long elapsed) {
      return (count.get() % actual == 0) || (elapsed() == elapsed);
    }

    @Override
    public long longValue() {
      return count.get();
    }

    @Override
    public int intValue() {
      return (int) longValue();
    }

    @Override
    public boolean pageScope(int startRow, int endRow) {
      int c = intValue();
      return c >= startRow && c < endRow;
    }
  }

  private static class UnSafeCount extends Count {

    private long count = 0;

    private long time = FormatTime.nowMills();

    private void updateTime() {
      this.time = FormatTime.nowMills();
    }

    private long elapsed() {
      return FormatTime.nowMills() - this.time;
    }

    @Override
    public void reset() {
      count = 0;
      updateTime();
    }

    @Override
    public boolean isInitValue() {
      return count == 0;
    }

    @Override
    public long incrementAndGet() {
      return ++count;
    }

    @Override
    public long incrementAndGet(long c) {
      count += c;
      return count;
    }

    @Override
    public long decrementAndGet() {
      return --count;
    }

    @Override
    public long decrementAndGet(long c) {
      count -= c;
      return count;
    }

    @Override
    public boolean compareAndInit(long actual) {
      boolean r = count == actual;
      if (r) {
        count = 0;
      }
      return r;
    }

    @Override
    public boolean compareAndInit(long actual, long elapsed) {
      boolean r = (elapsed() >= elapsed);
      if (r) {
        updateTime();
      }
      return r || compareAndInit(actual);
    }

    @Override
    public boolean compare(long actual) {
      return count % actual == 0;
    }

    @Override
    public boolean compare(long actual, long elapsed) {
      return false;
    }

    @Override
    public long longValue() {
      return count;
    }

    @Override
    public int intValue() {
      return (int) longValue();
    }

    @Override
    public boolean pageScope(int startRow, int endRow) {
      int c = intValue();
      return c >= startRow && c < endRow;
    }
  }
}
