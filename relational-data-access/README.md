# 使用 JDBC 和 Spring 访问关系数据

<https://spring.io/guides/gs/relational-data-access/>

本指南将引导您完成使用 Spring 访问关系数据的过程。

将构建一个使用 Spring 的 JdbcTemplate 来访问存储在关系数据库中的数据的应用程序。

你需要什么

- JDK 1.8 或更高版本
- Gradle 4+ 或 Maven 3.2+
- 引入 org.springframework.boot.spring-boot-starter-jdbc
- 加载嵌入式数据库

嵌入式数据库，仅用于开发和测试环境，不推荐用于生产环境。Spring Boot提供自动配置的嵌入式数据库有H2、HSQL、Derby，你不需要提供任何连接配置就能使用。

比如，我们可以在pom.xml中引入如下配置使用HSQL

```xml
<dependency>
    <groupId>org.hsqldb</groupId>
    <artifactId>hsqldb</artifactId>
    <scope>runtime</scope>
</dependency>
```

连接生产数据源，参见 accessing-data-mysql 项目。

连接JNDI数据源，当你将应用部署于应用服务器上的时候想让数据源由应用服务器管理，那么可以使用如下配置方式引入JNDI数据源。

`spring.datasource.jndi-name=java:jboss/datasources/customers`

## 概念

JDBC

Java数据库连接（Java Database Connectivity）是Java语言中用来规范客户端程序如何来访问数据库的应用程序接口，提供了诸如查询和更新数据库中数据的方法。通常说的JDBC是面向关系型数据库的。

JDBC API主要位于JDK中的java.sql包中（之后扩展的内容位于javax.sql包中），主要包括（斜体代表接口，需驱动程序提供者来具体实现）：

- DriverManager：负责加载各种不同驱动程序（Driver），并根据不同的请求，向调用者返回相应的数据库连接（Connection）。
- Driver：驱动程序，会将自身加载到DriverManager中去，并处理相应的请求并返回相应的数据库连接（Connection）。
- Connection：数据库连接，负责与进行数据库间通讯，SQL执行以及事务处理都是在某个特定Connection环境中进行的。可以产生用以执行SQL的Statement。
- Statement：用以执行SQL查询和更新（针对静态SQL语句和单次执行）。PreparedStatement：用以执行包含动态参数的SQL查询和更新（在服务器端编译，允许重复执行以提高效率）。
- CallableStatement：用以调用数据库中的存储过程。
- SQLException：代表在数据库连接的建立和关闭和SQL语句的执行过程中发生了例外情况（即错误）。

数据源

可以看到，在java.sql中并没有数据源（Data Source）的概念。这是由于在java.sql中包含的是JDBC内核API，另外还有个javax.sql包，其中包含了JDBC标准的扩展API。而关于数据源（Data Source）的定义，就在javax.sql这个扩展包中。

实际上，在JDBC内核API的实现下，就已经可以实现对数据库的访问了，那么我们为什么还需要数据源呢？主要出于以下几个目的：

- 封装关于数据库访问的各种参数，实现统一管理
- 通过对数据库的连接池管理，节省开销并提高效率

在Java这个自由开放的生态中，已经有非常多优秀的开源数据源可以供大家选择，比如：DBCP、C3P0、Druid、HikariCP等。

