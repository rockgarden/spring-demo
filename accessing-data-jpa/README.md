# Spring Data JPA

<https://github.com/spring-guides/gs-accessing-data-jpa>

本指南将引导您完成构建应用程序的过程，该应用程序使用 Spring Data JPA 在关系数据库中存储和检索数据。

您将构建一个在基于内存的数据库中存储 `Customer` POJOs (Plain Old Java Objects) 的应用程序。

## 概念

通过整合Hibernate，我们能够以操作Java实体的方式来完成对数据的操作，通过框架的帮助，对Java实体的变更最终将自动地映射到数据库表中。

在Hibernate的帮助下，Java实体映射到数据库表数据完成之后，再进一步解决抽象各个Java实体基本的“增删改查”操作，我们通常会以泛型的方式封装一个模板Dao来进行抽象简化，但是这样依然不是很方便，我们需要针对每个实体编写一个继承自泛型模板Dao的接口，再编写该接口的实现。虽然一些基础的数据访问已经可以得到很好的复用，但是在代码结构上针对每个实体都会有一堆Dao的接口和实现。

由于模板Dao的实现，使得这些具体实体的Dao层已经变的非常“薄”，有一些具体实体的Dao实现可能完全就是对模板Dao的简单代理，并且往往这样的实现类可能会出现在很多实体上。Spring Data JPA的出现正可以让这样一个已经很“薄”的数据访问层变成只是一层接口的编写方式。

## 构建

定义一个简单的实体

在此示例中，您存储 Customer 对象，每个对象都被注释为 JPA 实体。 以下清单显示了 Customer 类（在 src/main/java/com/example/accessingdatajpa/Customer.java 中）。

在这里，您有一个具有三个属性的 Customer 类：id、firstName 和 lastName。您还有两个构造函数。默认构造函数的存在只是为了 JPA。您不直接使用它，因此它被指定为受保护的。另一个构造函数是用于创建要保存到数据库的 Customer 实例的构造函数。

Customer 类使用 @Entity 注解，表明它是一个 JPA 实体。 （因为不存在@Table 注解，所以假设该实体映射到名为 Customer 的表。）

Customer 对象的 id 属性使用 @Id 注释，以便 JPA 将其识别为对象的 ID。 id 属性还使用 @GeneratedValue 进行注释，以指示应自动生成 ID。

其他两个属性 firstName 和 lastName 没有注释。假设它们被映射到与属性本身共享相同名称的列。

方便的 toString() 方法打印出客户的属性。

创建简单查询

Spring Data JPA 专注于使用 JPA 将数据存储在关系数据库中。它最引人注目的功能是能够在运行时从存储库接口自动创建存储库实现。

要查看其工作原理，请创建一个与客户实体一起使用的存储库接口，如下所示（在 src/main/java/com/example/accessingdatajpa/CustomerRepository.java 中）

CustomerRepository 扩展了 CrudRepository 接口。 它使用的实体类型和 ID，Customer 和 Long，在 CrudRepository 的通用参数中指定。 通过扩展 CrudRepository，CustomerRepository 继承了几种处理客户持久性的方法，包括保存、删除和查找客户实体的方法。

Spring Data JPA 还允许您通过声明方法签名来定义其他查询方法。 例如，CustomerRepository 包含 findByLastName() 方法。

在典型的 Java 应用程序中，您可能希望编写一个实现 CustomerRepository 的类。 然而，这正是 Spring Data JPA 如此强大的原因：您无需编写存储库接口的实现。 Spring Data JPA 在您运行应用程序时创建一个实现。

创建应用程序类

现在您需要修改 Initializr 为您创建的简单类。要获得输出（在本例中为控制台），您需要设置一个记录器。然后您需要设置一些数据并使用它来生成输出。以下清单显示了完成的 AccessingDataJpaApplication 类（在 src/main/java/com/example/accessingdatajpa/AccessingDataJpaApplication.java。

AccessingDataJpaApplication 类包含一个 demo() 方法，该方法使 CustomerRepository 通过一些测试。首先，它从 Spring 应用程序上下文中获取 CustomerRepository。然后它保存了一些 Customer 对象，演示了 save() 方法并设置了一些要使用的数据。接下来，它调用 findAll() 从数据库中获取所有 Customer 对象。然后它调用 findById() 通过其 ID 获取单个客户。最后，它调用 findByLastName() 来查找姓氏为“Bauer”的所有客户。 demo() 方法返回一个 CommandLineRunner bean，它会在应用程序启动时自动运行代码。

> 默认情况下，Spring Boot 启用 JPA 存储库支持并在 @SpringBootApplication 所在的包（及其子包）中查找。如果您的配置具有位于不可见包中的 JPA 存储库接口定义，则可以使用 @EnableJpaRepositories 及其类型安全的 basePackageClasses=MyRepository.class 参数指出备用包。

构建一个可执行的 JAR

```bash
./gradlew bootRun
./gradlew build
java -jar build/libs/gs-accessing-data-jpa-0.1.0.jar

./mvnw spring-boot:run
./mvnw clean package
java -jar target/gs-accessing-data-jpa-0.1.0.jar
```

运行应用程序时，您应该会看到类似于以下内容的输出：

```console
== Customers found with findAll():
Customer[id=1, firstName='Jack', lastName='Bauer']
Customer[id=2, firstName='Chloe', lastName='O'Brian']
Customer[id=3, firstName='Kim', lastName='Bauer']
Customer[id=4, firstName='David', lastName='Palmer']
Customer[id=5, firstName='Michelle', lastName='Dessler']

== Customer found with findById(1L):
Customer[id=1, firstName='Jack', lastName='Bauer']

== Customer found with findByLastName('Bauer'):
Customer[id=1, firstName='Jack', lastName='Bauer']
Customer[id=3, firstName='Kim', lastName='Bauer']
```
