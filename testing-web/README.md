# Spring Boot Test 介绍

Spring Test与JUnit等其他测试框架结合起来，提供了便捷高效的测试手段。而Spring Boot Test 是在Spring Test之上的再次封装，增加了切片测试，增强了mock能力。

整体上，Spring Boot Test支持的测试种类，大致可以分为如下三类：

- 单元测试：一般面向方法，编写一般业务代码时，测试成本较大。涉及到的注解有@Test。
- 切片测试：一般面向难于测试的边界功能，介于单元测试和功能测试之间。涉及到的注解有@RunWith @WebMvcTest等。
- 功能测试：一般面向某个完整的业务功能，同时也可以使用切面测试中的mock能力，推荐使用。涉及到的注解有@RunWith @SpringBootTest等。

功能测试过程中的几个关键要素及支撑方式如下：

- 测试运行环境：通过@RunWith 和 @SpringBootTest启动spring容器。
- mock能力：Mockito提供了强大mock功能。
- 断言能力：AssertJ、Hamcrest、JsonPath提供了强大的断言能力。

## DEMO

您将构建一个简单的 Spring 应用程序并使用 JUnit 对其进行测试。 你可能已经 知道如何编写和运行应用程序中各个类的单元测试，所以， 对于本指南，我们将专注于使用 Spring Test 和 Spring Boot 特性进行测试 Spring 和你的代码之间的交互。 您将从一个简单的测试开始 应用程序上下文加载成功并继续使用以下方法仅测试 Web 层 Spring的`MockMvc`。

### 需要什么

- IDE
- JDK 1.8 或更高版本
- Gradle 4+ 或 Maven 3.2+
- 可以将代码直接导入 IDE：
  - Spring Tool Suite (STS)
  - IntelliJ IDEA
  
### 创建一个简单的应用程序

为您的 Spring 应用程序创建一个新控制器。参见 src/main/java/com/example/testingweb/HomeController.java。
示例代码没有指定 GET 与 PUT、POST 等。 默认情况下，@RequestMapping 映射所有 HTTP 操作。 您可以使用 @GetMapping 或 @RequestMapping(method=GET) 来缩小此映射。

### 运行应用程序

Spring Initializr 为您创建了一个应用程序类（具有 main() 方法的类）。 参见 src/main/java/com/example/testingweb/TestingWebApplication.java。

@SpringBootApplication 是一个方便的注解，它添加了以下所有内容：

- @Configuration：将类标记为应用程序上下文的 bean 定义源。
- @EnableAutoConfiguration：告诉 Spring Boot 根据类路径设置、其他 bean 和各种属性设置开始添加 bean。
- @EnableWebMvc：将应用程序标记为 Web 应用程序并激活关键行为，例如设置 DispatcherServlet。当 Spring Boot 在类路径上看到 spring-webmvc 时，它会自动添加它。
- @ComponentScan：告诉 Spring 在 com.example.testingweb 包中查找其他组件、配置和服务，让它找到 HelloController 类。

main() 方法使用 Spring Boot 的 SpringApplication.run() 方法来启动应用程序。您是否注意到没有一行 XML？也没有 web.xml 文件。这个 Web 应用程序是 100% 纯 Java，您不必处理任何管道或基础设施的配置。 Spring Boot 为您处理所有这些。

显示记录输出。该服务应在几秒钟内启动并运行。

### 测试应用程序

现在应用程序正在运行，您可以对其进行测试。 您可以在 <http://localhost:8080> 加载主页。 但是，为了让您对应用程序在您进行更改时可以正常工作更有信心，您希望自动化测试。

Spring Boot 假设您计划测试您的应用程序，因此它会将必要的依赖项添加到您的构建文件（build.gradle 或 pom.xml）中。
您可以做的第一件事是编写一个简单的健全性检查测试，如果应用程序上下文无法启动，该测试将失败。
代码如下：

```Java
package com.example.testingweb;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestingWebApplicationTests {

 @Test
 public void contextLoads() {
 }

}
```

@SpringBootTest 注解告诉 Spring Boot 寻找一个主配置类（例如一个带有 @SpringBootApplication 的类）并使用它来启动一个 Spring 应用程序上下文。 您可以在 IDE 或命令行中运行此测试（通过运行 ./mvnw test 或 ./gradlew test），它应该会通过。 为了让自己相信上下文正在创建您的控制器，您可以添加一个断言，参见 src/test/java/com/example/testingweb/SmokeTest.java。

Spring 解释了@Autowired 注解，并且在测试方法运行之前注入了控制器。 我们使用 AssertJ（它提供了 assertThat() 和其他方法）来表达测试断言。

> Spring Test 支持的一个很好的特性是应用程序上下文在测试之间被缓存。 这样，如果您在一个测试用例中有多个方法或具有相同配置的多个测试用例，它们会产生仅启动一次应用程序的成本。 您可以使用 @DirtiesContext 注释来控制缓存。

进行健全性检查很好，但您还应该编写一些测试来断言应用程序的行为。 为此，您可以启动应用程序并侦听连接（就像在生产中所做的那样），然后发送 HTTP 请求并断言响应。 参见 src/test/java/com/example/testingweb/HttpRequestTest.java。

注意使用 webEnvironment=RANDOM_PORT 以随机端口启动服务器（有助于避免测试环境中的冲突）并使用 @LocalServerPort 注入端口。 另外，请注意 Spring Boot 已经自动为您提供了一个 TestRestTemplate。 您所要做的就是将@Autowired 添加到其中。

另一个有用的方法是根本不启动服务器，而只测试它下面的层，Spring 处理传入的 HTTP 请求并将其交给你的控制器。 这样，几乎使用了整个堆栈，并且您的代码将以与处理真正的 HTTP 请求完全相同的方式被调用，但无需启动服务器。 为此，请使用 Spring 的 MockMvc 并要求通过在测试用例上使用 @AutoConfigureMockMvc 注释为您注入它。 参见 src/test/java/com/example/testingweb/TestingWebApplicationTest.java。

在这个测试中，启动了完整的 Spring 应用程序上下文，但没有服务器。 我们可以使用 @WebMvcTest 将测试范围缩小到仅 Web 层，参见 src/test/java/com/example/testingweb/WebLayerTest.java。

测试断言与前一种情况相同。 然而，在这个测试中，Spring Boot 只实例化了 web 层而不是整个上下文。 在具有多个控制器的应用程序中，您甚至可以通过使用 @WebMvcTest(HomeController.class) 来请求仅实例化一个。

到目前为止，我们的 HomeController 很简单，没有依赖关系。 我们可以通过引入一个额外的组件来存储问候语（可能在一个新的控制器中）来使其更加真实。 参见 src/main/java/com/example/testingweb/GreetingController.java。

然后创建一个问候服务，参见 src/main/java/com/example/testingweb/GreetingService.java。

Spring 自动将服务依赖注入到控制器中（因为构造函数签名）。 参见 src/test/java/com/example/testingweb/WebMockTest.java 示例了如何使用 @WebMvcTest 测试此控制器。

我们使用@MockBean 为 GreetingService 创建和注入一个模拟（如果不这样做，应用程序上下文将无法启动），我们使用 Mockito 设置它的期望。

### 概括

开发了一个 Spring 应用程序并使用 JUnit 和 Spring MockMvc 对其进行了测试，并使用了 Spring Boot 来隔离 Web 层并加载一个特殊的应用程序上下文。
