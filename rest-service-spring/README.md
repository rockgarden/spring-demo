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

## 变得 RESTful？

到目前为止，您拥有一个基于 Web 的服务来处理涉及员工数据的核心操作。但这还不足以让事情变得“RESTful”。

- /employees/3 之类的漂亮 URL 不是 REST。
- 仅仅使用 GET、POST 等不是 REST。
- 安排好所有的 CRUD 操作不是 REST。

事实上，到目前为止，我们构建的更好地描述为 RPC（远程过程调用）。那是因为没有办法知道如何与这个服务交互。如果您今天发布此内容，您还必须编写文档或在某个地方托管开发人员的门户，其中包含所有详细信息。

Roy Fielding 的这个陈述可能会进一步为 REST 和 RPC 之间的区别提供线索：

> I am getting frustrated by the number of people calling any HTTP-based interface a REST API. Today’s example is the SocialSite REST API. That is RPC. It screams RPC. There is so much coupling on display that it should be given an X rating.
What needs to be done to make the REST architectural style clear on the notion that hypertext is a constraint? In other words, if the engine of application state (and hence the API) is not being driven by hypertext, then it cannot be RESTful and cannot be a REST API. Period. Is there some broken manual somewhere that needs to be fixed?
— Roy Fielding <https://roy.gbiv.com/untangled/2008/rest-apis-must-be-hypertext-driven>

在我们的表示中不包括超媒体的副作用是客户端必须硬编码 URI 来导航 API。这导致了与网络电子商务兴起之前相同的脆弱性。这表明我们的 JSON 输出需要一点帮助。

