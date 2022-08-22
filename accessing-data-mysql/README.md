# 使用 MySQL 访问数据

Extend <https://github.com/spring-guides/gs-accessing-data-mysql.git>

本指南将引导您完成创建连接到 MySQL 数据库的 Spring 应用程序的过程（与大多数其他指南和许多示例应用程序使用的内存中的嵌入式数据库相反）。 它使用 Spring Data JPA 来访问数据库，但这只是许多可能的选择之一（例如，您可以使用普通的 Spring JDBC）。

Hibernate是一个开放源代码的对象关系映射框架，它对JDBC进行了非常轻量级的对象封装，它将POJO与数据库表建立映射关系，是一个全自动的orm框架，hibernate可以自动生成SQL语句，自动执行，使得Java程序员可以随心所欲的使用对象编程思维来操纵数据库。 Hibernate可以应用在任何使用JDBC的场合，既可以在Java的客户端程序使用，也可以在Servlet/JSP的Web应用中使用，最具革命意义的是，Hibernate可以在应用EJB的JaveEE架构中取代CMP，完成数据持久化的重任。

在Hibernate的帮助下，Java实体映射到数据库表数据完成之后，再进一步解决抽象各个Java实体基本的“增删改查”操作，通常会以泛型的方式封装一个模板Dao来进行抽象简化；但是这样依然不是很方便，仍需要针对每个实体编写一个继承自泛型模板Dao的接口，再编写该接口的实现。虽然一些基础的数据访问已经可以得到很好的复用，但是在代码结构上针对每个实体都会有一堆Dao的接口和实现。

由于模板Dao的实现，使得这些具体实体的Dao层已经变的非常“薄”，一些具体实体的Dao实现可能完全就是对模板Dao的简单代理。

Spring Data JPA的出现可以通过编写一个继承自JpaRepository的接口就能完成数据访问。

你需要什么:

