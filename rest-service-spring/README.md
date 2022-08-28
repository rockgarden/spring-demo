# 使用 Spring 构建 REST 服务

REST 已迅速成为在 Web 上构建 Web 服务的事实标准，因为它们易于构建且易于使用。

关于 REST 如何适应微服务世界还有一个更大的讨论，但是——对于本教程——让我们看看构建 RESTful 服务。

为什么是 REST？ REST 包含 Web 的规则，包括其架构、优势和其他一切。这并不奇怪，因为它的作者 Roy Fielding 参与了十几个管理网络运行方式的规范。

有什么好处？ Web 及其核心协议 HTTP 提供了一系列功能：

- 合适的操作 Suitable actions（GET、POST、PUT、DELETE……）
- 缓存 Caching
- 重定向和转发 Redirection and forwarding
- 安全性（加密和身份验证） Security (encryption and authentication)

这些都是构建弹性服务的关键因素。但这还不是全部。网络是由许多微小的规范构成的，因此它能够轻松发展，而不会陷入“标准战争”的泥潭。

开发人员能够利用 3rd 方工具包来实现这些不同的规范，并立即让客户端和服务器技术触手可及。

通过在 HTTP 之上构建，REST API 提供了构建方法：

- 向后兼容的 API Backwards compatible
- 可演进的 API Evolvable
- 可扩展的服务 Scaleable services
- 安全的服务 Securable services
- 一系列无状态服务到有状态服务 A spectrum of stateless to stateful services

重要的是要意识到，无论多么普遍，REST 本身并不是一种标准，而是一种方法、一种风格、一组对您的架构的约束，可以帮助您构建 Web 规模的系统。在本教程中，我们将使用 Spring 产品组合来构建 RESTful 服务，同时利用 REST 的无堆栈特性。

What’s important to realize is that REST, however ubiquitous, is not a standard, per se, but an approach, a style, a set of constraints on your architecture that can help you build web-scale systems. In this tutorial we will use the Spring portfolio to build a RESTful service while leveraging the stackless features of REST.

## 入门

在完成本教程时，我们将使用 Spring Boot。 转到 Spring Initializr 并将以下依赖项添加到项目中：

- 网络
- JPA
- H2

将名称更改为“rest-service-spring”，然后选择“生成项目”。 将下载一个 .zip。 解压它。 在里面你会发现一个简单的、基于 Maven 的项目，包括一个 pom.xml 构建文件（注意：你可以使用 Gradle。本教程中的示例将基于 Maven。）

Spring Boot 可以与任何 IDE 一起使用。 您可以使用 Eclipse、IntelliJ IDEA、Netbeans 等。Spring Tool Suite 是一个开源的、基于 Eclipse 的 IDE 发行版，它提供了 Eclipse 的 Java EE 发行版的超集。 它包括使使用 Spring 应用程序更加容易的功能。

让我们从我们能构建的最简单的东西开始。 事实上，为了尽可能简单，我们甚至可以省略 REST 的概念。 （稍后，我们将添加 REST 以了解其中的区别。）

概述：我们将创建一个简单的工资单服务来管理公司的员工。 我们将员工对象存储在（H2 内存中）数据库中，并访问它们（通过称为 JPA 的东西）。 然后我们将使用允许通过 Internet 访问的东西（称为 Spring MVC 层）包装它。

以下代码在我们的系统中定义了一个 Employee。

这个 Java 类包含很多：

- @Entity 是一个 JPA 注释，用于使该对象准备好存储在基于 JPA 的数据存储中。
- id、name 和 role 是我们 Employee 域对象的属性。 id 用更多的 JPA 注释标记，以表明它是主键并由 JPA 提供程序自动填充。
- 当我们需要创建一个新实例但还没有 id 时，会创建一个自定义构造函数。

有了这个域对象(domain object)定义，我们现在可以转向 Spring Data JPA 来处理繁琐的数据库交互。

