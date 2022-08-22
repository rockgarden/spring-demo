# 使用 Redis 响应式访问数据

<https://github.com/spring-guides/gs-spring-data-reactive-redis.git>

本指南将引导您完成创建功能反应式应用程序的过程，该应用程序使用 Spring Data 使用非阻塞 Lettuce 驱动程序与 Redis 交互。

您将构建什么
您将构建一个 Spring 应用程序，该应用程序使用 Spring Data Redis 和 Project Reactor 与 Redis 数据存储进行响应式交互，存储和检索 Coffee 对象而不会阻塞。 此应用程序使用基于 Reactive Streams 规范的 Reactor 的 Publisher 实现，即 Mono（对于返回 0 或 1 值的 Publisher）和 Flux（对于返回 0 到 n 值的 Publisher）。

环境准备：

- JDK 1.8 或更高版本
- Gradle 4+ 或 Maven 3.2+
- 安装 redis
  - docker pull redis
  - 配置 localhost:port 为 6379

## 编码

创建域类

- 创建一个代表我们希望在咖啡目录中储存的咖啡类型的类。
- src/main/java/hello/Coffee.java，在这个示例中，使用 Lombok 来消除构造函数和所谓的“数据类”方法（访问器/突变器、equals()、toString() 和 hashCode()）的样板代码。

使用支持反应式 Redis 操作的 Spring Beans 创建配置类

- src/main/java/hello/CoffeeConfiguration.java

创建一个 Spring Bean 以在我们启动应用程序时将一些示例数据加载到我们的应用程序中

- 由于我们可能多次（重新）启动我们的应用程序，我们应该首先删除之前执行中可能仍然存在的任何数据。 我们使用 flushAll() (Redis) 服务器命令来完成此操作。 一旦我们刷新了任何现有数据，我们创建一个小的 Flux，将每个咖啡名称映射到一个 Coffee 对象，并将其保存到响应式 Redis 存储库。 然后我们在 repo 中查询所有值并显示它们。
- src/main/java/hello/CoffeeLoader.java

创建一个 RestController 为我们的应用程序提供一个外部接口

- src/main/java/hello/CoffeeController.java

使应用程序可执行

- 尽管可以将此服务打包为传统的 WAR 文件以部署到外部应用程序服务器，但下面演示的更简单的方法会创建一个独立的应用程序。 您将所有内容打包在一个可执行的 JAR 文件中，由一个很好的旧 Java main() 方法驱动。 在此过程中，您使用 Spring 对嵌入 Netty 异步“容器”作为 HTTP 运行时的支持，而不是部署到外部实例。
- src/main/java/hello/Application.java
  - @SpringBootApplication 是一个方便的注解，它添加了以下所有内容：
    - @Configuration：将类标记为应用程序上下文的 bean 定义源。
    - @EnableAutoConfiguration：告诉 Spring Boot 根据类路径设置、其他 bean 和各种属性设置开始添加 bean。 例如，如果 spring-webmvc 在类路径上，则此注释将应用程序标记为 Web 应用程序并激活关键行为，例如设置 DispatcherServlet。
    - @ComponentScan：告诉 Spring 在 hello 包中查找其他组件、配置和服务，让它找到控制器。
  - main() 方法使用 Spring Boot 的 SpringApplication.run() 方法来启动应用程序。 您是否注意到没有一行 XML？ 也没有 web.xml 文件。 这个 Web 应用程序是 100% 纯 Java，您不必处理任何管道或基础设施的配置。

## 部署

构建一个可执行的 JAR

- 您可以使用 Gradle 或 Maven 从命令行运行应用程序。您还可以构建一个包含所有必要依赖项、类和资源的单个可执行 JAR 文件并运行它。构建可执行 jar 可以在整个开发生命周期、跨不同环境等中轻松地作为应用程序交付、版本化和部署服务。
- 如果您使用 Gradle，则可以使用 ./gradlew bootRun 运行应用程序。或者，您可以使用 ./gradlew build 构建 JAR 文件，然后运行 ​​JAR 文件，如下所示：
  - `java -jar build/libs/gs-spring-data-reactive-redis-0.1.0.jar`
- 如果您使用 Maven，则可以使用 ./mvnw spring-boot:run 运行应用程序。或者，您可以使用 ./mvnw clean package 构建 JAR 文件，然后运行 ​​JAR 文件，如下所示：
  - `java -jar target/gs-spring-data-reactive-redis-0.1.0.jar`
- 此处描述的步骤创建了一个可运行的 JAR。您还可以[构建经典的 WAR 文件](https://spring.io/guides/gs/convert-jar-to-war/)。

## 测试应用程序

现在应用程序正在运行，您可以通过从 HTTPie、curl 或您喜欢的浏览器访问 <http://localhost:8080/coffees> 来测试它。