- MySQL 5.6 或更高版本。 如果您安装了 Docker，则将数据库作为容器运行可能会很有用。
- JDK 1.8 或更高版本
- Gradle 4+ 或 Maven 3.2+
- [数据库准备](../README.md#创建数据库)

## 搭建

您将创建一个 MySQL 数据库，构建一个 Spring 应用程序，并将其连接到新创建的数据库。

## 工程配置

在pom.xml中添加相关依赖，加入以下内容：

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

## 创建 application.properties 文件

Spring Boot 为您提供所有事物的默认值。 例如，默认数据库是 H2。 因此，当您想使用任何其他数据库时，您必须在 application.properties 文件中定义连接属性。

创建一个名为 src/main/resources/application.properties 的资源文件。

在这里，spring.jpa.hibernate.ddl-auto 可以是 none、update、create 或 create-drop。有关详细信息，请参阅 [Hibernate](https://docs.jboss.org/hibernate/orm/5.4/userguide/html_single/Hibernate_User_Guide.html#configurations-hbmddl) 文档。

- none：MySQL 的默认值。不会对数据库结构进行任何更改。
- update：Hibernate 根据给定的实体结构更改数据库。
- create：每次都创建数据库，但不会在关闭时删除它。
- create-drop：创建数据库并在 SessionFactory 关闭时将其删除。
- validate：每次加载hibernate时，验证创建数据库表结构，只会和数据库中的表进行比较，不会创建新表，但是会插入新值。

您必须从 create 或 update 开始，因为您还没有数据库结构。第一次运行后，您可以根据程序要求将其切换为更新或无。当您想对数据库结构进行一些更改时，请使用更新。

H2 和其他嵌入式数据库的默认设置是 create-drop。对于其他数据库，例如 MySQL，默认值为 none。

> 在您的数据库处于生产状态后，将其设置为 none，撤销连接到 Spring 应用程序的 MySQL 用户的所有权限，并只为 MySQL 用户提供 SELECT、UPDATE、INSERT 和 DELETE，这是一个很好的安全实践。您可以在本指南的末尾阅读更多相关信息。

JPA的传统配置在persistence.xml文件。

### 创建 @Entity 模型

您需要创建实体模型，见 src/main/java/com/example/accessingdatamysql/User.java。
Hibernate 自动将实体转换为表格。

`Error: org.hibernate.InstantiationException: No default constructor for entity :: com.example.accessingdatamysql.User`

原因: 封装了一个实体，却没有创建它的空构造器，或者是删除了默认构造器（空构造器）。

- 构造器的作用：定义了构建类的不同形式
- 无参的构造函数，只是为了比较好的构造类（new）。当你尝试通过一个无参的构造函数来构建（new）时，此时编译器才会报错，因为找不到这个无参的构造函数。
- 显示指定了一个带参的构造， 为了能实例这个类的对象还需要显示指定一个无参构造。若没有，编译器默认创建一个空构造。
- 构造器的命名要与该类名一致。没有返回值也不需要加void。
- 构造器只能被调用，不能被继承。
- 需要一个空的构造函数来通过持久性框架的反射来创建一个新实例。
- Hibernate通过java反射构建bean时，需要用到这个无参构造器，下面代码中可以体现：

解决: 给这个实体创建一个空构造器 `public User() {}`。

通过引入 lombok 的 @NoArgsConstructor 注解可避免冗余代码，自动生成一个无参数的构造函数。 但如果由于存在 final 字段而无法编写此类构造函数，将生成错误消息。

> 注意：在 @NoArgsConstructor 构造函数中不会检查带有 @NonNull 等约束的字段！

通过引入 lombok 的 @Data 注解可避免冗余代码；为所有字段生成 getter，一个有用的 toString 方法，以及检查所有非瞬态字段的 hashCode 和 equals 实现。 还将为所有非最终字段以及构造函数生成设置器。

### 创建存储库

您需要创建包含用户记录的存储库，在 src/main/java/com/example/accessingdatamysql/UserRepository.java。

#### Extend CrudRepository

如是基于 CrudRepository 实现 User 类则无需添加空构造器。

```java
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {

}
```

#### Extend JpaRepository

User Class 需要引入 lombok @NoArgsConstructor 注解，原因见上。

Spring 自动在同名的 bean 中实现了这个存储库接口（大小写有所变化 — 称为 userRepository）。

继承自JpaRepository，通过查看JpaRepository接口的API文档，可以看到该接口本身已经实现了创建（save）、更新（save）、删除（delete）、查询（findAll、findOne）等基本操作的函数，因此对于这些基础操作的数据访问就不需要开发者再自己定义。

实际开发中，JpaRepository接口定义的接口往往还不够或者性能不够优化，需要进一步实现更复杂一些的查询或操作。

两个示例函数：

- User findByName(String name)
- User findByNameAndAge(String name, Integer age)
- User findUser(@Param("name") String name)
它们分别实现了按name查询User实体和按name和age查询User实体，可以看到我们这里没有任何类SQL语句就完成了两个条件查询方法。这就是Spring-data-jpa的一大特性：通过解析方法名创建查询。

jpa也提供通过使用 @Query 注解来创建查询，只需要编写JPQL语句，并通过类似“:name”来映射@Param指定的参数，如findUser函数。

### 创建控制器

您需要创建一个控制器来处理对应用程序的 HTTP 请求，在 src/main/java/com/example/accessingdatamysql/MainController.java。

前面的示例为两个端点明确指定了 POST 和 GET。 默认情况下，@RequestMapping 映射所有 HTTP 操作。

### 创建应用程序类

Spring Initializr 为应用程序创建一个简单的类。在 src/main/java/com/example/accessingdatamysql/AccessingDataMysqlApplication.java。

@SpringBootApplication 是一个方便的注解，它添加了以下所有内容：

- @Configuration：将类标记为应用程序上下文的 bean 定义源。
- @EnableAutoConfiguration：告诉 Spring Boot 根据类路径设置、其他 bean 和各种属性设置开始添加 bean。例如，如果 spring-webmvc 在类路径上，则此注释将应用程序标记为 Web 应用程序并激活关键行为，例如设置 DispatcherServlet。
- @ComponentScan：告诉 Spring 在 com/example 包中查找其他组件、配置和服务，让它找到控制器。

main() 方法使用 Spring Boot 的 SpringApplication.run() 方法来启动应用程序。您是否注意到没有一行 XML？也没有 web.xml 文件。这个 Web 应用程序是 100% 纯 Java，您不必处理任何管道或基础设施的配置。

## Build an executable JAR

您还可以构建一个包含所有必要依赖项、类和资源的单个可执行 JAR 文件并运行它。 构建可执行 jar 可以在整个开发生命周期、跨不同环境等中轻松地作为应用程序交付、版本化和部署服务。

如果您使用 Gradle，则可以使用 ./gradlew bootRun 运行应用程序。 或者，您可以使用 ./gradlew build 构建 JAR 文件，然后运行 JAR 文件，如下所示：
`java -jar build/libs/gs-accessing-data-mysql-0.1.0.jar`
如果您使用 Maven，则可以使用 ./mvnw spring-boot:run 运行应用程序。 或者，您可以使用 ./mvnw clean package 构建 JAR 文件，然后运行 JAR 文件，如下所示：
`java -jar target/gs-accessing-data-mysql-0.1.0.jar`

此处描述的步骤创建了一个可运行的 JAR。 您还可以[构建经典的WAR文件](https://spring.io/guides/gs/convert-jar-to-war/)。
运行应用程序时，会显示日志记录输出。 该服务应在几秒钟内启动并运行。

## 测试应用程序

现在应用程序正在运行，您可以使用 curl 或其他类似工具对其进行测试。 您有两个可以测试的 HTTP 端点：

GET localhost:8080/demo/all：获取所有数据。 POST localhost:8080/demo/add：将一个用户添加到数据中。

以下 curl 命令添加了一个用户：
`$ curl localhost:8080/demo/add -d name=Zero -d age=6 -d email=someemail@someemailprovider.com`
`% curl localhost:8080/demo/add -d name=First -d age=8 -d email=someemail@someemailprovider.com`

答复应如下所示：`User(id=1, name=First, age=8, email=someemail@someemailprovider.com)`

以下命令显示所有用户：
`$ curl 'localhost:8080/demo/all'`
答复应如下所示：
`[{"id":1,"name":"First","email":"someemail@someemailprovider.com"}]`

以下命令删除某用户：
`% curl localhost:8080/demo/del -d name=Zero`
答复应如下所示：
`delete Zero` 或 `User Wang does not exist`

## 进行一些安全更改

当您在生产环境中时，您可能会受到 SQL 注入攻击。黑客可能会注入 DROP TABLE 或任何其他破坏性 SQL 命令。因此，作为一种安全实践，您应该在向用户公开应用程序之前对数据库进行一些更改。

以下命令撤销与 Spring 应用程序关联的用户的所有权限：
`mysql> revoke all on db_example.* from 'springuser'@'%';`

现在 Spring 应用程序无法在数据库中执行任何操作。

应用程序必须具有某些权限，因此使用以下命令授予应用程序所需的最低权限：
`mysql> grant select, insert, delete, update on db_example.* to 'springuser'@'%';`

删除所有权限并授予一些权限，为您的 Spring 应用程序提供了仅更改数据库数据而不是结构（模式 schema）所需的权限。

当您要更改数据库时：

- 重新授予权限。
- 将 spring.jpa.hibernate.ddl-auto 更改为更新。
- 重新运行您的应用程序。

然后重复此处显示的两个命令，以使您的应用程序再次安全地用于生产。更好的是，使用专用的迁移工具，例如 Flyway 或 Liquibase。

## 附

String 转 Integer：

```java
try {
 int number = Integer.parseInt(age);
   User n = new User(name, number, email);
   userRepository.save(n);
   return "Saved";
  } catch (NumberFormatException ex) {
   log.trace(ex.getMessage());
   return "Error";
  }
```
