# 构建 RESTful Web 服务

<https://spring.io/guides/gs/rest-service/>

本指南将引导您完成使用 Spring 创建“Hello, World”RESTful Web 服务的过程。

你将建造什么
您将构建一个在 <http://localhost:8080/greeting> 处接受 HTTP GET 请求的服务。

它将以 JSON 表示的问候进行响应，如以下清单所示：
`{"id":1,"content":"Hello, World!"}`
您可以使用查询字符串中的可选名称参数自定义问候语，如以下清单所示：
`http://localhost:8080/greeting?name=UserCOPY`
name 参数值覆盖 World 的默认值并反映在响应中，如以下清单所示：
`{"id":1,"content":"Hello, User!"}`

What You Need

- JDK 1.8 or later
- Gradle 4+ or Maven 3.2+

## 编码

### 创建资源表示类

设置了项目和构建系统，创建您的 Web 服务。

从考虑服务交互开始这个过程。

该服务将处理 /greeting 的 GET 请求，可以选择在查询字符串中使用名称参数。 GET 请求应返回 200 OK 响应，其中包含表示问候的正文中的 JSON。 它应该类似于以下输出：

```json
{
    "id": 1,
    "content": "Hello, World!"
}
```

id 字段是问候语的唯一标识符，内容是问候语的文本表示。

要对问候表示建模，请创建一个资源表示类。 为此，请提供一个普通的旧 Java 对象，其中包含 id 和内容数据的字段、构造函数和访问器，来自 src/main/java/com/example/restservice/Greeting.java.

此应用程序使用 Jackson JSON 库自动将 Greeting 类型的实例编组为 JSON。 网络启动器默认包含 Jackson。

### 创建资源控制器

在 Spring 构建 RESTful Web 服务的方法中，HTTP 请求由控制器处理。 这些组件由@RestController 注解标识，来自 src/main/java/com/example/restservice/GreetingController.java，通过返回 Greeting 的新实例来处理 /greeting 的 GET 请求。

这个控制器简洁明了，但引擎盖下有很多事情要做。我们一步一步分解。

@GetMapping 注解确保对 /greeting 的 HTTP GET 请求映射到 greeting() 方法。

> 其他 HTTP 动词有伴随注释（例如，@PostMapping 用于 POST）。它们都派生自， @RequestMapping 注释，并且可以用作同义词（例如 @RequestMapping(method=GET)）。

@RequestParam 将查询字符串参数 name 的值绑定到 greeting() 方法的 name 参数中。如果请求中没有 name 参数，则使用 World 的 defaultValue。

方法体的实现基于来自计数器的下一个值创建并返回具有 id 和 content 属性的新 Greeting 对象，并使用问候模板格式化给定名称。

传统 MVC 控制器和前面显示的 RESTful Web 服务控制器之间的一个关键区别是 HTTP 响应主体的创建方式。这个 RESTful Web 服务控制器不依赖于视图技术将问候数据在服务器端呈现为 HTML，而是填充并返回一个 Greeting 对象。对象数据将作为 JSON 直接写入 HTTP 响应。

此代码使用 Spring @RestController 注释，它将类标记为控制器，其中每个方法都返回域对象而不是视图。它是同时包含@Controller 和@ResponseBody 的简写。

Greeting 对象必须转换为 JSON。由于 Spring 的 HTTP 消息转换器支持，您无需手动进行此转换。因为 [Jackson 2](https://github.com/FasterXML/jackson) 在类路径上，所以会自动选择 Spring 的 MappingJackson2HttpMessageConverter 将 Greeting 实例转换为 JSON。

@SpringBootApplication 是一个方便的注解，它添加了以下所有内容：

- @Configuration：将类标记为应用程序上下文的 bean 定义源。
- @EnableAutoConfiguration：告诉 Spring Boot 根据类路径设置、其他 bean 和各种属性设置开始添加 bean。例如，如果 spring-webmvc 在类路径上，则此注释将应用程序标记为 Web 应用程序并激活关键行为，例如设置 DispatcherServlet。
- @ComponentScan：告诉 Spring 在 com/example 包中查找其他组件、配置和服务，让它找到控制器。

main() 方法使用 Spring Boot 的 SpringApplication.run() 方法来启动应用程序。您是否注意到没有一行 XML？也没有 web.xml 文件。这个 Web 应用程序是 100% 纯 Java，您不必处理任何管道或基础设施的配置。

## 构建一个可执行的 JAR

您可以使用 Gradle 或 Maven 从命令行运行应用程序。 您还可以构建一个包含所有必要依赖项、类和资源的单个可执行 JAR 文件并运行它。 构建可执行 jar 可以在整个开发生命周期、跨不同环境等中轻松地作为应用程序交付、版本化和部署服务。

如果您使用 Gradle，则可以使用 ./gradlew bootRun 运行应用程序。 或者，您可以使用 ./gradlew build 构建 JAR 文件，然后运行 JAR 文件，如下所示：
`java -jar build/libs/rest-service-0.1.0.jar`

如果您使用 Maven，则可以使用 ./mvnw spring-boot:run 运行应用程序。 或者，您可以使用 ./mvnw clean package 构建 JAR 文件，然后运行 JAR 文件，如下所示：
`java -jar target/gs-rest-service-0.1.0.jar`

## 测试服务

现在服务已经启动，访问 `curl http://localhost:8080/greeting`，你应该会看到：
`{"id":1,"content":"Hello, World!"}`

通过访问 `curl 'http://localhost:8080/greeting?name=User'` 提供名称查询字符串参数。 注意 content 属性的值是如何从 Hello, World! 改变的。 致您好，用户！，如以下清单所示：
`{"id":2,"content":"Hello, User!"}`

此更改表明 GreetingController 中的 @RequestParam 安排按预期工作。 name 参数已被赋予 World 的默认值，但可以通过查询字符串显式覆盖。

还要注意 id 属性如何从 1 更改为 2。这证明您正在处理多个请求中的同一个 GreetingController 实例，并且它的计数器字段在每次调用时都按预期递增。
