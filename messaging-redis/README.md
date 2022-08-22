# 使用 Redis 进行消息传递

<https://github.com/spring-guides/gs-messaging-redis.git>

本指南将引导您完成使用 Spring Data Redis 发布和订阅通过 Redis 发送的消息的过程。

你将建造什么
您将构建一个应用程序，该应用程序使用 StringRedisTemplate 发布字符串消息，并使用 MessageListenerAdapter 让 POJO 订阅该消息。

使用 Spring Data Redis 作为发布消息的方式可能听起来很奇怪，但是，正如您将发现的那样，Redis 不仅提供了 NoSQL 数据存储，还提供了消息传递系统。

准备

- JDK 1.8 或更高版本
- Gradle 4+ 或 Maven 3.2+
- 建立一个 Redis 服务器
  - 在构建消息传递应用程序之前，您需要设置将处理接收和发送消息的服务器。
  - Redis 附带一个消息传递系统。 该服务器可在 <https://redis.io/download> 免费获得。
    - localhost port 6397

## 编码

### 创建 Redis 消息接收器

- 在任何基于消息传递的应用程序中，都有消息发布者和消息接收者。 要创建消息接收器，请使用响应消息的方法实现接收器，来自 src/main/java/com/example/messagingredis/Receiver.java。
- Receiver 是一个 POJO，它定义了接收消息的方法。 当您将 Receiver 注册为消息侦听器时，您可以随意命名消息处理方法。
- 出于演示目的，接收方正在对收到的消息进行计数。 这样，它可以在收到消息时发出信号。

### 注册监听器并发送消息

Spring Data Redis 提供了使用 Redis 发送和接收消息所需的所有组件。 具体来说，需要配置：

- 连接工厂
- 消息侦听器容器
- REDIS模板

您将使用REDIS模板发送消息，然后将接收器注册为消息侦听容器，以便它接收消息。 连接工厂同时驱动模板和消息侦听器容器，让它们连接到 Redis 服务器。

listenerAdapter 方法中定义的 bean 在 container 中定义的消息侦听器容器中注册为消息侦听器，并将侦听聊天主题的消息。因为Receiver类是POJO，所以需要封装在一个消息监听适配器中，实现MessageListener接口（addMessageListener()需要这个接口）。消息侦听器适配器还配置为在消息到达时调用 Receiver 上的 receiveMessage() 方法。

连接工厂和消息侦听器容器 bean 是您侦听消息所需的全部。要发送消息，您还需要一个 Redis 模板。在这里，它是一个配置为 StringRedisTemplate 的 bean，它是 RedisTemplate 的一个实现，专注于 Redis 的常见用途，其中键和值都是 String 实例。

main() 方法通过创建 Spring 应用程序上下文来启动一切。然后应用程序上下文启动消息侦听器容器，消息侦听器容器 bean 开始侦听消息。然后 main() 方法从应用程序上下文中检索 StringRedisTemplate bean，并使用它从 Redis 发送 Hello！关于聊天主题的消息。最后，它关闭 Spring 应用程序上下文，应用程序结束。

### 创建控制器

RedisController.java 实现一个 `/publish` 接口，用来发送消息。

### 创建服务

MessageSubscriber.java 实现消息订阅服务，打接收到的消息打印处理。

## 构建一个可执行的 JAR

如果使用Gradle，则可以使用./gradlew bootrun运行该应用程序。 或者，您可以使用 ./gradlew build 构建 JAR 文件，然后运行 JAR 文件，如下所示：
`java -jar build/libs/gs-messaging-redis-0.1.0.jar`

如果您使用 Maven，则可以使用 ./mvnw spring-boot:run 运行应用程序。 或者，您可以使用 ./mvnw clean package 构建 JAR 文件，然后运行 JAR 文件，如下所示：
`Java -Jar target/GS-Messaging-REDIS-0.1.0.JAR`

