## RESTful接口版本控制

在`RestController`类中使用`@Version`标记方法

```java
@Version("v1.0")
@RequestMapping(path = "/foo/{v}", method = RequestMethod.GET)
@ResponseBody
public Object foo(@ApiParam(required = true, defaultValue = "v1.0") 
                  @PathVariable String v) {
    ....
    
}
```

```bash
curl -XGET /foo/v1.0
```



## RESTful接口返回包装器

采用`Result<?>`包装返回结果

```java
@Version("v1.0")
@RequestMapping(path = "/foo/{v}", method = RequestMethod.GET)
@ResponseBody
public Result<?> foo(@ApiParam(required = true, defaultValue = "v1.0") 
                  @PathVariable String v) {
    return Result.suc(...);
    
}
```



返回格式

```json
{
  "code": 1,
  "ver": "v1.0",
  "msg": "调用成功",
  "time": "yyyy-MM-dd HH:mm:ss",
  "body": ...
}
```



## 分布式锁使用

### 启用配置

```properties
# 启用分布式锁
hbq.common.lock.redis.enable=false

# redis单机模式
hbq.common.lock.redis.connect-timeout=5
hbq.common.lock.redis.timeout=5
hbq.common.lock.redis.lettuce.pool.max-active=20
hbq.common.lock.redis.lettuce.pool.max-idle=8
hbq.common.lock.redis.lettuce.pool.min-idle=0
hbq.common.lock.redis.lettuce.pool.max-wait=10000
hbq.common.lock.redis.password=passwd
hbq.common.lock.redis.host=localhost
hbq.common.lock.redis.port=6379

# redis哨兵模式
#hbq.common.lock.redis.sentinel.master=mymaster
#hbq.common.lock.redis.sentinel.password=passwd
#hbq.common.lock.redis.sentinel.nodes=localhost:16379,10.21.16.101:26379,10.21.16.101:36379

# redis集群模式
#hbq.common.lock.redis.password=passwd
#hbq.common.lock.redis.cluster.nodes=localhost:6379,10.21.16.101:6380,10.21.16.101:6381
```

以上三种模式根据实际环境redis部署模式进行配置。



### 使用说明

#### 注解申明

在方法上添加注解 @DLock，默认将方法的全限定名作为锁的KEY，此方式为阻塞方式。

```java
public class FooServiceImpl implements FooService {

  @DLock
  @Override
  public String bar(String name) {
    ...
  }
}
```



DLock参数说明

|    参数    |       说明       |        默认值        |
| :--------: | :--------------: | :------------------: |
|  lockKey   |      锁KEY       | 默认：方法的全限定名 |
|   expire   |   锁过期时间值   |          30          |
|    unit    |  锁过期时间单位  |   TimeUnit.SECONDS   |
| retryCount |     重试次数     |          45          |
| retryCycle |  重试间隔时间值  |          1           |
| retryUnit  | 重试间隔时间单位 |   TimeUnit.SECONDS   |



#### 声明使用

```java
@Autowired
private Lock lock;
```



阻塞锁（悲观）

```java
String requestId = UidBox.uid();
try {
    if (lock.getLock(lockKey, requestId, 30, 1, TimeUnit.SECONDS)) {
        // 业务逻辑
    }
} finally {
    lock.releaseLock(lockKey, requestId);
}
```



非阻塞锁（乐观）

```java
String requestId = UidBox.uid();
try {
    if (lock.tryLock(lockKey, requestId, 30, TimeUnit.SECONDS)) {
        // 业务逻辑
    }
} finally {
    lock.releaseLock(lockKey, requestId);
}
```



## 任务统计脚手架代码

### 使用说明

使用示例