介绍 [Spring HATEOAS](https://spring.io/projects/spring-hateoas)，这是一个 Spring 项目，旨在帮助您编写超媒体驱动的输出。要将您的服务升级为 RESTful，请将其添加到您的构建中：

将 Spring HATEOAS (spring-boot-starter-hateoas) 添加到 pom.xml 的依赖项部分。

这个小型库将为我们提供定义 RESTful 服务的结构，然后以可接受的格式呈现它以供客户使用。

任何 RESTful 服务的一个关键要素是添加指向相关操作的[链接](https://tools.ietf.org/html/rfc8288)。 
要使您的控制器更加 RESTful，将改进如下代码：

```java
@GetMapping("/employees/{id}")
    Employee one(@PathVariable Long id) {
        return repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));
    }
```

获取单个项目资源，改为：

```java
@GetMapping("/employees/{id}")
EntityModel<Employee> one(@PathVariable Long id) {

  Employee employee = repository.findById(id) //
      .orElseThrow(() -> new EmployeeNotFoundException(id));

  return EntityModel.of(employee, //
      linkTo(methodOn(EmployeeController.class).one(id)).withSelfRel(),
      linkTo(methodOn(EmployeeController.class).all()).withRel("employees"));
}
```

本教程基于 Spring MVC，并使用 WebMvcLinkBuilder 中的静态帮助方法来构建这些链接。如果您在项目中使用 Spring WebFlux，则必须改用 WebFluxLinkBuilder。

这与我们之前的情况非常相似，但有一些变化：

- 该方法的返回类型已从 Employee 更改为 `EntityModel<Employee>`。 `EntityModel<T>` 是来自 Spring HATEOAS 的通用容器，它不仅包括数据，还包括链接集合。
- `linkTo(methodOn(EmployeeController.class).one(id)).withSelfRel()` 要求 Spring HATEOAS 建立到 EmployeeController 的 one() 方法的链接，并将其标记为[自链接](https://www.iana.org/assignments/link-relations/link-relations.xhtml)。
- `linkTo(methodOn(EmployeeController.class).all()).withRel("employees")` 要求 Spring HATEOAS 建立到聚合根 all() 的链接，并将其称为“employees”。

“建立链接”是什么意思？ Spring HATEOAS 的核心类型之一是 Link。它包括一个 URI 和一个 rel（关系）。链接是赋予网络权力的东西。在万维网之前，其他文档系统会呈现信息或链接，但正是将文档与这种关系元数据链接在一起，才将网络缝合在一起。

Roy Fielding 鼓励使用使 Web 成功的相同技术构建 API，链接就是其中之一。

如果重新启动应用程序并查询 Bilbo 的员工记录，您将得到与之前略有不同的响应：

> Curling prettier
当你的 curl 输出变得更复杂时，它可能变得难以阅读。 使用这个或其他技巧来美化 curl 返回的 json：

  ```bash
  # 指示部分将输出通过管道传输到 json_pp 并要求它使您的 JSON 更漂亮。 （或者使用任何你喜欢的工具！）
  # v------------------v
  curl -v localhost:8080/employees/1 | json_pp
  ```

单个员工的 RESTful 表示:

```json
{
   "_links" : {
      "employees" : {
         "href" : "http://localhost:8080/employees"
      },
      "self" : {
         "href" : "http://localhost:8080/employees/1"
      }
   },
   "id" : 1,
   "name" : "Bilbo Baggins",
   "role" : "burglar"
}
```

这个解压缩的输出不仅显示了您之前看到的数据元素（id、名称和角色），而且还显示了一个包含两个 URI 的 _links 条目。 整个文档使用 [HAL](http://stateless.co/hal_specification.html) 进行格式化。

HAL 是一种轻量级[媒体类型](https://tools.ietf.org/html/draft-kelly-json-hal-08)，它不仅可以编码数据，还可以编码超媒体控件，提醒消费者注意他们可以导航的 API 的其他部分。 在这种情况下，有一个“自我”链接（有点像代码中的 this 语句）以及一个返回[聚合根(aggregate root)](https://www.google.com/search?q=What+is+an+aggregate+root)的链接。

为了使聚合根 ALSO 更加 RESTful，您希望包括顶级链接，同时还包括其中的任何 RESTful 组件。

所以我们把这个

获取聚合根

```java
@GetMapping("/employees")
List<Employee> all() {
  return repository.findAll();
}
```

进入这个

获取聚合根资源

```java
@GetMapping("/employees")
CollectionModel<EntityModel<Employee>> all() {

  List<EntityModel<Employee>> employees = repository.findAll().stream()
      .map(employee -> EntityModel.of(employee,
          linkTo(methodOn(EmployeeController.class).one(employee.getId())).withSelfRel(),
          linkTo(methodOn(EmployeeController.class).all()).withRel("employees")))
      .collect(Collectors.toList());

  return CollectionModel.of(employees, linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
}
```

CollectionModel<> 是另一个 Spring HATEOAS 容器； 它旨在封装资源集合——而不是像之前的 EntityModel<> 那样的单个资源实体。 CollectionModel<> 也允许您包含链接。

不要让第一个声明溜走。 “封装集合”是什么意思？ 员工收藏？不完全的。

由于我们谈论的是 REST，它应该封装员工资源的集合。

这就是您获取所有员工，然后将它们转换为 `EntityModel<Employee>` 对象列表的原因。 （感谢 Java 8 Streams流！）

如果您重新启动应用程序并获取聚合根，您可以看到它现在的样子。

员工资源集合的 RESTful 表示

```json
{
   "_embedded" : {
      "employeeList" : [
         {
            "_links" : {
               "employees" : {
                  "href" : "http://localhost:8080/employees"
               },
               "self" : {
                  "href" : "http://localhost:8080/employees/1"
               }
            },
            "id" : 1,
            "name" : "Bilbo Baggins",
            "role" : "burglar"
         },
         {
            "_links" : {
               "employees" : {
                  "href" : "http://localhost:8080/employees"
               },
               "self" : {
                  "href" : "http://localhost:8080/employees/2"
               }
            },
            "id" : 2,
            "name" : "Frodo Baggins",
            "role" : "thief"
         }
      ]
   },
   "_links" : {
      "self" : {
         "href" : "http://localhost:8080/employees"
      }
   }
}
```

对于提供员工资源集合的聚合根，有一个顶级“self”链接。 “collection”列在“_embedded”部分下方； 这就是 HAL 表示集合的方式。

并且集合的每个单独成员都有他们的信息以及相关链接。

添加所有这些链接有什么意义？ 它使得随着时间的推移发展 REST 服务成为可能。 可以维护现有链接，而将来可以添加新链接。 新客户可以利用新链接，而旧客户可以在旧链接上维持自己的生命。 如果服务被重新定位和移动，这将特别有用。 只要保持链接结构，客户端仍然可以找到事物并与之交互。

## 简化链接创建

在前面的代码中，您是否注意到单个员工链接创建中的重复？ 为员工提供单个链接以及创建到聚合根的“Employee”链接的代码显示了两次。 如果这引起了您的关注，很好！ 有一个解决方案。

简单地说，您需要定义一个函数，将 Employee 对象转换为 `EntityModel<Employee>` 对象。 虽然您可以轻松地自己编写此方法，但在实现 Spring HATEOAS 的 RepresentationModelAssembler 接口的道路上也有好处——它将为您完成工作。

EmployeeModelAssembler.java

这个简单的接口有一个方法：toModel()。 它基于将非模型对象 (Employee) 转换为基于模型的对象 (`EntityModel<Employee>`)。

您之前在控制器中看到的所有代码都可以移到此类中。 并且通过应用 Spring Framework 的 `@Component` 注解，将在应用启动时自动创建汇编程序。

Spring HATEOAS 的所有模型的抽象基类是 RepresentationModel。 但为简单起见，我建议使用 `EntityModel<T>` 作为您的机制，以便轻松地将所有 POJO 包装为模型。
要利用此汇编器，您只需通过在构造函数中注入汇编器(assembler)来更改 EmployeeController。

将 EmployeeModelAssembler 注入控制器

```java
@RestController
class EmployeeController {

  private final EmployeeRepository repository;
  private final EmployeeModelAssembler assembler;

  EmployeeController(EmployeeRepository repository, EmployeeModelAssembler assembler) {
    this.repository = repository;
    this.assembler = assembler;
  }
  ...
}
```

从这里，您可以在单项员工方法中使用该汇编程序：

使用汇编程序获取单项资源

```java
@GetMapping("/employees/{id}")
EntityModel<Employee> one(@PathVariable Long id) {
  Employee employee = repository.findById(id) //
      .orElseThrow(() -> new EmployeeNotFoundException(id));
  return assembler.toModel(employee);
}
```

这段代码几乎是一样的，除了这里不是创建 `EntityModel<Employee>` 实例，而是将它委托给汇编器。 也许这看起来并不多。

在聚合根控制器方法中应用相同的东西更令人印象深刻：

使用汇编程序获取聚合根资源

```java
@GetMapping("/employees")
CollectionModel<EntityModel<Employee>> all() {
  List<EntityModel<Employee>> employees = repository.findAll().stream() //
      .map(assembler::toModel) //
      .collect(Collectors.toList());
  return CollectionModel.of(employees, linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
}
```

同样，代码几乎相同，但是您可以将所有 `EntityModel<Employee>` 创建逻辑替换为 `map(assembler::toModel)`。 由于 Java 8 方法引用，插入它并简化您的控制器非常容易。

> Spring HATEOAS 的一个关键设计目标是让 The Right Thing™ 变得更容易。 在这种情况下：将超媒体添加到您的服务中，而无需对事物进行硬编码。

在这个阶段，您已经创建了一个实际生成超媒体驱动内容的 Spring MVC REST 控制器！ 不讲 HAL 的客户端可以在使用纯数据时忽略额外的位。 使用 HAL 的客户可以浏览您授权的 API。

但这并不是使用 Spring 构建真正的 RESTful 服务所需的唯一事情。

## 不断发展的 REST API

通过一个额外的库和几行额外的代码，您已将超媒体添加到您的应用程序中。但这并不是使您的服务成为 RESTful 所需的唯一事情。 REST 的一个重要方面是它既不是技术堆栈也不是单一标准。

REST 是架构约束的集合，采用这些约束会使您的应用程序更具弹性。弹性的一个关键因素是，当您升级服务时，您的客户不会遭受停机时间的困扰。

在“过去”的日子里，升级因破坏客户端而臭名昭著。换句话说，升级到服务器需要更新客户端。在当今时代，花费数小时甚至数分钟进行升级的停机时间可能会造成数百万美元的收入损失。

有些公司要求您向管理层提出一个计划，以尽量减少停机时间。过去，您可以在周日凌晨 2:00 进行升级，因为此时负载最低。但在当今基于互联网的电子商务中，国际客户在其他时区，这样的策略并不那么有效。

[基于 SOAP 的服务](https://www.tutorialspoint.com/soap/what_is_soap.htm)和[基于 CORBA 的服务](https://www.corba.org/faq.htm)非常脆弱。很难推出可以同时支持新旧客户端的服务器。使用基于 REST 的实践，这要容易得多。特别是使用 Spring 堆栈。

### 支持对 API 的更改

想象一下这个设计问题：您已经推出了一个带有这个基于员工的记录的系统。该系统大受欢迎。你已经把你的系统卖给了无数的企业。突然，需要将员工的姓名拆分为名字和姓氏。

哦哦。没想到。

在您打开 Employee 类并将单个字段名称替换为 firstName 和 lastName 之前，请停下来想一想。这会破坏任何客户吗？升级它们需要多长时间。您甚至控制所有访问您服务的客户端吗？

停机时间 = 损失金钱。管理层准备好了吗？

有一个比 REST 早几年的旧策略。

永远不要删除数据库中的列。
— 未知

您始终可以将列（字段）添加到数据库表中。但不要带走一个。 RESTful 服务中的原理是相同的。

将新字段添加到您的 JSON 表示中，但不要带走任何字段。像这样：

支持多个客户端的 JSON

```json
{
  "id": 1,
  "firstName": "Bilbo",
  "lastName": "Baggins",
  "role": "burglar",
  "name": "Bilbo Baggins",
  "_links": {
    "self": {
      "href": "http://localhost:8080/employees/1"
    },
    "employees": {
      "href": "http://localhost:8080/employees"
    }
  }
}
```

请注意这种格式如何显示名字、姓氏和姓名？ 虽然它包含重复信息，但其目的是同时支持新老客户。 这意味着您可以升级服务器，而无需同时升级客户端。 一个可以减少停机时间的好举措。

您不仅应该以“旧方式”和“新方式”显示这些信息，还应该以两种方式处理传入的数据。

如何？ 简单的。 像这样：

处理“旧”和“新”客户的员工记录 更新 Employee.java

这个类与以前版本的 Employee 非常相似。 让我们回顾一下变化：

- 字段名称已替换为 firstName 和 lastName。
- 为旧名称属性定义了一个“虚拟”getter，getName()。 它使用 firstName 和 lastName 字段来生成一个值。
- 还定义了旧名称属性的“虚拟”设置器 setName()。 它解析传入的字符串并将其存储到适当的字段中。

当然，并非对 API 的每一次更改都像拆分字符串或合并两个字符串一样简单。 但是对于大多数场景来说，想出一组转换肯定不是不可能的，对吧？

不要忘记更改预加载数据库的方式（在 LoadDatabase 中）以使用这个新的构造函数。

#### 适当的反应

朝着正确方向迈出的另一个步骤是确保您的每个 REST 方法都返回正确的响应。 像这样更新 POST 方法：

处理“旧”和“新”客户端请求的 POST

```java
// old
@PostMapping("/employees")
Employee newEmployee(@RequestBody Employee newEmployee) {
    // These two facts combined together can lead to malicious attack: if a
    // persistent object is used as an argument of a method annotated with
    // @RequestMapping, it’s possible from a specially crafted user input, to change
    // the content of unexpected fields into the database.
    // do the mapping between "newEmployee" and "persistentEmployee"
    Employee persistentEmployee = new Employee();
    BeanUtils.copyProperties(newEmployee, persistentEmployee);
    return repository.save(persistentEmployee);
}
// new
@PostMapping("/employees")
ResponseEntity<?> newEmployee(@RequestBody Employee newEmployee) {
  EntityModel<Employee> entityModel = assembler.toModel(repository.save(newEmployee));
  return ResponseEntity //
      .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
      .body(entityModel);
}
```

- 新的 Employee 对象像以前一样被保存。 但是生成的对象是使用 EmployeeModelAssembler 包装的。
- Spring MVC 的 ResponseEntity 用于创建 HTTP 201 Created 状态消息。 这种类型的响应通常包含一个 Location 响应头，我们使用从模型的自相关链接派生的 URI。
- 此外，返回已保存对象的基于模型的版本。

通过这些调整，您可以使用相同的端点来创建新的员工资源，并使用旧名称字段：
`$ curl -v -X POST localhost:8080/employees -H 'Content-Type:application/json' -d '{"name": "Samwise Gamgee", "role": "gardener"}' | json_pp`

输出如下所示：

```log
> POST /employees HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.79.1
> Accept: */*
> Content-Type:application/json
> Content-Length: 46
> 
} [46 bytes data]
* Mark bundle as not supporting multiuse
< HTTP/1.1 201 
< Location: http://localhost:8080/employees/4
< Content-Type: application/hal+json
< Transfer-Encoding: chunked
< Date: Mon, 29 Aug 2022 13:59:40 GMT
< 
{ [216 bytes data]
100   256    0   210  100    46   1719    376 --:--:-- --:--:-- --:--:-- 12800
* Connection #0 to host localhost left intact
{
   "_links" : {
      "employees" : {
         "href" : "http://localhost:8080/employees"
      },
      "self" : {
         "href" : "http://localhost:8080/employees/4"
      }
   },
   "firstName" : "Samwise",
   "id" : 4,
   "lastName" : "Gamgee",
   "name" : "Samwise Gamgee",
   "role" : "gardener"
}
```

这不仅在 HAL 中呈现结果对象（名称和名字/姓氏），而且 Location 标头中也填充了 http://localhost:8080/employees/3。 超媒体驱动的客户端可以选择“冲浪”到这个新资源并继续与之交互。

PUT 控制器方法需要类似的调整：

为不同的客户端处理 PUT

```java
// old
    @PutMapping("/employees/{id}")
    Employee replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {
        return repository.findById(id)
                .map(employee -> {
                    employee.setName(newEmployee.getName());
                    employee.setRole(newEmployee.getRole());
                    return repository.save(employee);
                })
                .orElseGet(() -> {
                    newEmployee.setId(id);
                    return repository.save(newEmployee);
                });
    }
// new
@PutMapping("/employees/{id}")
ResponseEntity<?> replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {
  Employee updatedEmployee = repository.findById(id) //
      .map(employee -> {
        employee.setName(newEmployee.getName());
        employee.setRole(newEmployee.getRole());
        return repository.save(employee);
      }) //
      .orElseGet(() -> {
        newEmployee.setId(id);
        return repository.save(newEmployee);
      });
  EntityModel<Employee> entityModel = assembler.toModel(updatedEmployee);
  return ResponseEntity //
      .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
      .body(entityModel);
}
```

然后使用 `EmployeeModelAssembler` 将通过 `save()` 操作构建的 `Employee` 对象包装到 `EntityModel<Employee>` 对象中。 使用 `getRequiredLink()` 方法，您可以使用 `SELF` rel 检索 `EmployeeModelAssembler` 创建的链接。 此方法返回一个 `Link`，必须使用 `toUri` 方法将其转换为 `URI`。

由于我们想要一个比 200 OK 更详细的 HTTP 响应代码，我们将使用 Spring MVC 的 `ResponseEntity` 包装器。 它有一个方便的静态方法 `created()` ，我们可以在其中插入资源的 `URI`。 `HTTP 201 Created` 是否具有正确的语义值得商榷，因为我们不一定要“创建creating”新资源。 但它预装了一个 `Location` 响应头，所以用它运行。

`$ curl -v -X PUT localhost:8080/employees/3 -H 'Content-Type:application/json' -d '{"name": "Samwise Gamgee", "role": "ring bearer"}'  | json_pp`

该员工资源现已更新，并且位置 URI 已发回。 最后，适当地更新 DELETE 操作：

处理 DELETE 请求

```java
// old
    @DeleteMapping("/employees/{id}")
    void deleteEmployee(@PathVariable Long id) {
        repository.deleteById(id);
    }
// new
@DeleteMapping("/employees/{id}")
ResponseEntity<?> deleteEmployee(@PathVariable Long id) {
  repository.deleteById(id);
  return ResponseEntity.noContent().build();
}
```

这将返回 HTTP 204 No Content 响应。

`$ curl -v -X DELETE localhost:8080/employees/1`

```log
*   Trying 127.0.0.1:8080...
* Connected to localhost (127.0.0.1) port 8080 (#0)
> DELETE /employees/1 HTTP/1.1
> Host: localhost:8080
> User-Agent: curl/7.79.1
> Accept: */*
> 
* Mark bundle as not supporting multiuse
< HTTP/1.1 204 
< Date: Mon, 29 Aug 2022 14:13:06 GMT
< 
* Connection #0 to host localhost left intact
```

对 Employee 类中的字段进行更改需要与您的数据库团队协调，以便他们可以正确地将现有内容迁移到新列中。
您现在已准备好进行升级，不会干扰现有客户端，而新客户端可以利用这些增强功能！

顺便说一句，您是否担心通过网络发送太多信息？ 在某些每个字节都很重要的系统中，API 的发展可能需要退居二线。 但在测量之前不要追求这种过早的优化。

## 在 REST API 中构建链接

## 问题

**[WARN]** `Persistent entities should not be used as arguments of "@RequestMapping" methods (java:S4684)`
`Replace this persistent entity with a simple POJO or DTO object.`
**原因**：一方面，Spring MVC 自动将请求参数绑定到声明为使用 @RequestMapping 注释的方法的参数的 bean。由于这种自动绑定功能，可以在 @RequestMapping 注释方法的参数上提供一些意想不到的字段。
另一方面，持久对象（@Entity 或 @Document）链接到底层数据库，并由持久性框架自动更新，例如 Hibernate、JPA 或 Spring Data MongoDB。
这两个事实结合在一起可能会导致恶意攻击：如果将持久对象用作带有 @RequestMapping 注释的方法的参数，则有可能从特制的用户输入中将意外字段的内容更改到数据库中。
因此，应避免使用 @Entity 或 @Document 对象作为带有 @RequestMapping 注释的方法的参数。
除了@RequestMapping，这条规则还考虑了Spring Framework 4.3中引入的注解：@GetMapping、@PostMapping、@PutMapping、@DeleteMapping、@PatchMapping。
**解决**：用一个简单的 POJO 或 DTO 对象替换这个持久化实体，而不是`do the mapping between "sendINObject" and "persistentObject"`。

**[WARN]** The method methodOn(Class`<EmployeeController>`) is undefined for the type EmployeeController。

**解决**：An import needs to be settled and it has been update: `import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;`