# 使用 Spring 缓存数据

<https://github.com/spring-guides/gs-caching>

本指南将引导您完成在 Spring 托管 bean 上启用缓存的过程。

在Spring Boot中通过@EnableCaching注解自动化配置合适的缓存管理器（CacheManager），Spring Boot根据下面的顺序去侦测缓存提供者：

- Generic
- JCache (JSR-107) (EhCache 3, Hazelcast, Infinispan, and others)
- EhCache 2.x
- Hazelcast
- Infinispan
- Couchbase
- Redis
- Caffeine
- Simple

除了按顺序侦测外，我们也可以通过配置属性spring.cache.type来强制指定。
我们也可以通过debug调试查看cacheManager对象的实例来判断当前使用了什么缓存。

## 搭建

您将构建一个应用程序，在一个简单的图书存储库上启用缓存。

你需要什么：

- JDK 1.8 或更高版本
- Gradle 4+ 或 Maven 3.2+

### 创建图书模型

首先，您需要为您的书创建一个简单的模型。 来自 src/main/java/com/example/caching/Book.java。

### 创建图书存储库

您还需要该模型的存储库。 来自 src/main/java/com/example/caching/BookRepository.java。

您可以使用 Spring Data 在各种 SQL 或 NoSQL 存储上提供存储库的实现。
但是，出于本指南的目的，您将简单地使用一个简单的实现来模拟一些延迟（网络服务、慢延迟或其他问题）。 代码如下：

```java
package com.example.caching;

import org.springframework.stereotype.Component;

@Component
public class SimpleBookRepository implements BookRepository {

  @Override
  public Book getByIsbn(String isbn) {
    simulateSlowService();
    return new Book(isbn, "Some book");
  }

  private void simulateSlowService() {
    try {
      long time = 3000L;
      Thread.sleep(time);
    } catch (InterruptedException e) {
      // 中断线程 
      Thread.currentThread().interrupt();
      throw new IllegalStateException(e);
    }
  }

}
```

> 注意：InterruptedExceptions 永远不应在代码中被忽略，并且在这种情况下将异常计数简单地记录为“忽略”。 InterruptedException 的抛出清除了线程的中断状态，因此如果异常处理不当，线程被中断的信息将会丢失。相反，应立即或在清理方法的状态后重新抛出 InterruptedExceptions，或者应通过调用 Thread.interrupt() 重新中断线程，即使这应该是单线程应用程序。任何其他的行动都有延迟线程关闭的风险并丢失线程被中断的信息——可能没有完成它的任务。

simulateSlowService 故意在每个 getByIsbn 调用中插入三秒延迟。稍后，您将使用缓存加速此示例。

### 使用存储库

接下来，您需要连接存储库并使用它来访问一些书籍。以下清单显示了如何执行此操作：

```java
package com.example.caching;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CachingApplication {

  public static void main(String[] args) {
    SpringApplication.run(CachingApplication.class, args);
  }

}
```

@SpringBootApplication 是一个便捷的注解，它添加了以下所有内容：

- @Configuration：将类标记为应用程序上下文的 bean 定义源。
- @EnableAutoConfiguration：告诉 Spring Boot 根据类路径设置、其他 bean 和各种属性设置开始添加 bean。例如，如果 spring-webmvc 在类路径上，则此注释将应用程序标记为 Web 应用程序并激活关键行为，例如设置 DispatcherServlet。
- @ComponentScan：告诉 Spring 在 com/example 包中查找其他组件、配置和服务，让它找到控制器。

main() 方法使用 Spring Boot 的 SpringApplication.run() 方法来启动应用程序。您是否注意到没有一行 XML？也没有 web.xml 文件。这个 Web 应用程序是 100% 纯 Java，您不必处理任何管道或基础设施的配置。

您还需要一个 [CommandLineRunner](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#boot-features-command-line-runner) 来注入 BookRepository 并使用不同的参数多次调用它。来自 src/main/java/com/example/caching/AppRunner.java 显示了该类。

> 将需要进一步评估的消息参数传递给 Guava com.google.common.base.Preconditions 检查可能会导致性能下降。这是因为无论是否需要它们，每个参数都必须在实际调用方法之前解决。
类似地，将连接的字符串传递给日志记录方法也会导致不必要的性能损失，因为每次调用方法时都会执行连接，无论日志级别是否足够低以显示消息。
相反，您应该构建代码以将静态或预先计算的值传递到前提条件检查和记录调用。
具体来说，应该使用内置的字符串格式而不是字符串连接，如果消息是方法调用的结果，则应该完全跳过 Preconditions，而是有条件地抛出相关异常。

如果此时您尝试运行该应用程序，您应该注意到它非常慢，即使您多次检索完全相同的书。

### 启用缓存

现在您可以在 SimpleBookRepository 上启用缓存，以便将书籍缓存在书籍缓存中。来自 src/main/java/com/example/caching/SimpleBookRepository.java。

您现在需要启用缓存注释的处理，以下示例 src/main/java/com/example/caching/CachingApplication.java 显示了如何执行此操作。

[@EnableCaching](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/cache/annotation/EnableCaching.html) 注解触发一个后处理器(post-processor)，它检查每个 Spring bean 是否存在公共方法上有缓存注解。如果找到这样的注解，则会自动创建一个代理来拦截方法调用并相应地处理缓存行为。

后处理器处理[@Cacheable](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/cache/annotation/Cacheable.html)、[@CachePut](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/cache/annotation/CachePut.html) 和 [@CacheEvict](https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/cache/annotation/CacheEvict.html) 注释。您可以参考 Javadoc 和[参考指南](https://docs.spring.io/spring/docs/current/spring-framework-reference/html/cache.html)以获取更多详细信息。

Spring Boot 会自动配置一个合适的 CacheManager 作为相关缓存的提供者。有关更多详细信息，请参阅 [SpringBoot文档](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-caching.html)。

我们的示例不使用特定的缓存库，因此我们的缓存存储是使用 ConcurrentHashMap 的简单回退。缓存抽象支持广泛的缓存库，并且完全符合 JSR-107 (JCache)。

## 构建一个可执行的 JAR

您可以使用 Gradle 或 Maven 从命令行运行应用程序。您还可以构建一个包含所有必要依赖项、类和资源的单个可执行 JAR 文件并运行它。构建可执行 jar 可以在整个开发生命周期、跨不同环境等中轻松地作为应用程序交付、版本化和部署服务。

如果您使用 Gradle，则可以使用 ./gradlew bootRun 运行应用程序。或者，您可以使用 ./gradlew build 构建 JAR 文件，然后运行 ​​JAR 文件，如下所示：
`java -jar build/libs/caching-0.1.0.jar`
如果您使用 Maven，则可以使用 ./mvnw spring-boot:run 运行应用程序。或者，您可以使用 ./mvnw clean package 构建 JAR 文件，然后运行 ​​JAR 文件，如下所示：
`java -jar target/caching-0.1.0.jar`

## 测试应用程序

现在启用了缓存，您可以再次运行应用程序并通过添加额外的调用（有或没有相同的 ISBN）来查看差异。它应该有很大的不同。
在前面的示例输出中，第一次检索一本书仍然需要三秒钟。但是，同一本书的第二次和后续时间要快得多，这表明缓存正在发挥作用。
