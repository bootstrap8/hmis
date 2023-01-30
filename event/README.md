

## 说明

主要管理系统中控制类的消息，比如变更、通知之类的消息。此模块主要是将字符串格式的kafka消息转换成可读性 更强的`java` `pojo`对象。



## 事件监听

业务代码只需实现此接口即可 `com.github.hbq.event.handle.EventObserver`

```
public interface EventObserver {

  /**
   * 路由变化事件
   *
   * @param event
   */
  default void routeNotify(RouteEvent event) {
  }

  /**
   * 字典变化事件
   *
   * @param event
   */
  default void dictNotify(DictEvent event) {

  }

  /**
   * kafka入口消息限速事件
   *
   * @param event
   */
  default void kafkaRateLimiterNotify(KafkaInRateLimiterEvent event) {

  }
}
```



## 事件主题

| 主题                                     | 作用                    | 传播路径                     |
| ---------------------------------------- | ----------------------- | ---------------------------- |
| `HBQ-GATEWAY-ROUTE-CHANGE`               | 路由变更                | manage => gateway            |
| `HBQ-COMMON-DICT-CHANGE`                 | 字典变更                | manage => 需要关注的应用实例 |
| `HBQ-AGENT-KAFKA-IN-RATE-LIMITER-CHANGE` | `kafka`入口消息速率变更 | manage => 所有应用实例       |

