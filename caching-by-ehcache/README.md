# 使用 EhCache 缓存数据

引自 <http://blog.didispace.com/spring-boot-learning-21-5-2/>

## 搭建

[数据库准备](../README.md#创建数据库)

创建User模型

创建User存储库

- 引入缓存注解 `@CacheConfig(cacheNames = "users")`
- 所有变更操作，如 save 增加@CachePut注解，让更新操作完成之后将结果再put到缓存中

创建测试类

- 注入 CacheManager 观察程序使用的缓存管理类

使用 ehcache 缓存管理

- 在 pom.xml 中引入ehcache依赖
- 在 resources 目录下创建：ehcache.xml

## 测试

通过debug模式运行单元测试，观察此时CacheManager已经是EhCacheManager实例，说明EhCache开启成功了。或者在测试用例中加一句CacheManager的输出。

```log
2022-08-21 22:23:40.279  INFO 9753 --- [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 2 endpoint(s) beneath base path '/actuator'
2022-08-21 22:23:40.425  INFO 9753 --- [           main] c.e.c.CachingEhcacheApplicationTests     : Started CachingEhcacheApplicationTests in 13.52 seconds (JVM running for 16.974)
CacheManager type : class org.springframework.cache.ehcache.EhCacheCacheManager
2022-08-21 22:23:40.704  INFO 9753 --- [           main] c.e.c.CachingEhcacheApplicationTests     : CacheManager type : class org.springframework.cache.ehcache.EhCacheCacheManager
Hibernate: select next_val as id_val from hibernate_sequence for update
Hibernate: update hibernate_sequence set next_val= ? where next_val=?
Hibernate: insert into user (age, name, id) values (?, ?, ?)
Hibernate: select user0_.id as id1_0_, user0_.age as age2_0_, user0_.name as name3_0_ from user user0_ where user0_.name=?
第一次查询：10
第二次查询：10
```

## TODO

UserRepository 中重写的 User save(User user) 中 User 泛型转换不安全。
Warn: `Type safety: The return type User for save(User) from the type UserRepository needs unchecked conversion to conform to S from the type CrudRepository<T,ID>`
