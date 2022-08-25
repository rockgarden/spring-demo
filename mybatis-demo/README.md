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
    @Result(property = "age", column = "age"),
    @Result(property = "email", column = "email")
})
@Select("SELECT name, age, email FROM user")
List<User> findAll();
```

@Result中的property属性对应User对象中的成员名，column对应SELECT出的字段名。

> 注意：在该配置中故意没有查出id属性，只对User对应中的name、age、email对象做了映射配置，这样可以通过单元测试 `DemoApplicationTests.testUserMapper`来验证查出的id为null，而其他属性不为null。

[mybatis注解使用](https://mybatis.org/mybatis-3/zh/java-api.html)

### 创建单元测试

DemoApplicationTests.java

- 运行前设置 jpa.hibernate.ddl-auto 为 create-drop。
- 加入 @Transactional 与 @Rollback 实现测试结束后回滚数据，保证测试单元每次运行的数据环境独立。
- 若不加载 UserMapper.xml 测试 mybatis demo：
  - 恢复 UserMappeU.java 中注释掉的 sql 语句
  - 注释掉 DemoApplication 中的 @MapperScan("com.example.mybatisdemo.mapper") 注解
  - 注释掉 application.yml 中的 mapper-locations

## 问题

**ERROR**: `Cause: java.sql.SQLException: Field 'id' doesn't have a default value`

```log
org.springframework.dao.DataIntegrityViolationException: 
### Error updating database.  Cause: java.sql.SQLException: Field 'id' doesn't have a default value
### The error may exist in com/example/mybatisdemo/mapper/UserMapper.java (best guess)
### The error may involve com.example.mybatisdemo.mapper.UserMapper.insert-Inline
### The error occurred while setting parameters
### SQL: INSERT INTO USER(NAME, AGE, EMAIL) VALUES(?, ?, ?)
### Cause: java.sql.SQLException: Field 'id' doesn't have a default value
```

原因：通过 Hibernate 创建 User 表失败，由于主键 id 生成方式为 `@GeneratedValue(strategy = GenerationType.AUTO)` ，导致无法生成 id 默认值。
解决：主键 id 生成方式改为 `GenerationType.IDENTITY` 即可正常生成。

### 以 Spring MVC 模式运行 UserServiceTest.java

**ERROR**: Invalid bound statement (not found): com.example.mybatisdemo.mapper.UserMapper.insert
原因：当 MyBatisConfig 以 `<package name="com.example.mybatisdemo.mapper" />` 形式加映射文件（mapper.xml）无效导致 UserServiceTest 运行报错。
解决：TODO
暂时：在 ApplicationContext.xml 中 指定 `mapperLocations`，或 MyBatisConfig.xml 指定 `mapper resource`

**ERROR**: `org.springframework.jdbc.BadSqlGrammarException: ### Error updating database.  Cause: java.sql.SQLSyntaxErrorException: Table 'db_example.user' doesn't exist`

```log
### The error may exist in file [/Users/wangkan/git/spring_demo/mybatis-demo/target/classes/mapper/UserMapper.xml]
### The error may involve defaultParameterMap
### The error occurred while setting parameters
### SQL: INSERT INTO USER(NAME, AGE, EMAIL) VALUES(?, ?, ?)
### Cause: java.sql.SQLSyntaxErrorException: Table 'db_example.user' doesn't exist
```

原因：@Entity User 模型，未创建表，导致 UserServiceTest 运行报错。。
解决：TODO
暂时：先直接建表
