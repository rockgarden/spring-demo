# EhCache cache cluster

引自 <https://blog.didispace.com/spring-boot-learning-21-5-3/>

组建进程内缓存EnCache的集群以及配置配置他们的同步策略。

组建集群的过程，务必采用多机的方式调试，避免不必要的错误发生。

> 若无资源可使用同主机不同端口来进行测试。

## 编码

创建User模型

- 缓存对象实现Serializable接口：避免后续缓存集群通过过程中，传输User对象时导致序列化与反序列化的异常。

创建User存储库

- 引入缓存注解 `@CacheConfig(cacheNames = "users")`
- 所有变更操作，如 save 增加@CachePut注解，让更新操作完成之后将结果再put到缓存中

创建测试类

- 注入 CacheManager 观察程序使用的缓存管理类

使用 ehcache 缓存管理

- 在 pom.xml 中引入ehcache依赖
- 在 resources 目录下创建，根据不同实例在网络相关配置，生成不同ehcache.xml

配置说明：

- `cache` 标签中定义名为users的缓存，这里我们增加了一个子标签定义 `cacheEventListenerFactory` ，这个标签主要用来定义缓存事件监听的处理策略，它有以下这些参数用来设置缓存的同步策略：
  - replicatePuts：当一个新元素增加到缓存中的时候是否要复制到其他的peers。默认是true，结合 UserRepository 中 @CachePut 的注解，可保证数据在更新之后复制到其他节点，防止缓存脏数据。
  - replicateUpdates：当一个已经在缓存中存在的元素被覆盖时是否要进行复制。默认是true。
  - replicateRemovals：当元素移除的时候是否进行复制。默认是true。
  - replicateAsynchronously：复制方式是异步的指定为true时，还是同步的，指定为false时。默认是true。
  - replicatePutsViaCopy：当一个新增元素被拷贝到其他的cache中时是否进行复制指定为true时为复制，默认是true。
  - replicateUpdatesViaCopy：当一个元素被拷贝到其他的cache中时是否进行复制指定为true时为复制，默认是true。
- 新增了一个 `cacheManagerPeerProviderFactory` 标签的配置，用来指定组建的集群信息和要同步的缓存信息，其中：
  - hostName：是当前实例的主机名
  - port：当前实例用来同步缓存的端口号
  - socketTimeoutMillis：同步缓存的Socket超时时间
  - peerDiscovery：集群节点的发现模式，有手工与自动两种，这里采用了手工指定的方式
  - rmiUrls：当peerDiscovery设置为manual的时候，用来指定需要同步的缓存节点，如果存在多个用`|`连接

创建控制器 MainController

- 用来验证缓存的同步效果
  - 调用实例1的/create接口，创建一条数据
  - 调用实例1的/find接口，实例1缓存User，同时同步缓存信息给实例2，在实例1中会存在SQL查询语句
  - 调用实例2的/find接口，由于缓存集群同步了User的信息，所以在实例2中的这次查询也不会出现SQL语句

## 打包部署与启动

由于缓存配置内容存在一定差异，所以在指定节点的模式下，需要通过启动参数来控制读取不同的配置文件。

比如这样：

```xml
-Dspring.cache.ehcache.config=classpath:ehcache-1.xml
-Dspring.cache.ehcache.config=classpath:ehcache-2.xml
```

## 问题

??[Error: Java RMI:rmi Connection refused to host: 127.0.0.1](https://blog.csdn.net/chenchaofuck1/article/details/51558995)

异常主要根源是spring实现中，server端使用了主机名，而linux在解析主机名时使用了与windows不同的逻辑。  
在server端返回的绑定对象中采用的是server主机名（hostname），客户端程序向服务端请求一个对象的时候，返回的stub对象里面包含了服务器的hostname，客户端的后续操作根据这个hostname来连接服务器端；在服务器端bash中打入指令：`hostname -i` ，如果返回的是127.0.0.1，客户端会抛异常了。

> 注意：Spring RMI 会尽最大努力获取完全限定的主机名。 如果无法确定，它将回退并使用 IP 地址。 根据您的网络配置，在某些情况下，它会将 IP 解析为环回(loopback)地址。 为确保 RMI 将使用绑定到正确网络接口的主机名，您应该将 java.rmi.server.hostname 属性传递给将使用“-D”JVM 参数导出注册表和/或服务的 JVM。 例如：-Djava.rmi.server.hostname=myserver.com。

解决1: 修改/etc/hosts，将 `127.0.0.1       hostxxxxx` 中的127.0.0.1改成真实的，可供其他机器连接的ip；这样客户端就能得到真实的ip了。  

解决2: 在rmi服务器端程序启动脚本中显式指定hostname

```bash
hostname=`hostname`   
java -cp $CLASSPATH -Djava.rmi.server.codebase=$codebase -Djava.security.policy=$PROJECT_HOME/se_server/conf/se_server.policy -  
Djava.rmi.server.hostname=$hostname com.abc.server.StartServer > $PROJECT_HOME/se_server/logs/init.log 2>&1 & 
```

## 参考

[EhCache 分布式缓存/缓存集群](https://www.cnblogs.com/hoojo/archive/2012/07/19/2599534.html)