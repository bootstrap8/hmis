package com.github.hbq.manage.agent;

import com.github.hbq.agent.app.pojo.AppInfo;
import com.github.hbq.agent.app.pojo.DataInfo;
import com.github.hbq.agent.app.pojo.InstInfo;
import com.github.hbq.agent.app.pojo.QuotaData;
import com.github.hbq.agent.app.pojo.QuotaInfo;
import com.github.hbq.agent.app.serv.impl.kafka.QuotaKafkaTemplate;
import com.github.hbq.common.spring.boot.ctrl.Result;
import com.github.hbq.common.spring.boot.ctrl.Version;
import com.github.hbq.common.utils.FormatTime;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hbq
 */
//@RestController
@Slf4j
public class KafkaOutCtrl {

  @Autowired
  private QuotaKafkaTemplate kafka;

  @ApiOperation("测试消息发送")
  @Version("v1.0")
  @RequestMapping(path = "/agent/kafka/send/{v}", method = RequestMethod.GET)
  @ResponseBody
  public Result<?> sendMsg(@RequestHeader(name = "userInfo", required = false) String userInfo,
      @ApiParam(required = true, defaultValue = "v1.0") @PathVariable String v, @RequestParam(name = "size", defaultValue = "1") int size) {
    log.info("测试消息发送");
    try {
      for (int i = 0; i < size; i++) {
        QuotaData qd = new QuotaData(new QuotaInfo(new InstInfo(new AppInfo("manage", ""), "dc", "0.0.0.0", "localhost", 22222, "2345"), "kpi", "测试指标", "$"));
        qd.collectData(new DataInfo(3.5d, ""), FormatTime.nowSecs());
        kafka.push("HBQ-AGENT-QUOTA-DATA", null,
            Lists.newArrayList(qd));
      }
      return Result.suc("发送成功");
    } catch (Exception e) {
      log.error("测试消息发送异常", e);
      return (e instanceof RuntimeException) ? Result.fail(e.getMessage()) : Result.fail("测试消息发送异常");
    }
  }
}