```java
package com.github.hbq.common.schedule.impl;

import com.github.hbq.common.schedule.ScheduleHelper;
import com.github.hbq.common.schedule.Time;
import com.github.hbq.common.schedule.annotation.Schedule;
import com.github.hbq.common.utils.RoundInfo;

/**
 * @author hbq
 */
public class XXXScheduler implements ScheduleHelper {

  @Schedule(name = "任务描述", cron = "* * * * *", time = Time.DAYS, offset = -1, align = true)
  @Override
  public void execute(RoundInfo roundInfo) {
    // 执行任务
  }

  @Override
  public void clean(RoundInfo roundInfo) {
    // 清理
  }

  @Override
  public void reform(RoundInfo roundInfo) {
    // 执行任务前准备工作
  }

  @Override
  public void init(RoundInfo roundInfo) {
    // 初始化工作
  }
}

```



注解参数说明

| **方法** | **用途**                                                     |
| -------- | ------------------------------------------------------------ |
| execute  | 统计逻辑实现方法                                             |
| clean    | 清理无用数据实现方法，例如统计程序只需要保留最近1个月的数据，那么可以在此方法中清理1个月前的数据 |
| reform   | 重建索引实现方法，因为统计表经常需要增删数据，所以提供了重建索引的方法入口，在此方法中添加索引重建的逻辑 |
| init     | 表初始化实现方法，把创建表的逻辑放在此方法中实现             |



调度方法注解 `@com.github.hbq.common.schedule.annotation.Schedule` 说明

| **注解参数** | **作用**                                                     |
| ------------ | ------------------------------------------------------------ |
| enable       | 是否启用统计调度 默认值：true 支持通过spring变量传递，例如: ${xxx} |
| name         | 给统计任务取个名字                                           |
| cron         | 任务调度crontab表达式，最小单位到分钟 默认值: * * * * * 支持通过spring变量传递，例如: ${xxx} |
| time         | 参考 com.github.hbq.common.schedule.Time 枚举类型 默认：Time.DAYS 定义统计时间范围单位，支持秒、分钟、小时、天、周、月、年 |
| align        | 时间是否对齐 默认：true                                      |
| offset       | 统计时间偏移量 默认：-1 例如 time=Time.DAY, align=true, offset=0  => 今天的0点到明天的0点 时间范围 time=Time.DAY, align=true, offset=-1 => 昨天的0点到今天的0点 时间范围 |



如果需要补数据，增加如下配置

```properties
hbq.schedule.repair.com.github.demo.schedule.XXScheduleHelperImpl.enable=false
hbq.schedule.repair.com.github.demo.schedule.XXScheduleHelperImpl.startTime=2020-01-01 09:00:33
hbq.schedule.repair.com.github.demo.schedule.XXScheduleHelperImpl.endTime=2020-01-10 12:23:59
```





## 字典管理

### 使用说明

+ 支持Map的转义


```java
@Autowired
private MapDictImpl mapDict;
```



+ 对`List<Map>` 转义


```java
@Autowired
private ListDictImpl listMapDict;
```



- 使用示例

```java
package com.github.hbq.common.dict;

import com.github.hbq.common.dict.map.ListDictImpl;
import com.github.hbq.common.dict.map.MapDictImpl;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author hbq
 */
public class DictTest {

  private static JdbcTemplate jt;
  private static MapDictImpl mapDict;
  private static ListDictImpl listDict;

  @BeforeClass
  public static void init() {
    BasicDataSource ds = new BasicDataSource();
    ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
    ds.setUrl("jdbc:mysql://192.168.56.2:3306/hbq?useUnicode=true&serverTimezone=GMT%2B8&characterEncoding=utf8");
    ds.setUsername("hbq");
    ds.setPassword("hbq");

    jt = new JdbcTemplate(ds);
    mapDict = new MapDictImpl();
    mapDict.setJt(jt);
    listDict = new ListDictImpl();
    listDict.setMapDict(mapDict);
    mapDict.reloadImmediately();
  }

  @Test
  public void testDictMap() {
    Map data = new HashMap<>();
    data.put("foo", 1);
    data.put("bar", 15);
    mapDict.tranAnyForKey(data, false);
    Assert.assertEquals("一", MapUtils.getString(data, "foo"));
    Assert.assertEquals("a", MapUtils.getString(data, "bar"));
  }

  @Test
  public void testDictList() {
    List<Map> list = new ArrayList<>();
    Map data = new HashMap<>();
    data.put("foo", 2);
    data.put("bar", 16);
    list.add(data);

    data = new HashMap<>();
    data.put("foo", 4);
    data.put("bar", 101);
    list.add(data);

    listDict.tranAnyForKey(list, false);

    Assert.assertEquals("二", MapUtils.getString(list.get(0), "foo"));
    Assert.assertEquals("b", MapUtils.getString(list.get(0), "bar"));
    Assert.assertEquals("四", MapUtils.getString(list.get(1), "foo"));
    Assert.assertEquals("c", MapUtils.getString(list.get(1), "bar"));
  }
}
```