而在Spring Boot 2.x中，对数据源的默认配置，采用了目前性能最佳的 [HikariCP](https://github.com/brettwooldridge/HikariCP) 。

HikariCP

由于Spring Boot的自动化配置机制，大部分对于数据源的配置都可以通过配置参数的方式去改变。只有一些特殊情况，比如：更换默认数据源，多数据源共存等情况才需要去修改覆盖初始化的Bean内容。

在Spring Boot自动化配置中，对于数据源的配置可以分为两类：

- 通用配置：以spring.datasource.*的形式存在，主要是对一些即使使用不同数据源也都需要配置的一些常规内容。比如：数据库链接地址、用户名、密码等。常用配置：

  ```properties
  spring.datasource.url=jdbc:mysql://localhost:3306/test
  spring.datasource.username=root
  spring.datasource.password=123456
  spring.datasource.driver-class-name=com.mysql.jdbc.Driver
  ```

- 数据源连接池配置：`以spring.datasource.<数据源名称>.*` 的形式存在，比如：Hikari的配置参数就是spring.datasource.hikari.*形式。常用的几个配置项及对应说明：

  ```properties
  <!-- 最小空闲连接，默认值10，小于0或大于maximum-pool-size，都会重置为maximum-pool-size -->
  spring.datasource.hikari.minimum-idle=10
  <!-- 最大连接数，小于等于0会被重置为默认值10；大于零小于1会被重置为minimum-idle的值 -->
  spring.datasource.hikari.maximum-pool-size=20
  <!-- 空闲连接超时时间，默认值600000（10分钟），大于等于 max-lifetime 且 max-lifetime>0 ，会被重置为0-->
  spring.datasource.hikari.idle-timeout=500000
  <!-- 连接最大存活时间，不等于0且小于30秒，会被重置为默认值30分钟，设置应该比mysql设置的超时时间短 -->
  spring.datasource.hikari.max-lifetime=540000
  <!-- 连接超时时间：默认值30秒 -->
  spring.datasource.hikari.connection-timeout=60000
  <!-- 用于测试连接是否可用的查询语句 -->
  spring.datasource.hikari.connection-test-query=SELECT 1
  ```

## 编码

创建客户对象

您将使用的简单数据访问逻辑管理客户的名字和姓氏。 要在应用程序级别表示此数据，请创建一个 Customer 类，来自 src/main/java/com/example/relationaldataaccess/Customer.java 。

存储和检索数据

Spring 提供了一个名为 JdbcTemplate 的模板类， JdbcTemplate是自动配置，可以轻松使用 SQL 关系数据库和 JDBC。 大多数 JDBC 代码都陷入了资源获取、连接管理、异常处理和一般错误检查的泥潭，而这与代码要实现的目标完全无关。 JdbcTemplate 会为您处理所有这些。 您所要做的就是专注于手头的任务。 来自 src/main/java/com/example/relationaldataaccess/RelationalDataAccessApplication.java 显示了一个可以通过 JDBC 存储和检索数据的类。

@SpringBootApplication 是一个方便的注解，它添加了以下所有内容：

- @Configuration：将类标记为应用程序上下文的 bean 定义源。
- @EnableAutoConfiguration：告诉 Spring Boot 根据类路径设置、其他 bean 和各种属性设置开始添加 bean。
- @ComponentScan：告诉 Spring 在 com.example.relationaldataaccess 包中查找其他组件、配置和服务。在这种情况下，没有。

main() 方法使用 Spring Boot 的 SpringApplication.run() 方法来启动应用程序。

Spring Boot 支持 H2（内存中的关系数据库引擎）并自动创建连接。因为我们使用的是 spring-jdbc ，所以Spring Boot会自动创建一个JdbcTemplate。 JdbcTemplate是自动配置的，可以直接使用 @Autowired JdbcTemplate 字段会自动加载它并使其可用。

这个 Application 类实现了 Spring Boot 的 CommandLineRunner，这意味着它将在应用程序上下文加载后执行 run() 方法。

首先，使用 JdbcTemplate 的 execute 方法安装一些 DDL。

其次，获取一个字符串列表，并通过使用 Java 8 流，将它们拆分为 Java 数组中的名字/姓氏对。

然后使用 JdbcTemplate 的 batchUpdate 方法在新创建的表中安装一些记录。方法调用的第一个参数是查询字符串。最后一个参数（Object 实例的数组）保存要替换到 ? 字符所在查询的变量。

对于单个insert语句，JdbcTemplate的insert方法很好。但是，对于多次插入，最好使用 batchUpdate。

利用 ? 用于通过指示 JDBC 绑定变量来避免 [SQL注入攻击](https://en.wikipedia.org/wiki/SQL_injection)的参数。

最后，使用查询方法在表中搜索符合条件的记录。 您再次使用 ? 参数为查询创建参数，在调用时传入实际值。 最后一个参数是一个 Java 8 lambda，用于将每个结果行转换为一个新的 Customer 对象。

Java 8 lambda 很好地映射到单一方法接口，例如 Spring 的 RowMapper。

更多其他数据访问操作的使用请参考：[JdbcTemplate API](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/jdbc/core/JdbcTemplate.html) 。

## 构建一个可执行的 JAR

如果您使用 Gradle，则可以使用 ./gradlew bootRun 运行应用程序。 或者，您可以使用 ./gradlew build 构建 JAR 文件，然后运行 JAR 文件，如下所示：

`java -jar build/libs/gs-relational-data-access-0.1.0.jar`

如果您使用 Maven，则可以使用 ./mvnw spring-boot:run 运行应用程序。 或者，您可以使用 ./mvnw clean package 构建 JAR 文件，然后运行 JAR 文件，如下所示：

`java -jar target/gs-relational-data-access-0.1.0.jar`

```log
2022-09-12 21:32:55.777  INFO 73892 --- [           main] c.e.r.RelationalDataAccessApplication    : Creating tables
2022-09-12 21:32:55.838  INFO 73892 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Starting...
2022-09-12 21:32:56.483  INFO 73892 --- [           main] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Start completed.
2022-09-12 21:32:56.616  INFO 73892 --- [           main] c.e.r.RelationalDataAccessApplication    : Inserting customer record for John Woo
2022-09-12 21:32:56.617  INFO 73892 --- [           main] c.e.r.RelationalDataAccessApplication    : Inserting customer record for Jeff Dean
2022-09-12 21:32:56.627  INFO 73892 --- [           main] c.e.r.RelationalDataAccessApplication    : Inserting customer record for Josh Bloch
2022-09-12 21:32:56.629  INFO 73892 --- [           main] c.e.r.RelationalDataAccessApplication    : Inserting customer record for Josh Long
2022-09-12 21:32:56.765  INFO 73892 --- [           main] c.e.r.RelationalDataAccessApplication    : Querying for customer records where first_name = 'Josh':
2022-09-12 21:32:56.815  INFO 73892 --- [           main] c.e.r.RelationalDataAccessApplication    : Customer[id=3, firstName='Josh', lastName='Bloch']
2022-09-12 21:32:56.824  INFO 73892 --- [           main] c.e.r.RelationalDataAccessApplication    : Customer[id=4, firstName='Josh', lastName='Long']
2022-09-12 21:32:56.827  INFO 73892 --- [extShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown initiated...
2022-09-12 21:32:56.834  INFO 73892 --- [extShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown completed.
```

## 概括

Spring Boot 具有许多用于配置和自定义连接池的功能 — 例如，连接到外部数据库而不是内存中的数据库。 有关详细信息，请参阅[参考指南](https://docs.spring.io/spring-boot/docs/2.2.1.RELEASE/reference/htmlsingle/#boot-features-configure-datasource)。

## 附录

### Hikari的配置参数

| name                      | 描述                                                                            | 构造器默认值                         | 默认配置validate之后的值 | validate重置                                                                                                                                                                                      |
|---------------------------|-------------------------------------------------------------------------------|--------------------------------|------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| autoCommit                | 自动提交从池中返回的连接                                                                  | TRUE                           | TRUE             | –                                                                                                                                                                                               |
| connectionTimeout         | 等待来自池的连接的最大毫秒数                                                                | SECONDS.toMillis(30) = 30000   | 30000            | 如果小于250毫秒，则被重置回30秒                                                                                                                                                                              |
| idleTimeout               | 连接允许在池中闲置的最长时间                                                                | MINUTES.toMillis(10) = 600000  | 600000           | 如果idleTimeout+1秒>maxLifetime 且 maxLifetime>0，则会被重置为0（代表永远不会退出）；如果idleTimeout!=0且小于10秒，则会被重置为10秒                                                                                                 |
| maxLifetime               | 池中连接最长生命周期                                                                    | MINUTES.toMillis(30) = 1800000 | 1800000          | 如果不等于0且小于30秒则会被重置回30分钟                                                                                                                                                                          |
| connectionTestQuery       | 如果您的驱动程序支持JDBC4，我们强烈建议您不要设置此属性                                                | null                           | null             | –                                                                                                                                                                                               |
| minimumIdle               | 池中维护的最小空闲连接数                                                                  | -1                             | 10               | minIdle<0或者minIdle>maxPoolSize,则被重置为maxPoolSize                                                                                                                                                 |
| maximumPoolSize           | 池中最大连接数，包括闲置和使用中的连接                                                           | -1                             | 10               | 如果maxPoolSize小于1，则会被重置。当minIdle<=0被重置为DEFAULT_POOL_SIZE则为10;如果minIdle>0则重置为minIdle的值                                                                                                            |
| metricRegistry            | 该属性允许您指定一个 Codahale / Dropwizard MetricRegistry 的实例，供池使用以记录各种指标               | null                           | null             | –                                                                                                                                                                                               |
| healthCheckRegistry       | 该属性允许您指定池使用的Codahale / Dropwizard HealthCheckRegistry的实例来报告当前健康信息             | null                           | null             | –                                                                                                                                                                                               |
| poolName                  | 连接池的用户定义名称，主要出现在日志记录和JMX管理控制台中以识别池和池配置                                        | null                           | HikariPool-1     | –                                                                                                                                                                                               |
| initializationFailTimeout | 如果池无法成功初始化连接，则此属性控制池是否将 fail fast                                             | 1                              | 1                | –                                                                                                                                                                                               |
| isolateInternalQueries    | 是否在其自己的事务中隔离内部池查询，例如连接活动测试                                                    | FALSE                          | FALSE            | –                                                                                                                                                                                               |
| allowPoolSuspension       | 控制池是否可以通过JMX暂停和恢复                                                             | FALSE                          | FALSE            | –                                                                                                                                                                                               |
| readOnly                  | 从池中获取的连接是否默认处于只读模式                                                            | FALSE                          | FALSE            | –                                                                                                                                                                                               |
| registerMbeans            | 是否注册JMX管理Bean（MBeans）                                                         | FALSE                          | FALSE            | –                                                                                                                                                                                               |
| catalog                   | 为支持 catalog 概念的数据库设置默认 catalog                                                | driver default                 | null             | –                                                                                                                                                                                               |
| connectionInitSql         | 该属性设置一个SQL语句，在将每个新连接创建后，将其添加到池中之前执行该语句。                                       | null                           | null             | –                                                                                                                                                                                               |
| driverClassName           | HikariCP将尝试通过仅基于jdbcUrl的DriverManager解析驱动程序，但对于一些较旧的驱动程序，还必须指定driverClassName | null                           | null             | –                                                                                                                                                                                               |
| transactionIsolation      | 控制从池返回的连接的默认事务隔离级别                                                            | null                           | null             | –                                                                                                                                                                                               |
| validationTimeout         | 连接将被测试活动的最大时间量                                                                | SECONDS.toMillis(5) = 5000     | 5000             | 如果小于250毫秒，则会被重置回5秒                                                                                                                                                                              |
| leakDetectionThreshold    | 记录消息之前连接可能离开池的时间量，表示可能的连接泄漏                                                   | 0                              | 0                | 如果大于0且不是单元测试，则进一步判断：(leakDetectionThreshold < SECONDS.toMillis(2) or (leakDetectionThreshold > maxLifetime && maxLifetime > 0)，会被重置为0 . 即如果要生效则必须>0，而且不能小于2秒，而且当maxLifetime > 0时不能大于maxLifetime |
| dataSource                | 这个属性允许你直接设置数据源的实例被池包装，而不是让HikariCP通过反射来构造它                                    | null                           | null             | –                                                                                                                                                                                               |
| schema                    | 该属性为支持模式概念的数据库设置默认模式                                                          | driver default                 | null             | –                                                                                                                                                                                               |
| threadFactory             | 此属性允许您设置将用于创建池使用的所有线程的java.util.concurrent.ThreadFactory的实例。                  | null                           | null             | –                                                                                                                                                                                               |
| scheduledExecutor         | 此属性允许您设置将用于各种内部计划任务的java.util.concurrent.ScheduledExecutorService实例           | null                           | null             | –                                                                                                                                                                                               |
