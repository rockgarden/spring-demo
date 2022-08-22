# 使用Redis实现数据缓存

要求高一致性（任何数据变化都能及时的被查询到）的系统和应用中，使用集中式缓存。

引自 <https://blog.didispace.com/spring-boot-learning-21-5-4/>

## 搭建

[数据库准备](../README.md#创建数据库)

创建User模型

创建User存储库

- 引入缓存注解 `@CacheConfig(cacheNames = "users")`

引入 redis 缓存管理

- 在 pom.xml 中引入 spring-boot-starter-data-redis 和 commons-pool2 依赖
- 在 resources 目录下创建：ehcache.xml

配置文件 application.properties 中增加 redis 配置信息

创建测试类

## 测试

通过debug模式运行单元测试，观察此时CacheManager已经是EhCacheManager实例，说明EhCache开启成功了。或者在测试用例中加一句CacheManager的输出。

```log
2022-08-22 15:00:19.439  INFO 90335 --- [           main] c.e.c.CachingByRedisApplicationTests     : CacheManager type : class org.springframework.data.redis.cache.RedisCacheManager
Hibernate: select next_val as id_val from hibernate_sequence for update
Hibernate: update hibernate_sequence set next_val= ? where next_val=?
Hibernate: insert into user (age, name, id) values (?, ?, ?)
2022-08-22 15:00:20.470  INFO 90335 --- [           main] io.lettuce.core.EpollProvider            : Starting without optional epoll library
2022-08-22 15:00:20.486  INFO 90335 --- [           main] io.lettuce.core.KqueueProvider           : Starting without optional kqueue library
第一次查询：10
第二次查询：10
```

## TODO

验证缓存一致性？