### 启用配置

```properties
hbq.common.dict.enable=false
hbq.common.dict.reload.cron=0 0 * * * *
```



### 字段数据存储表

```sql
create table `hbq_dic_info`
(
    `field_name` varchar(200) primary key,
    `field_desc` varchar(500),
    `enum_type` varchar(100) default 'enum'
);

create table `hbq_dic_ext_kv`
(
    `field_name` varchar(200),
    `enum_key`   varchar(500),
    `enum_value` varchar(500)
);

create table `hbq_dic_ext_sql`
(
    `field_name` varchar(200),
    `enum_sql`   varchar(500)
);
```



### 字典变更事件

默认接收到消息事件后会做字典数据重载动作，应用无需关注字典数据的变化，除非有特殊需要对字典重载做额外逻辑处理请关注此事件对象 `com.github.hbq.event.handle.event.DictEvent` ,见如下代码示例:

```java
@Component
public class EventObserverImpl implements com.github.hbq.event.handle.EventObserver {
    
  @Override
  public void dicEvent(DicEvent event) {
      ...
  }
}
```





## 规则引擎

这是一套强大自定义的规则引擎解析和匹配引擎，使用场景非常多。例如下面的规则内容

```
(p1=1|p1=2)&(p2=\"abc\"|p2%=\"xx\")
```



支持的操作符

| 操作符 |   说明   |
| :----: | :------: |
|   ()   |  表示组  |
|   =    |   等于   |
|   !=   |  不等于  |
|   %=   |   包含   |
|  !%=   |  不包含  |
|   >    |   大于   |
|   >=   | 大于等于 |
|   <    |   小于   |
|   <=   | 小于等于 |



### 使用说明

```java
package com.github.hbq.common.rule;

import com.github.hbq.common.rule.map.ChainRule;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author hbq
 */
public class RuleTest {

  @Test
  public void testMapRule() {
    String rc = "(p1=1|p1=2)&(p2=\"abc\"|p2%=\"xx\")";
    ChainRule rule = new ChainRule(rc);

    Map map = new HashMap();
    map.put("p1", 1);
    map.put("p2", "xxabc");
    Assert.assertTrue(rule.accept(map));

    map.clear();
    map.put("p1", 3);
    map.put("p2", "abc");
    Assert.assertFalse(rule.accept(map));

    map.clear();
    map.put("p1", 2);
    map.put("p2", "zzz");
    Assert.assertFalse(rule.accept(map));
  }
}
```



## 工具类

### 时间快速格式化

```java
// 时间数值转指定格式的字符串
FormatTime.YYYYMMDDHHMISS.withMills(System.currentTimeMillis());
FormatTime.YYYYMMDDHHMISS.withTime(System.currentTimeMillis(), TimeUnit.MILLISECONDS);

// 将时间字符串转成数值
FormatTime.YYYYMMDDHHMISS.toSecs("2009-12-09 23:34:09");
FormatTime.YYYYMMDDHHMISS.toMills("2009-12-09 23:34:09");
```



### 分段工具

#### 时间分段