Spring Data JPA 存储库是与支持针对后端数据存储创建、读取、更新和删除记录的方法的接口。 在适当的情况下，一些存储库还支持数据分页和排序。 Spring Data 根据接口中方法命名中的约定来综合实现。

除了 JPA 之外，还有多个存储库实现。 您可以使用 Spring Data MongoDB、Spring Data GemFire、Spring Data Cassandra 等。

Spring 使访问数据变得容易。 通过简单地声明以下 EmployeeRepository 接口，我们将能够自动

- 创建新员工
- 更新现有的
- 删除员工
- 查找员工（一个、全部或按简单或复杂属性搜索）

为了获得所有这些免费功能，我们所要做的就是声明一个扩展 Spring Data JPA 的 JpaRepository 的接口，将域类型指定为 Employee，id 类型指定为 Long。

Spring Data 的存储库解决方案[repository solution](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories)可以回避数据存储细节，而是使用特定于域的术语解决大多数问题。

信不信由你，这足以启动应用程序！ Spring Boot 应用程序至少是一个公共静态 void 主入口点和 @SpringBootApplication 注释。 这告诉 Spring Boot 尽可能提供帮助。

@SpringBootApplication 是一个引入组件扫描、自动配置和属性支持的元注释。 在本教程中，我们不会深入探讨 Spring Boot 的细节，但本质上，它将启动一个 servlet 容器并提供我们的服务。

尽管如此，没有数据的应用程序并不是很有趣，所以让我们预加载它。 Spring 将自动加载类 LoadDatabase.java。

加载时会发生什么？

- 加载应用程序上下文后，Spring Boot 将运行所有 CommandLineRunner bean。
- 此运行程序将请求您刚刚创建的 EmployeeRepository 的副本。
- 使用它，它将创建两个实体并存储它们。

右键单击并运行 RestServiceSpringApplication ，这就是你得到的：

显示数据预加载的控制台输出片段

```log
2022-08-28 20:54:47.717  INFO 66768 --- [           main] c.e.restservicespring.LoadDatabase       : Preloading Employee{id=1, name='Bilbo Baggins', role='burglar'}
2022-08-28 20:54:47.718  INFO 66768 --- [           main] c.e.restservicespring.LoadDatabase       : Preloading Employee{id=2, name='Frodo Baggins', role='thief'}
```

## HTTP 是平台

要使用 Web 层包装您的存储库，您必须使用 Spring MVC。 多亏了 Spring Boot，代码基础设施很少。 相反，我们可以专注于行动：EmployeeController.java。

- @RestController 表示每个方法返回的数据会直接写入响应体，而不是渲染模板。
- EmployeeRepository 由构造函数注入到控制器中。
- 我们为每个操作（@GetMapping、@PostMapping、@PutMapping 和 @DeleteMapping，对应于 HTTP GET、POST、PUT 和 DELETE 调用）设置了路由。
- EmployeeNotFoundException 是一个异常，用于指示何时查找但未找到员工。

EmployeeNotFoundException.java： 当抛出 EmployeeNotFoundException 时，Spring MVC 配置的这个异常用于呈现 HTTP 404。

EmployeeNotFoundAdvice.java：

- @ResponseBody 表示此建议直接呈现到响应正文中。
- @ExceptionHandler 将建议配置为仅在抛出 EmployeeNotFoundException 时才响应。
- @ResponseStatus 表示要发出 HttpStatus.NOT_FOUND，即 HTTP 404。
- 建议的主体生成内容。 在这种情况下，它会给出异常消息。

要启动应用程序，请右键单击 RestServiceSpringApplication 中的 public static void main 并从您的 IDE 中选择 Run，或者：

若 Spring Initializr 时使用 maven 包装器，则可输入：
`$ ./mvnw clean spring-boot:run`
或者使用您安装的 Maven 版本输入：
`$ mvn clean spring-boot:run`
当应用程序启动时，我们可以立即对其进行询问。
`$ curl -v localhost:8080/employees`

