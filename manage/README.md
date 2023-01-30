## 安装使用

### 打包

包含`ui`代码和`微服务`代码的打包

```shell
cd manage
mvn -DskipTests=true clean package
```



### 部署

同门户网关。



## 维护页面

### 路由管理

![image-20230126152739505](README/image/README/image-20230126152739505.png)

- 路由查询

![image-20230113153709574](README/image/README/image-20230113153709574.png)



- 路由新增、编辑

![image-20230113153748378](README/image/README/image-20230113153748378.png)



### 字典管理

![image-20230126152803618](README/image/README/image-20230126152803618.png)

- 字典查询

![image-20230113154040475](README/image/README/image-20230113154040475.png)



- 字典新增、编辑

![image-20230113153848146](README/image/README/image-20230113153848146.png)



![image-20230113154055870](README/image/README/image-20230113154055870.png)



### 应用kafka入口消息速率管理

可针对应用或实例两个纬度进行限速配置。

![image-20230126152824597](README/image/README/image-20230126152824597.png)

- 应用限速管理

![image-20230126152916081](README/image/README/image-20230126152916081.png)



![image-20230126152930908](README/image/README/image-20230126152930908.png)



- 实例限速管理

![image-20230126152959376](README/image/README/image-20230126152959376.png)



![image-20230126153102716](README/image/README/image-20230126153102716.png)



### 指标管理

#### 指标列表

![image-20230130145533236](README/image/README/image-20230130145533236.png)

#### 指标阈值配置

![image-20230130152138390](README/image/README/image-20230130152138390.png)

#### 应用指标趋势

![image-20230130145703566](README/image/README/image-20230130145703566.png)



#### 实例指标趋势

![image-20230130145818302](README/image/README/image-20230130145818302.png)

