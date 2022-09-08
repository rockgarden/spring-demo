# 使用 Spring MVC 提供 Web 内容

本指南将引导您完成使用 Spring 创建“Hello, World”网站的过程。

## 原理

静态资源访问

在我们开发Web应用的时候，需要引用大量的js、css、图片等静态资源。Spring Boot默认提供静态资源目录位置需置于classpath下，目录名需符合如下规则：

- /static
- /public
- /resources
- /META-INF/resources

举例：我们可以在 src/main/resources/ 目录下创建static，在该位置放置一个图片文件。启动程序后，尝试访问 <http://localhost:8080/xxx.jpg> 。如能显示图片，配置成功。

渲染Web页面

模板引擎

在动态HTML实现上Spring Boot依然可以完美胜任，并且提供了多种模板引擎的默认配置支持，所以在推荐的模板引擎下，可方便开发动态网站。

Spring Boot提供了自动化配置模块的模板引擎主要有以下几种：

- Thymeleaf
- FreeMarker
- Groovy

当你使用上述模板引擎中的任何一个，它们默认的模板配置路径为：src/main/resources/templates。当然也可以修改这个路径，具体如何修改，可在后续各模板引擎的配置属性中查询并修改。

> Spring Boot从一开始就建议使用模板引擎，避免使用JSP。同时，随着Spring Boot版本的迭代，逐步的淘汰了一些较为古老的模板引擎。

Thymeleaf

Thymeleaf是一个XML/XHTML/HTML5模板引擎，可用于Web与非Web环境中的应用开发。它是一个开源的Java库，基于Apache License 2.0许可，由Daniel Fernández创建。

Thymeleaf提供了一个用于整合Spring MVC的可选模块，在应用开发中，你可以使用Thymeleaf来完全代替JSP或其他模板引擎。Thymeleaf的主要目标在于提供一种可被浏览器正确显示的、格式良好的模板创建方式，因此也可以用作静态建模。你可以使用它创建经过验证的XML与HTML模板。相对于编写逻辑或代码，开发者只需将标签属性添加到模板中即可。接下来，这些标签属性就会在DOM（文档对象模型）上执行预先制定好的逻辑。

示例模板：

```html
<table>
  <thead>
    <tr>
      <th th:text="#{msgs.headers.name}">Name</td>
      <th th:text="#{msgs.headers.price}">Price</td>
    </tr>
  </thead>
  <tbody>
    <tr th:each="prod : ${allProducts}">
      <td th:text="${prod.name}">Oranges</td>
      <td th:text="${#numbers.formatDecimal(prod.price,1,2)}">0.99</td>
    </tr>
  </tbody>
</table>
```

Thymeleaf主要以属性的方式加入到html标签中，浏览器在解析html时，当检查到没有的属性时候会忽略，所以Thymeleaf的模板可以通过浏览器直接打开展现，这样非常有利于前后端的分离。

## 构建

您将构建一个具有静态主页的应用程序，该应用程序还将在以下位置接受 HTTP GET 请求：<http://localhost:9080/web/greeting>。

它将响应一个显示 HTML 的网页。 HTML 的正文将包含一个问候语：“Hello, World!”

您可以使用查询字符串中的可选名称参数自定义问候语。 该 URL 可能是 <http://localhost:9080/web/greeting?name=User>。

name 参数值覆盖 World 的默认值，并通过内容更改为“Hello，User！”反映在响应中。

你需要什么

- JDK 1.8 或更高版本
- Gradle 4+ 或 Maven 3.2+
- 依赖项并选择 Spring Web、Thymeleaf 和 Spring Boot DevTools。

创建 Web 控制器
在 Spring 构建网站的方法中，HTTP 请求由控制器处理。 您可以通过 @Controller 注解轻松识别控制器。 在下面的示例中，GreetingController 通过返回视图的名称（在本例中为问候语）来处理 /greeting 的 GET 请求。 视图负责呈现 HTML 内容。 来自 src/main/java/com/example/servingwebcontent/GreetingController.java：

- @GetMapping 注解确保对 /greeting 的 HTTP GET 请求映射到 greeting() 方法。
- @RequestParam 将查询字符串参数 name 的值绑定到 greeting() 方法的 name 参数中。此查询字符串参数不是必需的。如果请求中不存在，则使用 World 的 defaultValue。 name 参数的值被添加到 Model 对象中，最终使视图模板可以访问它。

