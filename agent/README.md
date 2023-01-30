

# 指标采集

## 依赖

```xml
<dependency>
    <groupId>com.github.hbq</groupId>
    <artifactId>agent</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```



## jvm指标采集

```properties
# 开启指标采集
hbq.agent.enable=true
# 启用jvm指标采集
hbq.agent.jvm.enable=true
hbq.agent.jvm.cycle.time=30
hbq.agent.jvm.cycle.unit=SECONDS

# 额外配置项，不配置默认为空串
# 实例所在数据中心
hbq.agent.data-center=dc
# 应用描述
spring.application.desc=
```

## 应用kafka指标采集

```properties
# 开启指标采集
hbq.agent.enable=true
# 启用应用kafka指标采集
hbq.agent.kafka.enable=true
hbq.agent.kafka.cycle.time=30
hbq.agent.kafka.cycle.unit=SECONDS

# 额外配置项
# 是否自动采集
hbq.agent.kafka.auto-collect.enable=true
# 入口消息限速
hbq.agent.kafka.in.rate-limiter=50000
# 指定指标数据推送的kafka集群配置
hbq.agent.kafka.configs={"bootstrap.servers":["192.168.56.2:9092"],"value.serializer":"com.github.hbq.agent.app.serv.impl.kafka.QuotaDataSerializer"}
```



## 自定义指标扩展 

继承 `com.github.hbq.agent.app.serv.AbstractQuotaDataGet` 实现以下四个方法即可

```java
@Component
public class DemoQuotaDataGet extends AbstractQuotaDataGet {

  // 采集指标
  @Override
  protected Collection<QuotaData> collectData(InstInfo instance) {
    CycleInfo cycleInfo = cycle();
    QuotaInfo qi = new QuotaInfo(instance, 
                                 "app,jvm,rate_heapmemory", 
                                 "应用指标,jvm,堆内存占用率", 
                                 "%", cycleInfo, Type.Data);
    QuotaData data = new QuotaData(qi);
    data.collectData(new DataInfo(83.2, ""), FormatTime.nowSecs());
    return Lists.newArrayList(data);
  }

  // 定义指标
  @Override
  protected Collection<QuotaInfo> registry(InstInfo instance) {
    CycleInfo cycleInfo = cycle();
    QuotaInfo qi = new QuotaInfo(instance, 
                                 "app,jvm,rate_heapmemory", 
                                 "应用指标,jvm,堆内存占用率", 
                                 "%", cycleInfo, Type.Data);
    return Lists.newArrayList(qi);
  }
    
  // 指标采集器名称
  @Override
  public String identify() {
    CycleInfo c = cycle();
    return String.join("", "Demo指标采集器[", c.getKey(), "]");
  }

  // 指标采集周期
  @Override
  public CycleInfo cycle() {
    return CycleInfo.SECOND30;
  }
}
```



## 采集指标上报的报文

主题：`HBQ-AGENT-QUOTA-DATA`

```json
[{
    "collectTime": 1674535955,
    "data": {
        "desc": "",
        "fmtValue": 5.54
    },
    "fmtCollectTime": "2023-01-24 12:52:35",
    "quota": {
        "cycleInfo": {
            "key": "SECONDS:30",
            "time": 30,
            "unit": "SECONDS"
        },
        "desc": "应用指标,jvm,堆内存占用率",
        "instInfo": {
            "app": {
                "desc": "manage",
                "fmtRegTime": "2023-01-24 12:52:05",
                "key": "manage",
                "name": "manage",
                "regTime": 1674535925,
                "tags": {}
            },
            "dataCenter": "dc",
            "fmtRegTime": "2023-01-24 12:52:05",
            "hostName": "1a666b43aa67",
            "ip": "192.168.56.1",
            "key": "manage,dc,192.168.56.1,21001",
            "port": 21001,
            "processNo": "13428",
            "regTime": 1674535925,
            "tags": {}
        },
        "name": "app,jvm,rate_heapmemory",
        "type": "Data"
    }
}]
```



## 扩展性

可使用此扩展编写模块 实现对主机、中间件、数据库等指标采集。