```java
TimeFragment.DAYS
    .fragment("2009-12-09 03:34:56", "2009-12-15 12:21:09", FormatTime.YYYYMMDDHHMISS);

TimeFragment.DAYS.fragment(133456457234, 133456657234, FormatTime.YYYYMMDDHHMISS);
```



#### 集合分段

```
List<?> list = ....;
List<SubList<?>> DigitSpliter.defaultStep(500).split(list);
```



### 表名获取工具

#### 推荐使用

```java
List<TableInfo> ts = FormatTable.YYYY_MM.tables("2021-07-02 09:30:34", "2021-08-13 21:19:00",FormatTime.YYYYMM,"tab_foo_");

List<TableInfo> ts = FormatTable.YYYY_MM.tables(133456457234L, 133456657234L,TimeUnit.SECONDS,"tab_foo_");
```



#### 不推荐(更灵活)

```java
TableUtils.createTableNames("2021-07-02 09:30:34", "2021-08-13 21:19:00",
            FormatTime.YYYYMMDDHHMISS, TimeFragment.MONTHS, "tab_foo_","yyyy_MM");

TableUtils.createTableNames(133456457234L, 133456657234L,
            TimeUnit.SECONDS, TimeFragment.MONTHS, "tab_foo_","yyyy_MM");
```



### 字符串工具

#### 替换文本中的占位变量

```java
String str = "${foo} world!";
Map map = new HashMap();
map.put("foo","hello");
String result = StrUtils.replacePlaceHolders(str,map);
// result="hello world!";
```





## 应用配置加密

### 如何配置

使用方式为：把需要加密的配置通过 `ENC(这里是加密后的配置内容)` 这种方式进行设置

```properties
spring.datasource.dbcp2.password=ENC(........)
spring.datasource.dbcp2.url=jdbc:mysql://localhost:3306/xxxx?useUnicode=true&characterEncoding=utf8&tinyInt1isBit=false&allowMultiQueries=true
spring.datasource.dbcp2.username=ENC(........)
spring.datasource.dbcp2.validation-query=select 1
```





## RESTful接口加、解密

​	RSA的原理是使用一对密钥对，将公钥分发给数据发送端，私钥数据接收端保留。数据发送端使用公钥对数据进行加密处理，数据接收接收到加密数据后使用私钥进行解密还原原始数据。

​	在http请求中，需要使用两对密钥对，请求处理（js->后端）js使用公钥加密，服务端使用私钥解密。请求响应，服务端使用另外一对的公钥加密数据，js使用私钥解密数据。



### 前端加、解密

#### rsa非对称加密

```html
npm install jsencrypt

<script type="text/javascript" src="jsencrypt.min.js"></script>
```



使用公钥加密

```js
var publicKey="公钥";
var encrypt = new JSEncrypt();
encrypt.setPublicKey("-----BEGIN PUBLIC KEY-----"+publicKey+"-----END PUBLIC KEY-----");
var encryptData = encrypt.encrypt("要加密的字符串");
console.log(encryptData);
```



使用私钥解密

```js
var privateKey="私钥";
var decrypt = new JSEncrypt();
decrypt.setPrivateKey("-----BEGIN RSA PRIVATE KEY-----"+privateKey+"-----END RSA PRIVATE KEY-----");
var decryptData = decrypt.decrypt("要解密的字符串");
console.log(decryptData);
```



#### aes对称加密

```bash
npm install crypto-js
```

