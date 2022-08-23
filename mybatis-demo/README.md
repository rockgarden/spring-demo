# 使用MyBatis访问MySQL

## 配置

- 整合MyBatis，在pom.xml中引入MyBatis的Starter以及MySQL Connector依赖，具体如下：

```xml
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>2.1.1</version>
</dependency>

<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
</dependency>
```

- 在application.properties中配置mysql的连接

## 编码

### 建表

创建User表

```sql
CREATE TABLE `User` (
  `id` Integer NOT NULL AUTO_INCREMENT,
  `name` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `age` Integer DEFAULT NULL,
  `email` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci
```

### 创建 @Entity 模型

创建User表的映射对象User。

### 创建User表的操作接口 UserMapper

在接口中定义两个数据操作，一个插入，二个查询，用于后续单元测试验证。

#### 返回结果绑定

对于增、删、改操作相对变化较小。而对于“查”操作，我们往往需要进行多表关联，汇总计算等操作，那么对于查询的结果往往就不再是简单的实体对象了，往往需要返回一个与数据库实体不同的包装类，那么对于这类情况，就可以通过@Results和@Result注解来进行绑定，具体如下：

```java
@Results({
    @Result(property = "name", column = "name"),
    @Result(property = "age", column = "age")
})
@Select("SELECT name, age FROM user")
List<User> findAll();
```

@Result中的property属性对应User对象中的成员名，column对应SELECT出的字段名。
在该配置中故意没有查出id属性，只对User对应中的name和age对象做了映射配置，这样可以通过单元测试 `DemoApplicationTests.testUserMapper`来验证查出的id为null，而其他属性不为null。

[mybatis注解使用](https://mybatis.org/mybatis-3/zh/java-api.html)

### 创建单元测试

DemoApplicationTests.java

- 加入 @Rollback 实现测试结束后回滚数据，保证测试单元每次运行的数据环境独立。

## 问题

ERROR: 通过 Hibernate 创建 User 表失败，主键 id 无默认值，mybatis.mapper 无法操作。
