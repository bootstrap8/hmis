package com.github.hbq.monitor.notify.sms;

import com.apistd.uni.Uni;
import com.apistd.uni.UniException;
import com.apistd.uni.UniResponse;
import com.apistd.uni.sms.UniMessage;
import com.apistd.uni.sms.UniSMS;
import com.github.hbq.monitor.notify.INotify;
import com.github.hbq.monitor.notify.proxy.NotifyProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * @author hbq
 */
@ConditionalOnProperty(prefix = "hbq.monitor.notify", name = "type", havingValue = "uni")
@Component
@Slf4j
public class UniNotifyImpl implements INotify, InitializingBean {

  @Autowired
  private NotifyProxy proxy;

  @Value("${hbq.monitor.notify.uni.access-key-id}")
  private String accessKeyId;

  @Value("${hbq.monitor.notify.uni.signature}")
  private String signature;

  @Override
  public void afterPropertiesSet() throws Exception {
    proxy.registry(identify(), this);
    // 初始化
    // 若使用简易验签模式仅传入第一个参数即可
    Uni.init(accessKeyId);
  }

  @Override
  public boolean send(String[] phoneNums, String msg) {
    log.info("通知短信内容: {}", msg);
    // 构建信息
    UniMessage message = UniSMS.buildMessage()
        .setTo(phoneNums)
        .setSignature(signature)
        .setContent(msg);
    try {
      UniResponse res = message.send();
      log.info("发送结果: {}", res);
      if ("0".equals(res.code)) {
        return true;
      }
    } catch (UniException e) {
      log.error("", e);
    }
    return false;
  }

  @Override
  public String identify() {
    return "uni";
  }
}