```javascript
cat demo.js

//引用AES源码js
var CryptoJS = require('crypto-js');
    
//十六位十六进制数作为密钥,和后端保持一致
var key = CryptoJS.enc.Utf8.parse("AD42F6697B035B7580E4FEF93BE20BAD");
//十六位十六进制数作为密钥偏移量，取上面key前16位
var iv = CryptoJS.enc.Utf8.parse("AD42F6697B035B75");

//解密方法
function Decrypt(word) {

	var base64 = CryptoJS.enc.Base64.parse(word);
	
	var src = CryptoJS.enc.Base64.stringify(base64);

	// 解密模式为CBC，补码方式为PKCS5Padding（也就是PKCS7）
	var decrypt = CryptoJS.AES.decrypt(src, key, {
			iv: iv,
			mode: CryptoJS.mode.CBC,
			padding: CryptoJS.pad.Pkcs7
	});

	var decryptedStr = decrypt.toString(CryptoJS.enc.Utf8);
	return decryptedStr.toString();
}

//加密方法
function Encrypt(word) {
	
	var srcs = CryptoJS.enc.Utf8.parse(word);
	// 加密模式为CBC，补码方式为PKCS5Padding（也就是PKCS7）
	var encrypted = CryptoJS.AES.encrypt(srcs, key, {
			iv: iv,
			mode: CryptoJS.mode.CBC,
			padding: CryptoJS.pad.Pkcs7
	});
	

	//返回base64
	return CryptoJS.enc.Base64.stringify(encrypted.ciphertext);
}

var encryptData = Encrypt('foo');
console.log('加密: '+encryptData);
var decryptData = Decrypt(encryptData);
console.log('解密: '+decryptData);
```



### 开启配置

```properties
# 对称加密
hbq.common.restful.encrypt.aes.enable=false
hbq.common.restful.encrypt.aes.charset=utf-8
hbq.common.restful.encrypt.aes.showLog=true
hbq.common.restful.encrypt.aes.key=392D2FFC865C43D5B5B4988307D4C808

# 非对称加密
hbq.common.restful.encrypt.rsa.enable=false
hbq.common.restful.encrypt.rsa.privateKey=MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAIvzcdJK4ZtSTq3oxQQqMa7E8ayvLiEIMnN4G0jOzxmKDOG6H5GNsb7Ayuht+8nH32H+/rL0iN/39xrDZjKb9jAFWt2f8NDn+IIfmmIfw6rKMlQ3TrYEw51iIMBsIEsimiuA0n6JloWlNrP9173dUmKWJpw+Qyao0sJ9+uhHmdetAgMBAAECgYABNoLuB6gc6IG2ECZk+tijYyGVFY+YybFmPsw9vU0eM4wZCLvtTwUTWihoC0v7W7gb6VHbyzLEfimuGbNxDNj9XkSNUx9jsSDoDhioHgNh5Rt4Eavv9xgDu19muu22BqvimldLkRYDkfo3wARBeeCc+GTROEaSqFZ1m1GM4rfPIQJBAMrjey9YgLPUeXI2KoHyOT3OSOF55bdNqgIOPJ75kkJYYpJh3yKf4C3Pez+bc5ppLHTv5vqzNC8WgbLsssYbJUcCQQCwljdRoA8RnheMnrZpww/5v3Uv+CHdsfkPVhL6BddZ/LQY2iEIgSrg9qsdyDyuI2vCqVxu7F8islzWQJiKMiVrAkBypj20MYT5GfZO7gDzUZe7L1jL4YBIJyEM7FKhW1KlHyEEqz3WaRXbjUWOWh/nf8tZgT+7J40zlrpQC6WygSB9AkEApwCpuIyDM8orw1WmdkeAKFYlYgRtItlaDmL892/mxrfzKz2/1+cwwikGk9f3a0kcdLDvNghdwNambgoROUFLmQJBAMXjRlvu63qYj3UTC4l24LtZ2qkFexcqEm5ZTMUf/IjP2hG1s+zDD0H7gfp0+vClqLldqODi76q6Y5x2cWns/z8=
hbq.common.restful.encrypt.rsa.publicKey=MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCL83HSSuGbUk6t6MUEKjGuxPGsry4hCDJzeBtIzs8Zigzhuh+RjbG+wMrobfvJx99h/v6y9Ijf9/caw2Yym/YwBVrdn/DQ5/iCH5piH8OqyjJUN062BMOdYiDAbCBLIporgNJ+iZaFpTaz/de93VJiliacPkMmqNLCffroR5nXrQIDAQAB
hbq.common.restful.encrypt.rsa.charset=utf-8
hbq.common.restful.encrypt.rsa.showLog=true
hbq.common.restful.encrypt.rsa.timestampCheck=false

# 需要加密的接口扫描包路径
hbq.common.restful.encrypt.basePackages=com.github
```



