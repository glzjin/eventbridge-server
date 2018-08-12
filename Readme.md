### 说明

事件中继--服务器。

### 环境

JRE 1.8+

### 部署方式

1. 在源码包里的 src/main/resources/application.properties 写好配置(也可以不写，第3步运行时指定参数)
2. maven 打成 jar 包
3. 直接 java -jar 运行吧。

```

java -jar eventbridge-1.0.0-SNAPSHOT.jar --server.port=8083 --system.producer_key=123 --system.consumer_key=1234

```