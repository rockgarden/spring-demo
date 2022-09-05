# 使用 LDAP 对用户进行身份验证

本指南将引导您完成创建应用程序并使用 [Spring Security](https://projects.spring.io/spring-security/) LDAP 模块保护它的过程。

你将建造什么

您将构建一个简单的 Web 应用程序，该应用程序由 Spring Security 的嵌入式基于 Java 的 LDAP 服务器保护。 您将使用包含一组用户的数据文件加载 LDAP 服务器。

在本例中，您使用了[基于LDAP的用户存储](https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#ldap)。

你需要什么

- JDK 1.8 或更高版本
- Gradle 4+ 或 Maven 3.2+

## 编码

### 创建一个简单的 Web 控制器

在 Spring 中，REST 端点是 Spring MVC 控制器。 以下 Spring MVC 控制器 src/main/java/com/example/authenticatingldap/HomeController.java 通过返回简单消息来处理 GET / 请求。

整个类都用@RestController 标记，这样Spring MVC 可以自动检测控制器（通过使用其内置的扫描功能）并自动配置必要的Web 路由。

@RestController 还告诉 Spring MVC 将文本直接写入 HTTP 响应正文，因为没有视图。 相反，当您访问该页面时，您会在浏览器中收到一条简单的消息（因为本指南的重点是使用 LDAP 保护该页面）。

### 创建 Application 类

src/main/java/com/example/authenticatingldap/AuthenticatingLdapApplication.java。

@SpringBootApplication 是一个方便的注解，它添加了以下所有内容：

- @Configuration：将类标记为应用程序上下文的 bean 定义源。
- @EnableAutoConfiguration：告诉 Spring Boot 根据类路径设置、其他 bean 和各种属性设置开始添加 bean。 例如，如果 spring-webmvc 在类路径上，则此注释将应用程序标记为 Web 应用程序并激活关键行为，例如设置 DispatcherServlet。
- @ComponentScan：告诉 Spring 在 com/example 包中查找其他组件、配置和服务，让它找到控制器。

main() 方法使用 Spring Boot 的 SpringApplication.run() 方法来启动应用程序。 您是否注意到没有一行 XML？ 也没有 web.xml 文件。 这个 Web 应用程序是 100% 纯 Java，您不必处理任何管道或基础设施的配置。

## 设置 Spring Security

要配置 Spring Security，首先需要在构建中添加一些额外的依赖项。

对于基于 Gradle 的构建，将以下依赖项添加到 build.gradle 文件中：

```xml
implementation("org.springframework.boot:spring-boot-starter-security")
implementation("org.springframework.ldap:spring-ldap-core")
implementation("org.springframework.security:spring-security-ldap")
implementation("com.unboundid:unboundid-ldapsdk")
```

由于 Gradle 的工件解析问题，必须引入 spring-tx。否则，Gradle 会获取一个不起作用的旧版本。

对于基于 Maven 的构建，将以下依赖项添加到 pom.xml 文件中：

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.ldap</groupId>
  <artifactId>spring-ldap-core</artifactId>
</dependency>
<dependency>
  <groupId>org.springframework.security</groupId>
  <artifactId>spring-security-ldap</artifactId>
</dependency>
<dependency>
  <groupId>com.unboundid</groupId>
  <artifactId>unboundid-ldapsdk</artifactId>
</dependency>
```

这些依赖项添加了 Spring Security 和 UnboundId，一个开源 LDAP 服务器。 有了这些依赖项，您就可以使用纯 Java 来配置您的安全策略，来自 src/main/java/com/example/authenticatingldap/WebSecurityConfig.java。

- spring-boot-starter-data-ldap 是Spring Boot封装的对LDAP自动化配置的实现，它是基于 spring-data-ldap 来对LDAP服务端进行具体操作的。
- unboundid-ldapsdk主要是为了在这里使用嵌入式的LDAP服务端来进行测试操作，所以scope设置为了test，实际应用中，通常会部署的LDAP服务器，所以不需要此项依赖。

要自定义安全设置，请使用 WebSecurityConfigurer。通过覆盖实现 WebSecurityConfigurer 接口的 WebSecurityConfigurerAdapter 的方法来完成的。

您还需要一个 LDAP 服务器。 Spring Boot 为用纯 Java 编写的嵌入式服务器提供自动配置，本指南将使用该服务器。 `ldapAuthentication()` 方法进行配置，以便将登录表单中的用户名插入 `{0}`，以便它在 LDAP 服务器中搜索 `uid={0},ou=people,dc=springframework,dc=org`。此外，`passwordCompare()` 方法配置编码器和密码属性的名称。

### 设置用户数据

LDAP 服务器可以使用 LDIF（LDAP 数据交换格式）文件来交换用户数据。 application.properties 中的 spring.ldap.embedded.ldif 属性允许 Spring Boot 拉入一个 LDIF 数据文件。这使得预加载演示数据变得容易。来自 src/main/resources/test-server.ldif 显示了一个适用于此示例的 LDIF 文件。

在 resources/application.properties 中添加嵌入式LDAP的配置。

## 测试

使用 LDIF 文件不是生产系统的标准配置。 但是，它对于测试目的或指南很有用。
如果您在 <http://localhost:8080> 访问该站点，您应该被重定向到 Spring Security 提供的登录页面。

输入用户名 ben 和密码 benspassword。 您应该在浏览器中看到以下消息： `Welcome to the home page!` 。

## 附

### LDAP简介

LDAP（轻量级目录访问协议，Lightweight Directory Access Protocol)是实现提供被称为目录服务的信息服务。目录服务是一种特殊的数据库系统，其专门针对读取，浏览和搜索操作进行了特定的优化。目录一般用来包含描述性的，基于属性的信息并支持精细复杂的过滤能力。目录一般不支持通用数据库针对大量更新操作操作需要的复杂的事务管理或回卷策略。而目录服务的更新则一般都非常简单。这种目录可以存储包括个人信息、web链结、jpeg图像等各种信息。为了访问存储在目录中的信息，就需要使用运行在TCP/IP 之上的访问协议—LDAP。

LDAP目录中的信息是是按照树型结构组织，具体信息存储在条目(entry)的数据结构中。条目相当于关系数据库中表的记录；条目是具有区别名DN （Distinguished Name）的属性（Attribute），DN是用来引用条目的，DN相当于关系数据库表中的关键字（Primary Key）。属性由类型（Type）和一个或多个值（Values）组成，相当于关系数据库中的字段（Field）由字段名和数据类型组成，只是为了方便检索的需要，LDAP中的Type可以有多个Value，而不是关系数据库中为降低数据的冗余性要求实现的各个域必须是不相关的。LDAP中条目的组织一般按照地理位置和组织关系进行组织，非常的直观。LDAP把数据存放在文件中，为提高效率可以使用基于索引的文件数据库，而不是关系数据库。类型的一个例子就是mail，其值将是一个电子邮件地址。

LDAP的信息是以树型结构存储的，在树根一般定义国家(c=CN)或域名(dc=com)，在其下则往往定义一个或多个组织 (organization)(o=Acme)或组织单元(organizational units) (ou=People)。此外，LDAP支持对条目能够和必须支持哪些属性进行控制，这是有一个特殊的称为对象类别(objectClass)的属性来实现的。该属性的值决定了该条目必须遵循的一些规则，其规定了该条目能够及至少应该包含哪些属性。例如：inetorgPerson对象类需要支持sn(surname)和cn(common name)属性，但也可以包含可选的如邮件，电话号码等属性。

LDAP简称对应

- o：organization（组织-公司）
- ou：organization unit（组织单元-部门）
- c：countryName（国家）
- dc：domainComponent（域名）
- sn：surname（姓氏）
- cn：common name（常用名称）