### 使用说明

+ 接口响应加密处理 @Encrypt

```java
@Encrypt(algorithm = Algorithm.AES)
@RequestMapping(path = "/hello", method = RequestMethod.GET)
@ResponseBody
public Result<Map> encrypt() {
    
    return Result.suc(....);
}
```



+ 接口请求解密处理 @Decrypt，注意只能针对POST请求体进行加密和解密

```java
@Decrypt(algorithm = Algorithm.AES)
@Version("v1.0")
@RequestMapping(path = "/demo/decrypt/{v}", method = RequestMethod.POST)
@ResponseBody
public Result<?> decrypt(@ApiParam(required = true, defaultValue = "v1.0") 
    @PathVariable String v,@RequestBody Map map) {
    
    return Result.ok(map);
}
```





## 配置中心加、解密

基于zk作为配置中心的使用方法，参考如下配置文件 `bootstrap.properties`

```properties
# 应用目录
spring.application.name=工程名
# 是否启用从配置中心读取配置
spring.cloud.zookeeper.enabled=${spring_cloud_zookeeper_enabled}
# 配置中心地址
spring.cloud.zookeeper.connect-string=${spring_cloud_zookeeper_connectString}
# 应用配置根目录
spring.cloud.zookeeper.config.root=/com/github/hbq
spring.cloud.zookeeper.config.watcher.enabled=false
# 应用默认配置目录
spring.cloud.zookeeper.config.defaultContext=common
# 各环境profiles分割符
spring.cloud.zookeeper.config.profileSeparator=,
# zk认证acl方式
spring.cloud.zookeeper.auth.schema=digest
# zk加密信息
spring.cloud.zookeeper.auth.info=${spring_cloud_zookeeper_auth_info}
```



## RESTful接口缓存



​	是一套基于本地缓存轻量级接口缓存实现方案，基于spring-cache的基础上扩展了缓存失效特性。



### 使用说明

注意事项：

1. 接口缓存使用`@Cacheable`和`@Expiry`，其中`@Cacheable`为缓存定义(固定) ，`@Expiry`为缓存过期策略
2. 清除缓存使用`@CacheEvict`和`@Expiry`
3. 注意`@Expiry`中需要定义一个`methodKey`，这个属性唯一标识一个方法，必须和清除接口配置成一样



+ 接口配置缓存策略

```java
@Version("v1.0")
@Cacheable(value = "default", keyGenerator = "apiKeyGenerator")
@Expiry(time = 10, unit = TimeUnit.SECONDS, methodKey = "testCache")
@RequestMapping(path = "/cache/greeting/{v}", method = RequestMethod.GET)
@ResponseBody
public Result<Map> testCache(@ApiParam(required = true, defaultValue = "v1.0") 
                             @PathVariable String v,
    @RequestParam(name = "name", defaultValue = "world") String name) {
    
    log.info("测试缓存, name: {}", name);
    String time = FormatTime.YYYYMMDDHHMISS.withMills(System.currentTimeMillis());
    return Result.ok(ImmutableMap.of("time", time));
    
}
```



+ 清除接口缓存

```java
@Version("v1.0")
@CacheEvict(value = "default", keyGenerator = "apiKeyGenerator")
@Expiry(methodKey = "testCache")
@RequestMapping(path = "/evict/greeting/{v}", method = RequestMethod.GET)
@ResponseBody
public Result<?> testEvict(@ApiParam(required = true, defaultValue = "v1.0") 
                           @PathVariable String v,
    @RequestParam(name = "name", defaultValue = "world") String name) {
    
    log.info("测试清除缓存, name: {}", name);
    return Result.ok("清理成功");
    
}
```





