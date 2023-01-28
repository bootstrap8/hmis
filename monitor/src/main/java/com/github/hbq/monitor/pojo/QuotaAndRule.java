package com.github.hbq.monitor.pojo;

import com.alibaba.fastjson.JSON;
import com.github.hbq.agent.app.pojo.QuotaData;
import com.github.hbq.agent.app.pojo.QuotaInfo;
import com.github.hbq.common.rule.map.ChainRule;
import com.github.hbq.common.spring.context.SpringContext;
import com.github.hbq.common.utils.Count;
import com.github.hbq.common.utils.FormatTime;
import com.google.common.base.Joiner;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * @author hbq
 */
@Data
@Slf4j
public class QuotaAndRule {

  private QuotaInfo quota;
  private QuotaWarnRuleInfo warn;
  private ChainRule chain;
  private Count count = Count.unsafe();
  private long lastNotifyTime = FormatTime.nowSecs();
  private boolean notified = false;

  public QuotaAndRule(QuotaInfo quota, QuotaWarnRuleInfo warn) {
    this.quota = quota;
    this.warn = warn;
    this.chain = new ChainRule(warn.getRule());
  }

  public String getKey() {
    return quota.getInstQuotaKey();
  }

  public boolean accept(SpringContext context, QuotaData qd) {
    boolean r = false;
    if (chain.accept(qd.map())) {
      log.info("指标匹配上规则, 指标数据: {}, 规则内容: {}",
          JSON.toJSONString(qd), JSON.toJSONString(warn));
      count.incrementAndGet();
    } else {
      count.reset();
    }
    if (warn.acceptCount(count.intValue())) {
      // 首次通知
      if (!notified) {
        log.info("首次通知: {}", JSON.toJSONString(warn));
        updateNotifyStatus();
        r = true;
      }
      // 非首次通知需要等待超时时间
      else {
        if (warn.acceptUnFirstNotifyTimeout(lastNotifyTime)) {
          log.info("非首次通知: {}", JSON.toJSONString(warn));
          updateNotifyStatus();
          r = true;
        } else {
          log.info("满足通知条件，但不满足通知等待时间(配置等待超时: {} 秒, 实际等待超时: {} 秒)",
              warn.getNotifyTimeoutSec(), FormatTime.nowSecs() - lastNotifyTime);
        }
      }
    }
    if (!r) {
      log.info("指标满足检查次数要求: {} 次", count.intValue());
    }
    return r;
  }

  public String notifyContent(QuotaData qd) {
    return Joiner.on("").join("",
        "警告时间: ", FormatTime.YYYYMMDDHHMISS.withMills()
        , "\n应用实例: ", quota.getInstInfo().getKey()
        , "\n指标信息: ", quota.getQuotaKey()
        , "\n指标数据: ", qd.getData().getFmtValue()
        , "\n通知规则: ", warn.getRule()
        , "\n检查次数: ", warn.getCheckCount());
  }

  private void updateNotifyStatus() {
    count.reset();
    lastNotifyTime = FormatTime.nowSecs();
    notified = true;
  }
}