方法体的实现依赖于视图技术（在本例中为 Thymeleaf）来执行 HTML 的服务器端呈现。 Thymeleaf 解析 greeting.html 模板并评估 th:text 表达式以呈现在控制器中设置的 ${name} 参数的值。见 src/main/resources/templates/greeting.html 模板。

> 确保你的类路径上有 Thymeleaf（工件坐标 artifact co-ordinates：org.springframework.boot:spring-boot-starter-thymeleaf）。

Spring Boot 开发工具

开发 Web 应用程序的一个常见功能是编写更改代码、重新启动应用程序并刷新浏览器以查看更改。 整个过程会消耗大量时间。 为了加快这个刷新周期，Spring Boot 提供了一个方便的模块，称为 [spring-boot-devtools](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#using-boot-devtools)。 Spring Boot 开发工具：

- 启用热插拔 [hot swapping](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#howto-hotswapping) 。
- 切换模板引擎以禁用缓存。
- 启用 LiveReload 以自动刷新浏览器。
- 基于开发而不是生产的其他合理默认值。

运行应用程序

Spring Initializr 为您创建了一个应用程序类。 在这种情况下，您不需要进一步修改 Spring Initializr 提供的类。 以下清单 src/main/java/com/example/servingwebcontent/ServingWebContentApplication.java 。

构建一个可执行的 JAR

如果您使用 Gradle，则可以使用 ./gradlew bootRun 运行应用程序。 或者，您可以使用 ./gradlew build 构建 JAR 文件，然后运行 JAR 文件，如下所示：

`java -jar build/libs/gs-serving-web-content-0.1.0.jar`

如果您使用 Maven，则可以使用 ./mvnw spring-boot:run 运行应用程序。 或者，您可以使用 ./mvnw clean package 构建 JAR 文件，然后运行 JAR 文件，如下所示：

`java -jar target/gs-serving-web-content-0.1.0.jar`

测试应用程序

现在网站正在运行，访问 <http://localhost:9080/web/greeting> ，您应该会看到“Hello, World!”

通过访问 <http://localhost:9080/web/greeting?name=User> 提供名称查询字符串参数。注意消息是如何从“Hello, World!”改变的。到“你好，用户！”：

此更改表明 GreetingController 中的 @RequestParam 安排按预期工作。 name 参数已被赋予 World 的默认值，但可以通过查询字符串显式覆盖它。

添加主页

静态资源，包括 HTML、JavaScript 和 CSS，可以通过将它们放到源代码中的正确位置从 Spring Boot 应用程序提供。默认情况下，Spring Boot 从 /static（或 /public）的类路径中的资源中提供静态内容。 index.html 资源是特殊的，因为如果它存在，它被用作“欢迎页面”，serving-web-content/，这意味着它作为根资源提供（即在 <http://localhost:9080/web/>）。因此，您需要创建 src/main/resources/static/index.html 。

重新启动应用程序时，您将在 <http://localhost:9080/web> 处看到 HTML。

在Spring Boot配置文件中可对Thymeleaf的默认配置进行修改：

```xml
# 开启模板缓存（默认值：true）
spring.thymeleaf.cache=true 
# Check that the template exists before rendering it.
spring.thymeleaf.check-template=true 
# 检查模板位置是否正确（默认值:true）
spring.thymeleaf.check-template-location=true
# Content-Type的值（默认值：text/html）
spring.thymeleaf.content-type=text/html
# 开启MVC Thymeleaf视图解析（默认值：true）
spring.thymeleaf.enabled=true
# 模板编码
spring.thymeleaf.encoding=UTF-8
# 要被排除在解析之外的视图名称列表，用逗号分隔
spring.thymeleaf.excluded-view-names=
# 要运用于模板之上的模板模式。可设置为 LEGACYHTML5 避免严格校验；另见StandardTemplate-ModeHandlers(默认值：HTML5)
spring.thymeleaf.mode=HTML5
# 在构建URL时添加到视图名称前的前缀（默认值：classpath:/templates/）
spring.thymeleaf.prefix=classpath:/templates/
# 在构建URL时添加到视图名称后的后缀（默认值：.html）
spring.thymeleaf.suffix=.html
# Thymeleaf模板解析器在解析器链中的顺序。默认情况下，它排第一位。顺序从1开始，只有在定义了额外的TemplateResolver Bean时才需要设置这个属性。
spring.thymeleaf.template-resolver-order=
# 可解析的视图名称列表，用逗号分隔
spring.thymeleaf.view-names=
```

Spring Boot中，默认的html页面地址为src/main/resources/templates，默认的静态资源地址为src/main/resources/static。
