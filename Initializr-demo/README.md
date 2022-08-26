# 快速入门

Spring官方提供了[Spring Initializr](https://start.spring.io/)工具来创建Spring Boot应用。

Spring Boot的基础结构

- src/main/java 程序入口 InitializrDemoApplication
- src/main/resources 配置文件 application.properties
- src/test 测试入口 InitializrDemoApplicationTests
- 其中 XxxApplication 和 XxxApplicationTests 类都可以直接运行来启动当前创建的项目，由于目前该项目未配合任何数据访问或Web模块，程序会在加载完Spring之后结束运行。

工程结构

```text
com
  +- example
    +- myproject
      +- Application.java
      |
      +- domain
      |  +- Customer.java
      |  +- CustomerRepository.java
      |
      +- service
      |  +- CustomerService.java
      |
      +- web
      |  +- CustomerController.java
      |
```

- root package: com.example.myproject, 所有的类和其他package都在 root package 之下
- 应用主类 Application.java ，直接位于root package下。通常会在应用主类中做一些框架配置扫描等配置，放在root package下, 减少手工配置来扫描到希望被Spring加载的内容
- com.example.myproject.domain 用于定义实体映射关系与数据访问相关的接口和实现
- com.example.myproject.service 用于编写业务逻辑相关的接口与实现
- com.example.myproject.web 用于编写Web层相关的实现，如 Spring MVC的Controller等
- 默认情况下，Spring Boot的应用主类会自动扫描root package以及所有子包下的所有类来进行初始化

非典型结构下的初始化

- 加载非root package下的内容
  - 使用@ComponentScan注解指定具体的加载包
  - 使用@Bean注解来初始化

```java
@SpringBootApplication
@ComponentScan(basePackages="com.example")
public class Bootstrap {
    public static void main(String[] args) {
        SpringApplication.run(Bootstrap.class, args);
    }
}

@SpringBootApplication
public class Bootstrap {
    public static void main(String[] args) {
        SpringApplication.run(Bootstrap.class, args);
    }
    @Bean
    public CustomerController customerController() {
        return new CustomerController();
    }
}
```

pom.xml

- 项目元数据 创建时候输入的 Project Metadata 部分，包括 Maven 项目的基本元素 groupId、artifactId、version、name、description等
- parent 继承 spring-boot-starter-parent 的依赖管理，控制版本与打包等内容
- dependencies 项目具体依赖
  - spring-boot-starter-web （包含 Spring MVC） 用于实现HTTP接口
  - spring-boot-starter-test 用于编写单元测试的依赖包
- build 构建配置部分 默认使用了 spring-boot-maven-plugin，配合spring-boot-starter-parent就可以把Spring Boot应用打包成JAR来直接运行。

编写一个HTTP接口

- 创建 HelloController.java

编写单元测试用例

- src/test/ 测试入口 InitializrDemoApplicationTests.java
- 使用 MockServletContext 来构建一个空的 WebApplicationContext，HelloController 就可以在 @Before 函数中创建并传递到 MockMvcBuilders.standaloneSetup() 函数中。

## 问题

**ERROR**: `org.junit.platform.commons.JUnitException: @BeforeAll method 'public void com.example.Initializrdemo.InitializrDemoApplicationTests.setUp() throws java.lang.Exception' must be static unless the test class is annotated with @TestInstance(Lifecycle.PER_CLASS).`
原因：@BeforeAll 注解的方法非 static 方法

```java
import org.junit.jupiter.api.BeforeAll;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
...
    @BeforeAll
    public void setUp() throws Exception {
    mockMvc = MockMvcBuilders.standaloneSetup(new HelloController()).build();
    }
```

> 测试实例生命周期: 为了允许隔离执行单个的测试方法，并避免由于可变测试实例状态而产生的意外副作用，JUnit在执行每个测试方法之前创建每个测试类的新实例。这个”per-method”测试实例生命周期是 JUnit Jupiter 中的默认行为，类似于JUnit以前的所有版本。如果您希望JUnit Jupiter在同一个测试实例上执行所有测试方法，只需使用 @TestInstance(Lifecycle.PER_CLASS) 对您的测试类进行注解即可。当使用这种模式时，每个测试类将创建一个新的测试实例。因此，如果您的测试方法依赖于存储在实例变量中的状态，则可能需要在 @BeforeEach 或 @AfterEach 方法中重置该状态。“per-class”模式比默认的”per-method”模式有一些额外的好处。具体来说，使用”per-class”模式，可以在非静态方法和接口默认方法上声明 @BeforeAll 和 @AfterAll（否则@BeforeAll与@AfterAll必须是注解在static的方法上才能生效） 。因此，”per-class”模式也可以在 @Nested 测试类中使用 @BeforeAll 和 @AfterAll 方法。

解决：去除 @BeforeAll setUp() 方法, 用 `@AutoConfigureMockMvc` 对测试类进行注解。
@BeforeAll 注解的方法改为 `static` 或 用 `@TestInstance(Lifecycle.PER_CLASS)` 对测试类进行注解。
