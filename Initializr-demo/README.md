# 快速入门

引用 <http://blog.didispace.com/spring-boot-learning-2x/>

Spring官方提供了[Spring Initializr](https://start.spring.io/)工具来创建Spring Boot应用。

Spring Boot的基础结构

- src/main/java 程序入口 如InitializrDemoApplication
- src/main/resources 配置文件 如 application.properties
- src/test 测试入口 如 InitializrDemoApplicationTests
- 其中 XxxApplication 和 XxxApplicationTests 类都可以直接运行来启动当前创建的项目，由于目前该项目未配合任何数据访问或Web模块，程序会在加载完Spring之后结束运行。

## 工程结构

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

### 非典型结构下的初始化

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

## 配置基础

Spring Boot的默认配置文件位置为： src/main/resources/application.properties ；关于Spring Boot应用的配置内容都可以集中在该文件中了，根据引入的Starter模块，可以定义诸如：容器端口名、数据库链接信息、日志级别等各种配置信息。

如下所示：

```xml
# 指定服务端口
server.port=8888
# 指定应用名（该在Spring Cloud应用中会被注册为服务名）
spring.application.name=hello

book.name=SpringCloudInAction
book.author=ZhaiYongchao
```

### YAML 格式

Spring Boot 支持采用 YAML 格式的配置项目，YAML 利用阶梯化缩进的方式，其结构显得更为清晰易读，同时配置内容的字符量也得到显著的减少。

更重要的是，YAML还可以在一个单个文件中通过使用spring.profiles属性来定义多个不同的环境配置。

例如下面的内容，在指定为test环境时，server.port将使用8882端口；而在prod环境，server.port将使用8883端口；如果没有指定环境，server.port将使用8881端口。

```yaml
# default 
server:
  port: 8881
---
spring:
  profiles: test
server:
  port: 8882
---
spring:
  profiles: prod
server:
  port: 8883
```

> 注意：YAML 不支持 @PropertySource注解来加载配置，properties 支持。

#### 2.4版本

需要将spring.profiles配置用spring.config.activate.on-profile替代:

```yaml
# default 
server:
  port: 8881
---
spring:
  config:
    activate:
      on-profile: "dev"
server:
  port: 8882
```

### 指定环境启动

应用启动的时候，我们要加载不同的环境配置的参数不变，依然采用spring.profiles.active参数，对应值采用spring.config.activate.on-profile定义的标识名称。比如下面的命令就能激活dev环境的配置。
`java -jar myapp.jar -Dspring.profiles.active=dev`
日志输出：
`2020-12-16 16:34:20.614  INFO 5951 --- [           main] c.d.chapter12.Chapter12Application       : The following profiles are active: dev`

也可以将spring.profiles.active写入yaml配置中，这样的作用就可以指定默认使用某一个环境的配置，通常我们可以设置成开发环境，这样有利于我们平时的开发调试，而真正部署到其他环境的时候则多以命令参数激活为主。

```yaml
# default 
spring:
  profiles:
    active: "dev"

---

spring:
  config:
    activate:
      on-profile: "dev"

name: dev.didispace.com

---

spring:
  config:
    activate:
      on-profile: "test"

name: test.didispace.com

```

### 自定义参数

可以在配置文件中定义一些我们需要的自定义属性，如全局性参数，在代码中通过@Value注解来加载。

```yaml
book.name=Spring Boot Guide
```

```java
@Component
public class Book {
    @Value("${book.name}")
    private String name;
}
```

@Value注解加载属性值的时候可以支持两种表达式来进行配置：

- PlaceHolder方式，格式为 ${...}，大括号内为PlaceHolder
- 使用SpEL表达式（Spring Expression Language）， 格式为 #{...}，大括号内为SpEL表达式

在application.properties中的各个参数之间，我们也可以直接通过使用PlaceHolder的方式来进行引用，就像下面的设置：

```yaml
book.name=Spring Boot Guide
book.author=coder
book.desc=${book.author} is writing《${book.name}》
```

### 随机数

在一些特殊情况下，有些参数我们希望它每次加载的时候不是一个固定的值，比如：密钥、服务端口等。

在Spring Boot的属性配置文件中，我们可以通过使用${random}配置来产生随机的int值、long值或者string字符串，这样我们就可以容易的通过配置来属性的随机生成，而不是在程序中通过编码来实现这些逻辑。

```xml
# 随机字符串
blog.value=${random.value}
# 随机int
blog.number=${random.int}
# 随机long
blog.bignumber=${random.long}
# 10以内的随机数
blog.test1=${random.int(10)}
# 10-20的随机数
blog.test2=${random.int[10,20]}
```

该配置方式可以用于设置应用端口等场景，避免在本地调试时出现端口冲突的麻烦。

### 命令行参数

使用命令java -jar命令来启动Spring Boot应用。可以在命令行中来指定应用的参数，比如：java -jar xxx.jar --server.port=8888，直接以命令行的方式，来设置server.port属性。

在命令行方式启动Spring Boot应用时，连续的两个减号--就是对application.properties中的属性值进行赋值的标识。所以，java -jar xxx.jar --server.port=8888命令，等价于我们在application.properties中添加属性server.port=8888。

通过命令行来修改属性值是Spring Boot非常重要的一个特性，通过此特性，理论上已经使得我们应用的属性在启动前是可变的，所以其中端口号也好、数据库连接也好，都是可以在应用启动时发生改变，而不同于以往的Spring应用通过Maven的Profile在编译器进行不同环境的构建。其最大的区别就是，Spring Boot的这种方式，可以让应用程序的打包内容，贯穿开发、测试以及线上部署，而Maven不同Profile的方案每个环境所构建的包，其内容本质上是不同的。

## 多环境配置

我们在开发任何应用的时候，通常同一套程序会被应用和安装到几个不同的环境，比如：开发、测试、生产等。其中每个环境的数据库地址、服务器端口等等配置都会不同，如果在为不同环境打包时都要频繁修改配置文件的话，那必将是个非常繁琐且容易发生错误的事。

对于多环境的配置，各种项目构建工具或是框架的基本思路是一致的，通过配置多份不同环境的配置文件，再通过打包命令指定需要打包的内容之后进行区分打包，Spring Boot也不例外。

在Spring Boot中多环境配置文件名需要满足application-{profile}.properties的格式，其中{profile}对应你的环境标识，比如：

- application-dev.properties：开发环境
- application-test.properties：测试环境
- application-prod.properties：生产环境

至于哪个具体的配置文件会被加载，需要在application.properties文件中通过spring.profiles.active属性来设置，其值对应配置文件中的{profile}值。如：spring.profiles.active=test就会加载application-test.properties配置文件内容。

在这三个文件均都设置不同的server.port属性，如：dev环境设置为1111，test环境设置为2222，prod环境设置为3333。

application.properties中设置spring.profiles.active=dev，就是说默认以dev环境设置。

测试不同配置的加载

- 执行java -jar xxx.jar，可以观察到服务端口被设置为1111，也就是默认的开发环境（dev）
- 执行java -jar xxx.jar --spring.profiles.active=test，可以观察到服务端口被设置为2222，也就是测试环境的配置（test）

多环境的配置思路：

- application.properties中配置通用内容，并设置spring.profiles.active=dev，以开发环境为默认配置
- application-{profile}.properties中配置各个环境不同的内容
- 通过命令行方式去激活不同环境的配置

### 分组配置

spring.profiles.include对于这个配置项，是用来引入一些其他配置的配置.

当我们的应用有很多配置信息的时候，比如当用到了很多中间件MySQL、Redis、MQ等，每个中间件的配置都是一大串的，那么这个时候我们为了配置更简洁一些，可能就会对其做分组。

```yaml
spring:
  profiles:
    active: "dev"

---
spring.profiles: "dev"
spring.profiles.include: "dev-db,dev-mq"

---
spring.profiles: "dev-db"

db: dev-db.didispace.com

---
spring.profiles: "dev-mq"

mq: dev-mq.didispace.com
```

- 第一个spring.profiles.active: dev，代表默认激活dev配置
- 第二段dev配置中使用了spring.profiles.include来引入其他配置信息，这里模拟一下一个是dev的db配置，一个是dev的mq配置。在2.3和之前版本的时候，我们通常就是这样来分组配置不同中间件的。

#### 版本

在升级spring boot到2.4之后，再启动之前的应用，你会发现配置就没有生效了，这里不仅是因为spring.profiles失效的原因，即使我们将其都修改为spring.config.activate.on-profile，也依然无法激活dev-db和dev-mq的配置。因为在2.4版本之后，我们需要使用spring.profiles.group来配置了，同时组织结构也发生了变化。

```yaml
spring:
  profiles:
    active: "dev"
    group:
      "dev": "dev-db,dev-mq"
      "prod": "prod-db,prod-mq"

---
spring:
  config:
    activate:
      on-profile: "dev-db"

db: dev-db.didispace.com

---
spring:
  config:
    activate:
      on-profile: "dev-mq"

mq: dev-mq.didispace.com

---
spring:
  config:
    activate:
      on-profile: "prod-db"

db: prod-db.didispace.com

---
spring:
  config:
    activate:
      on-profile: "prod-mq"

mq: prod-mq.didispace.com
```

在2.4版本的配置中，不同环境的配置定义都在第一段默认配置中了，所有的环境定义都转移到了spring.profiles.group的key字段（上面配置了dev和prod），value字段则代表了每个环境需要加载的不同配置分组。

不同环境的配置标识都集中定义在了每个spring.config.activate.on-profile里。而这次分组的配置改变，让激活配置、环境配置集中到了默认配置里，其他的profile定义是环境+配置分组的组合内容。

## 加载顺序

在上面的例子中，我们将Spring Boot应用需要的配置内容都放在了项目工程中，虽然我们已经能够通过spring.profiles.active或是通过Maven来实现多环境的支持。但是，当我们的团队逐渐壮大，分工越来越细致之后，往往我们不需要让开发人员知道测试或是生成环境的细节，而是希望由每个环境各自的负责人（QA或是运维）来集中维护这些信息。那么如果还是以这样的方式存储配置内容，对于不同环境配置的修改就不得不去获取工程内容来修改这些配置内容，当应用非常多的时候就变得非常不方便。同时，配置内容都对开发人员可见，本身这也是一种安全隐患。对此，现在出现了很多将配置内容外部化的框架和工具，后续将要介绍的Spring Cloud Config就是其中之一，为了后续能更好的理解Spring Cloud Config的加载机制，我们需要对Spring Boot对数据文件的加载机制有一定的了解。

Spring Boot为了能够更合理的重写各属性的值，使用了下面这种较为特别的属性加载顺序：

1. 命令行中传入的参数。
2. SPRING_APPLICATION_JSON中的属性。SPRING_APPLICATION_JSON是以JSON格式配置在系统环境变量中的内容。
3. java:comp/env中的JNDI属性。
4. Java的系统属性，可以通过System.getProperties()获得的内容。
5. 操作系统的环境变量
6. 通过random.*配置的随机属性
7. 位于当前应用jar包之外，针对不同{profile}环境的配置文件内容，例如：application-{profile}.properties或是YAML定义的配置文件
8. 位于当前应用jar包之内，针对不同{profile}环境的配置文件内容，例如：application-{profile}.properties或是YAML定义的配置文件
9. 位于当前应用jar包之外的application.properties和YAML配置内容
10. 位于当前应用jar包之内的application.properties和YAML配置内容
11. 在@Configuration注解修改的类中，通过@PropertySource注解定义的属性
12. 应用默认属性，使用SpringApplication.setDefaultProperties定义的内容

优先级按上面的顺序有高到低，数字越小优先级越高。

可以看到，其中第7项和第9项都是从应用jar包之外读取配置文件，所以，实现外部化配置的原理就是从此切入，为其指定外部配置文件的加载位置来取代jar包之内的配置内容。通过这样的实现，我们的工程在配置中就变的非常干净，我们只需要在本地放置开发需要的配置即可，而其他环境的配置就可以不用关心，由其对应环境的负责人去维护即可。

## 配置文件绑定

简单类型

在Spring Boot 2.0中对配置属性加载的时候会除了像1.x版本时候那样移除特殊字符外，还会将配置均以全小写的方式进行匹配和加载。所以，下面的4种配置方式都是等价的：

```xml
spring.jpa.databaseplatform=mysql
spring.jpa.database-platform=mysql
spring.jpa.databasePlatform=mysql
spring.JPA.database_platform=mysql
```

```yaml
spring:
  jpa:
    databaseplatform: mysql
    database-platform: mysql
    databasePlatform: mysql
    database_platform: mysql
```

Tips：推荐使用全小写配合-分隔符的方式来配置，比如：spring.jpa.database-platform=mysql

List类型

```xml
# 在properties文件中使用[]来定位列表类型
spring.my-example.url[0]=http://example.com
spring.my-example.url[1]=http://spring.io
# 也支持使用逗号分割的配置方式
spring.my-example.url=http://example.com,http://spring.io
```

```yaml
# 而在yaml文件中使用-配置
spring:
  my-example:
    url:
      - http://example.com
      - http://spring.io
# 支持逗号分割的方式：
spring:
  my-example:
    url: http://example.com, http://spring.io
```

> 注意：在Spring Boot 2.0中对于List类型的配置必须是连续的，不然会抛出UnboundConfigurationPropertiesException异常，所以如下配置是不允许的：`foo[0]=a foo[2]=b`; `foo[1]` 没有配置。

Map类型

```xml
spring.my-example.foo=bar
spring.my-example.hello=world
```

```yaml
spring:
  my-example:
    foo: bar
    hello: world
# Map类型的key包含非字母数字和-的字符，需要用[]括起来
spring:
  my-example:
    '[foo.baz]': bar
```

### 环境属性绑定

简单类型

在环境变量中通过小写转换与.替换_来映射配置文件中的内容，比如：环境变量SPRING_JPA_DATABASEPLATFORM=mysql的配置会产生与在配置文件中设置spring.jpa.databaseplatform=mysql一样的效果。

List类型

```xml
# 环境变量中无法使用[和]符号，所以使用_来替代
MY_FOO_1_ = my.foo[1]
MY_FOO_1_BAR = my.foo[1].bar
MY_FOO_1_2_ = my.foo[1][2]
# 环境变量最后是以数字和下划线结尾的话，最后的下划线可以省略
MY_FOO_1 = my.foo[1]
MY_FOO_1_2 = my.foo[1][2]
```

### 系统属性绑定

简单类型

系统属性与文件配置中的类似，都以移除特殊字符并转化小写后实现绑定，比如下面的命令行参数都会实现配置spring.jpa.databaseplatform=mysql的效果：

-Dspring.jpa.database-platform=mysql
-Dspring.jpa.databasePlatform=mysql
-Dspring.JPA.database_platform=mysql

List类型

```xml
# 系统属性的绑定也与文件属性的绑定类似，通过[]来标示
-D"spring.my-example.url[0]=http://example.com"
-D"spring.my-example.url[1]=http://spring.io"
# 支持逗号分割的方式
-Dspring.my-example.url=http://example.com,http://spring.io
```

## 属性的读取

对属性绑定的内容，可以看到对于一个属性我们可以有多种不同的表达，但是如果我们要在Spring应用程序的environment中读取属性的时候，每个属性的唯一名称符合如下规则：

- 通过.分离各个元素
- 最后一个.将前缀与属性名称分开
- 必须是字母（a-z）和数字(0-9)
- 必须是小写字母
- 用连字符-来分隔单词
- 唯一允许的其他字符是[和]，用于List的索引
- 不能以数字开头

要读取配置文件中spring.jpa.database-platform的配置，写法为 `this.environment.containsProperty("spring.jpa.database-platform")` 而 `this.environment.containsProperty("spring.jpa.databasePlatform")` 则无法获取.

> 注意：使用@Value获取配置内容的时候也需要这样的特点

## 绑定API

假设在propertes配置中有这样一个配置：`com.example.foo=bar`

创建对应的配置类

```java
@Data
@ConfigurationProperties(prefix = "com.example")
public class FooProperties {
    private String foo;
}
```

通过最新的Binder就可以这样来拿配置信息

```java
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Application.class, args);

        Binder binder = Binder.get(context.getEnvironment());
        FooProperties foo = binder.bind("com.example", Bindable.of(FooProperties.class)).get();
        System.out.println(foo.getFoo());
    }
}
```

配置内容是List类型

```xml
com.example.post[0]=Why Spring Boot
com.example.post[1]=Why Spring Cloud

com.example.posts[0].title=Why Spring Boot
com.example.posts[0].content=It is perfect!
com.example.posts[1].title=Why Spring Cloud
com.example.posts[1].content=It is perfect too!
```

```java
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(Application.class, args);

        Binder binder = Binder.get(context.getEnvironment());
        List<String> post = binder.bind("com.example.post", Bindable.listOf(String.class)).get();
        System.out.println(post);
        List<PostInfo> posts = binder.bind("com.example.posts", Bindable.listOf(PostInfo.class)).get();
        System.out.println(posts);
    }
}
```

## 配置元数据

若不配置元数据, 自定义属性有一个`Cannot resolve configuration property` 警告.

自动生成

- 创建一个配置类 DemoProperties.java ，定义与自定义配置同名的属性
- 在pom.xml中添加自动生成配置元数据的依赖 spring-boot-configuration-processor
- mvn install/gradle: build
- 在工程target目录下找到元数据文件 spring-configuration-metadata.json

## 加密敏感信息

使用 <https://github.com/ulisesbocchio/jasypt-spring-boot> 插件，完成敏感配置信息的加密。

- pom.xml中引入jasypt提供的Spring Boot Starter: jasypt-spring-boot-starter, 插件配置: jasypt-maven-plugin
- 配置文件中加入加密需要使用的密码 `jasypt.encryptor.password=example`
  - 实际应用的过程中，此配置，可以通过运维人员在环境变量或启动参数中注入，而不是由开发人员在配置文件中指定。
- 配置文件中要加密的信息用 DEC() 包裹: `datasource.password=DEC(didispace.com)`
- 使用jasypt-maven-plugin插件来给DEC()包裹的内容实现批量加密: `mvn jasypt:encrypt -Djasypt.encryptor.password=example`
  - 这里`-Djasypt.encryptor.password`参数必须与配置文件中的一致，不然后面会解密失败。
- 执行之后，重新查看配置文件: 其中 ENC()跟DEC()一样都是jasypt提供的标识，分别用来标识括号内的是加密后的内容和待加密的内容。
  
  ```log
  datasource.password=ENC(/AL9nJENCYCh9Pfzdf2xLPsqOZ6HwNgQ3AnMybFAMeOM5GphZlOK6PxzozwtCm+Q)
  jasypt.encryptor.password=didispace
  ```

- 解密配置文件 `mvn jasypt:decrypt -Djasypt.encryptor.password=example`

[ERROR] `Failed to execute goal com.github.ulisesbocchio:jasypt-maven-plugin:3.0.4:encrypt (default-cli) on project Initializr-demo: Error Encrypting: Unable to read file src/main/resources/application.properties`
解决：添加 application.properties 将加密信息配置在其中。

[ERROR] `DecryptionException: Unable to decrypt`
解决：**TOTEST** <http://blog.didispace.com/jasyptspringboot-decryption-exception>

参考：**TODO**

- <https://blog.csdn.net/qq_38225558/article/details/105747906>
- <https://www.jianshu.com/p/c7125139d688>

## Demo 创建

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

### 问题

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
