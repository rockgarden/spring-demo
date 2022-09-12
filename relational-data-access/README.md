# 使用 JDBC 和 Spring 访问关系数据

<https://spring.io/guides/gs/relational-data-access/>

本指南将引导您完成使用 Spring 访问关系数据的过程。

你将建造什么
您将构建一个使用 Spring 的 JdbcTemplate 来访问存储在关系数据库中的数据的应用程序。

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