## 应用端点

​	大部分容器的应用都存在镜像操作系统维护命令缺失的问题，所以开发出一套基于底层操作系统/proc/<PID>/*运行时状态文件解析网络连接信息的端点API。



### 网络连接查询端点

开启端点

```properties
management.endpoints.web.exposure.include=net
```



端点接口

|         链接         |               作用                |
| :------------------: | :-------------------------------: |
|  /actuator/net/all   |     查询应用网络连接总体概况      |
| /actuator/net/count  |     查询应用网络连接分类汇总      |
|  /actuator/net/top   | 查询应用网络连接按目的地址汇总top |
| /actuator/net/detail |       查询应用网络连接明细        |





### 应用线程CPU分析端点

开启端点

```properties
management.endpoints.web.exposure.include=threadtop
```



端点接口

|          链接          |           作用            |
| :--------------------: | :-----------------------: |
| /actuator/threadtop/10 | 查询应用线程CPU的占用情况 |





## 简单分布式id辅助接口

​	基于Twitter的雪花算法SnowFlake实现重新优化改造实现，源码参考 https://github.com/beyondfengyu/SnowFlake 进行扩展实现。

### 如何使用

```java
@Autoware
private Snowflake snowflake;
```





## 动态数据源

### 开启配置

```properties
# 开启动态数据源特性
hbq.common.dynamic.datasource.enable=false
spring.datasource.type=org.apache.commons.dbcp2.BasicDataSource

# 配置默认数据源
spring.datasource.dbcp2.driver-class-name=oracle.jdbc.OracleDriver
spring.datasource.dbcp2.url=jdbc:oracle:thin:@vip1:1521/xx
spring.datasource.dbcp2.username=xxx
spring.datasource.dbcp2.password=xxx
spring.datasource.dbcp2.max-total=10
spring.datasource.dbcp2.max-wait-millis=300000
spring.datasource.dbcp2.max-idle=1
spring.datasource.dbcp2.validation-query=select 1 from dual

# 配置数据源db1
spring.datasource.db1.driver-class-name=oracle.jdbc.OracleDriver
spring.datasource.db1.url=jdbc:oracle:thin:@vip2:1521/xx
spring.datasource.db1.username=ENC(......)
spring.datasource.db1.password=ENC(......)
spring.datasource.db1.max-total=10
spring.datasource.db1.max-wait-millis=300000
spring.datasource.db1.max-idle=1
spring.datasource.db1.validation-query=select 1 from dual

# 配置数据源db2
spring.datasource.db2.driver-class-name=oracle.jdbc.OracleDriver
spring.datasource.db2.url=jdbc:oracle:thin:@vip3:1521/xx
spring.datasource.db2.username=xxx
spring.datasource.db2.password=xxx
spring.datasource.db2.max-total=10
spring.datasource.db2.max-wait-millis=300000
spring.datasource.db2.max-idle=1
spring.datasource.db2.validation-query=select 1 from dual
```



### 如何使用

参考如下代码

场景一：作用在接口上，不用每个方法指定

```java
@Repository
@Mapper
@DS("db1")
public interface DemoDao {

  List<Map> queryData1(Map param);

  List<Map> queryData2();

  List<Map> queryData3();
}
```



场景二：作用在方法上

```java
@Repository
@Mapper
public interface DemoDao {

  List<Map> queryData1(Map param);

  @DS("db1")
  List<Map> queryData2();
    
  @DS("db2")
  List<Map> queryData3();
}
```



场景三：如果不指定@DS注解，则使用默认的dbcp2数据源，并且dbcp2数据源是必须要配置的

```java
@Repository
@Mapper
public interface DemoDao {

  List<Map> queryData1(Map param);

  List<Map> queryData2();
    
  List<Map> queryData3();
}
```