```log

2022-08-22 15:28:19.959  INFO 95424 --- [           main] c.e.m.MessagingRedisApplication          : Starting MessagingRedisApplication on xxx.local with PID 95424 (/Users/wangkan/git/spring_demo/messaging-redis/bin/main started by wangkan in /Users/wangkan/git/spring_demo/messaging-redis)
2022-08-22 15:28:19.965  INFO 95424 --- [           main] c.e.m.MessagingRedisApplication          : No active profile set, falling back to default profiles: default
2022-08-22 15:28:22.919  INFO 95424 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Multiple Spring Data modules found, entering strict repository configuration mode!
2022-08-22 15:28:22.940  INFO 95424 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data Redis repositories in DEFAULT mode.
2022-08-22 15:28:23.026  INFO 95424 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 21ms. Found 0 Redis repository interfaces.
2022-08-22 15:28:30.565  INFO 95424 --- [    container-1] io.lettuce.core.EpollProvider            : Starting without optional epoll library
2022-08-22 15:28:30.595  INFO 95424 --- [    container-1] io.lettuce.core.KqueueProvider           : Starting without optional kqueue library
2022-08-22 15:28:32.645  INFO 95424 --- [           main] c.e.m.MessagingRedisApplication          : Started MessagingRedisApplication in 14.757 seconds (JVM running for 20.887)
2022-08-22 15:28:32.648  INFO 95424 --- [           main] c.e.m.MessagingRedisApplication          : Sending message...
2022-08-22 15:28:32.782  INFO 95424 --- [    container-2] com.example.messagingredis.Receiver      : Received < Hello from Redis! >
```

## 测试

测试调用接口:
`curl 'http://localhost:8080/publish?message=hello'`

观察控制台，可以看到打印了收到的message参数:
`2022-08-22 16:22:46.128  INFO 8162 --- [ioEventLoop-4-2] m.Chapter55Application$MessageSubscriber : Receive message : hello`

## 问题

**WARN**: `com.example.messagingredis.MessageSubscriber.<init> Anonymous inner classes containing only one method should become lambdas`

```java
redisConnection.subscribe(new MessageListener() {
  @Override
  public void onMessage(Message message, byte[] bytes) {
    // 收到消息的处理逻辑
    log.info("Receive message : {}", message);
  }
}, cTest.getBytes(StandardCharsets.UTF_8));

// To lambdas
redisConnection.subscribe((message, bytes) -> log.info("Receive message : {}", message),
                    cTest.getBytes(StandardCharsets.UTF_8));
```

**Error**: `java.lang.NullPointerException: null at com.example.messagingredis.MessageSubscriber.<init>(MessageSubscriber.java:32) ~[main/:na]`

原因：程序会先执行构造函数，然后再为@Value修饰的成员变量注入配置值。因此，执行构造函数的时候，配置项的值一直为null。
解决：构造函数传参时注入配置值。

```java
    @Value("${CHANNEL.cTest}")
    private String cTest;

// 构造函数传参
public MessageSubscriber(RedisTemplate<String, String> redisTemplate, @Value("${CHANNEL.cTest}") String channel) 
```

@Value获取不到配置值的其他情况有：

- 变量使用static或final修饰。
- 类没有加上@Component(或者@service等)修饰。
- 类通过new创建实例，而没有通过@Autowired注入。

> 某种情况下，通过成员变量注入配置值，不如全局变量灵活。

**Error**: `Error starting Tomcat context. Exception: org.springframework.beans.factory.BeanCreationException. Message: Error creating bean with name 'servletEndpointRegistrar' defined in class path resource [org/springframework/boot/actuate/autoconfigure/endpoint/web/ServletEndpointManagementContextConfiguration$WebMvcServletEndpointManagementContextConfiguration.class]: Bean instantiation via factory method failed; nested exception is org.springframework.beans.BeanInstantiationException: Failed to instantiate [org.springframework.boot.actuate.endpoint.web.ServletEndpointRegistrar]: Factory method 'servletEndpointRegistrar' threw exception; nested exception is org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'healthEndpoint' defined in class path resource [org/springframework/boot/actuate/autoconfigure/health/HealthEndpointConfiguration.class]: Unsatisfied dependency expressed through method 'healthEndpoint' parameter 0; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'healthContributorRegistry' defined in class path resource [org/springframework/boot/actuate/autoconfigure/health/HealthEndpointConfiguration.class]: Bean instantiation via factory method failed; nested exception is org.springframework.beans.BeanInstantiationException: Failed to instantiate [org.springframework.boot.actuate.health.HealthContributorRegistry]: Factory method 'healthContributorRegistry' threw exception; nested exception is org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'org.springframework.boot.actuate.autoconfigure.redis.RedisReactiveHealthContributorAutoConfiguration': Unsatisfied dependency expressed through constructor parameter 0; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'redisConnectionFactory' defined in class path resource [org/springframework/boot/autoconfigure/data/redis/LettuceConnectionConfiguration.class]: Bean instantiation via factory method failed; nested exception is org.springframework.beans.BeanInstantiationException: Failed to instantiate [org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory]: Factory method 'redisConnectionFactory' threw exception; nested exception is java.lang.NoClassDefFoundError: org/apache/commons/pool2/impl/GenericObjectPoolConfig`

原因：使用 redis lettuce 客户端，需要添加 org.apache.commons:commons-pool2。
解决：使用 redis jedis 客户端，或引入 commons-pool2 架包。
