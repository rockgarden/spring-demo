# 保护 Web 应用程序

<https://github.com/spring-guides/gs-securing-web>

本指南将引导您完成使用受 Spring Security 保护的资源创建简单 Web 应用程序的过程。

您将构建一个 Spring MVC 应用程序，该应用程序使用由固定用户列表支持的登录表单来保护页面。

环境：

- JDK 1.8 或更高版本
- Gradle 4+ 或 Maven 3.2+

## 创建不安全的 Web 应用程序

在将安全性应用到 Web 应用程序之前，您需要一个 Web 应用程序来保护。 本部分将引导您创建一个简单的 Web 应用程序。 然后，您将在下一节中使用 Spring Security 对其进行保护。

Web 应用程序包括两个简单的视图：一个主页和一个“Hello, World”页面。 主页在以下 Thymeleaf 模板中定义。

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="https://www.thymeleaf.org" xmlns:sec="https://www.thymeleaf.org/thymeleaf-extras-springsecurity3">
    <head>
        <title>Spring Security Example</title>
    </head>
    <body>
        <h1>Welcome!</h1>
        
        <p>Click <a th:href="@{/hello}">here</a> to see a greeting.</p>
    </body>
</html>
```

这个简单的视图包含一个指向 /hello 页面的链接，该页面在以下 Thymeleaf 模板中定义：

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="https://www.thymeleaf.org"
      xmlns:sec="https://www.thymeleaf.org/thymeleaf-extras-springsecurity3">
    <head>
        <title>Hello World!</title>
    </head>
    <body>
        <h1>Hello world!</h1>
    </body>
</html>
```

Web 应用程序基于 Spring MVC。 因此，您需要配置 Spring MVC 并设置视图控制器来公开这些模板。 src/main/java/com/example/securingweb/MvcConfig.java显示了一个在应用程序中配置 Spring MVC 的类。

addViewControllers() 方法（覆盖 WebMvcConfigurer 中的同名方法）添加了四个视图控制器。 两个视图控制器引用名为 home 的视图（在 home.html 中定义），另一个引用名为 hello 的视图（在 hello.html 中定义）。 第四个视图控制器引用另一个名为 login 的视图。 您将在下一节中创建该视图。

此时，您可以跳到“运行应用程序”并运行应用程序，而无需登录任何东西。

现在您有了一个不安全的 Web 应用程序，您可以为其添加安全性。

## 设置 Spring Security

假设您要防止未经授权的用户查看位于 /hello 的问候语页面。 就像现在一样，如果访问者点击主页上的链接，他们会看到没有阻止他们的障碍。 您需要添加一个障碍，强制访问者在看到该页面之前登录。

您可以通过在应用程序中配置 Spring Security 来做到这一点。 如果 Spring Security 在类路径上，Spring Boot 会自动使用“基本”身份验证保护所有 HTTP 端点。 但是，您可以进一步自定义安全设置。 您需要做的第一件事是将 Spring Security 添加到类路径中。

使用 Gradle，您需要在 build.gradle 的依赖项闭包中添加两行（一行用于应用程序，一行用于测试），如代码所示。

使用 Maven，您需要向 pom.xml 中的 `<dependencies>` 元素添加两个额外的条目（一个用于应用程序，一个用于测试），如代码所示。

安全配置（来自 src/main/java/com/example/securingweb/WebSecurityConfig.java）确保只有经过身份验证的用户才能看到秘密问候。

WebSecurityConfig 类使用 @EnableWebSecurity 注解，以启用 Spring Security 的 Web 安全支持并提供 Spring MVC 集成。它还扩展了 WebSecurityConfigurerAdapter 并覆盖了它的几个方法来设置 Web 安全配置的一些细节。

configure(HttpSecurity) 方法定义了哪些 URL 路径应该被保护，哪些不应该。具体来说，/ 和 /home 路径配置为不需要任何身份验证。所有其他路径都必须经过身份验证。

当用户成功登录时，他们将被重定向到先前请求的需要身份验证的页面。有一个自定义的 /login 页面（由 loginPage() 指定），每个人都可以查看。

userDetailsS​​ervice() 方法使用单个用户设置内存用户存储。该用户被赋予用户名user、密码password和角色USER。

现在您需要创建登录页面。登录视图已经有一个视图控制器，因此您只需要创建登录视图本身，如 src/main/resources/templates/login.html 所示。

这个 Thymeleaf 模板提供了一个表单，该表单捕获用户名和密码并将它们发布到 /login。 按照配置，Spring Security 提供了一个过滤器来拦截该请求并对用户进行身份验证。 如果用户未能通过身份验证，页面将被重定向到 /login?error，并且您的页面会显示相应的错误消息。 成功退出后，您的应用程序将发送到 /login?logout，并且您的页面会显示相应的成功消息。

最后，您需要为访问者提供一种显示当前用户名和注销的方式。 为此，请更新 hello.html 以向当前用户问好并包含一个注销表单，如 src/main/resources/templates/hello.html 所示。

我们使用 Spring Security 与 HttpServletRequest#getRemoteUser() 的集成来显示用户名。 “退出”表单将 POST 提交到 /logout。 成功注销后，它会将用户重定向到 /login?logout。

## 运行应用程序

Spring Initializr 为您创建了一个应用程序类。 在这种情况下，您不需要修改类。 src/main/java/com/example/securingweb/SecuringWebApplication.java 显示了应用程序类。

### 构建一个可执行的 JAR

您可以使用 Gradle 或 Maven 从命令行运行应用程序。您还可以构建一个包含所有必要依赖项、类和资源的单个可执行 JAR 文件并运行它。构建可执行 jar 可以在整个开发生命周期、跨不同环境等中轻松地作为应用程序交付、版本化和部署服务。

如果您使用 Gradle，则可以使用 ./gradlew bootRun 运行应用程序。或者，您可以使用 ./gradlew build 构建 JAR 文件，然后运行 ​​JAR 文件，如下所示：

`java -jar build/libs/gs-securering-web-0.1.0.jar`

如果您使用 Maven，则可以使用 ./mvnw spring-boot:run 运行应用程序。或者，您可以使用 ./mvnw clean package 构建 JAR 文件，然后运行 ​​JAR 文件，如下所示：

`java -jar target/gs-securing-web-0.1.0.jar`

此处描述的步骤创建了一个可运行的 JAR。您还可以构建经典的 WAR 文件。
应用程序启动后，将浏览器指向 <http://localhost:8080>。您应该会看到主页。

应用程序的主页
当您单击该链接时，它会尝试将您带到 /hello 的问候页面。但是，由于该页面是安全的并且您还没有登录，它会将您带到登录页面。

登录页面
> 如果您使用不安全版本跳到此处，则看不到登录页面。您应该备份并编写其余基于安全性的代码。

在登录页面，分别在用户名和密码字段中输入用户名和密码，以测试用户身份登录。提交登录表单后，您将通过身份验证，然后进入欢迎页面。

安全的问候页面
如果您单击注销按钮，您的身份验证将被撤销，您将返回登录页面，并显示一条消息，表明您已注销。