```log
*   Trying ::1...
* TCP_NODELAY set
* Connected to localhost (::1) port 8080 (#0)
> GET /employees HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.54.0
> Accept: */*
>
< HTTP/1.1 200
< Content-Type: application/json;charset=UTF-8
< Transfer-Encoding: chunked
< Date: Thu, 09 Aug 2018 17:58:00 GMT
<
* Connection #0 to host localhost left intact
[{"id":1,"name":"Bilbo Baggins","role":"burglar"},{"id":2,"name":"Frodo Baggins","role":"thief"}]
```

在这里，您可以看到压缩格式的预加载数据。
如果您尝试查询一个不存在的用户......

$ curl -v localhost:8080/employees/99

```log
*   Trying ::1...
* TCP_NODELAY set
* Connected to localhost (::1) port 8080 (#0)
> GET /employees/99 HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.54.0
> Accept: */*
>
< HTTP/1.1 404
< Content-Type: text/plain;charset=UTF-8
< Content-Length: 26
< Date: Thu, 09 Aug 2018 18:00:56 GMT
<
* Connection #0 to host localhost left intact
Could not find employee 99
```

此消息很好地显示了 HTTP 404 错误以及自定义消息找不到员工 99。

显示当前编码的交互并不难……​

如果您使用 Windows 命令提示符发出 cURL 命令，则以下命令可能无法正常工作。您必须选择一个支持单引号参数的终端，或者使用双引号，然后转义 JSON 中的那些。

为了创建一个新的员工记录，我们在终端中使用以下命令——开头的 $ 表示它后面是终端命令：

`$ curl -X POST localhost:8080/employees -H 'Content-type:application/json' -d '{"name": "Samwise Gamgee", "role": "gardener"}'`
然后它存储新创建的员工并将其发送回给我们：
`{"id":3,"name":"Samwise Gamgee","role":"gardener"}`
您可以更新用户。让我们改变他的角色。
`$ curl -X PUT localhost:8080/employees/3 -H 'Content-type:application/json' -d '{"name": "Samwise Gamgee", "role": "ring bearer"}'`
我们可以看到输出中反映的变化。
`{"id":3,"name":"Samwise Gamgee","role":"ring bearer"}`
您构建服务的方式可能会产生重大影响。在这种情况下，我们说更新，但替换是更好的描述。例如，如果未提供名称，则它将被取消。
最后，您可以像这样删除用户：
`$ curl -X DELETE localhost:8080/employees/3`
现在如果我们再看一遍，它就不见了
`$ curl localhost:8080/employees/3`
Could not find employee 3
这一切都很好，但是我们有 RESTful 服务了吗？ （答案是否定的。）

## 问题

[WARN] `Persistent entities should not be used as arguments of "@RequestMapping" methods (java:S4684)`
`Replace this persistent entity with a simple POJO or DTO object.`
**原因**：一方面，Spring MVC 自动将请求参数绑定到声明为使用 @RequestMapping 注释的方法的参数的 bean。由于这种自动绑定功能，可以在 @RequestMapping 注释方法的参数上提供一些意想不到的字段。
另一方面，持久对象（@Entity 或 @Document）链接到底层数据库，并由持久性框架自动更新，例如 Hibernate、JPA 或 Spring Data MongoDB。
这两个事实结合在一起可能会导致恶意攻击：如果将持久对象用作带有 @RequestMapping 注释的方法的参数，则有可能从特制的用户输入中将意外字段的内容更改到数据库中。
因此，应避免使用 @Entity 或 @Document 对象作为带有 @RequestMapping 注释的方法的参数。
除了@RequestMapping，这条规则还考虑了Spring Framework 4.3中引入的注解：@GetMapping、@PostMapping、@PutMapping、@DeleteMapping、@PatchMapping。
**解决**：用一个简单的 POJO 或 DTO 对象替换这个持久化实体，而不是`do the mapping between "sendINObject" and "persistentObject"`。

