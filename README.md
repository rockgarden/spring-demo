# Spring Demos

## 项目列表

### caching

在 Spring 托管 bean 上启用缓存。

### caching-by-ehcache

使用 ehcache 缓存数据。

### securing-web

使用受 Spring Security 保护的资源创建简单 Web 应用程序。

## 用户指南

### 创建数据库

**所有示例通用。**

打开终端并以可以创建新用户的用户身份打开 MySQL 客户端。

例如，在 Linux 系统上，使用以下命令；

`$ sudo mysql --password`

这以 root 身份连接到 MySQL，并允许从所有主机访问用户。 这不是生产服务器的推荐方式。
要创建新数据库，请在 mysql 提示符下运行以下命令：

```sql
mysql> CREATE DATABASE db_example DEFAULT CHARACTER SET = 'utf8mb4';
mysql> CREATE USER 'springuser'@'%' IDENTIFIED WITH caching_sha2_password BY 'ThePassword';

mysql> create database db_example; -- Creates the new database
mysql> create user 'springuser'@'%' identified by 'ThePassword'; -- Creates the user

mysql> grant all on db_example.* to 'springuser'@'%'; -- Gives all privileges to the new user on the newly created database
```

### [将 Spring Boot 应用部署到 Azure](https://spring.io/guides/gs/spring-boot-for-azure/)

建议您查看 [官方 Azure 文档](https://docs.microsoft.com/java/azure/spring-framework/deploy-spring-boot-java-app-with-maven-plugin) 以获取最新说明相同的任务。

### [Spring Boot Kubernetes](https://spring.io/guides/gs/spring-boot-kubernetes/)

在 [Katacoda/springguides](https://katacoda.com/springguides) 上有一些交互式教程可以补充和扩展本指南的内容。如果您遵循这些教程，所有代码都将从您的浏览器在云中运行。或者您可以创建自己的集群并在本地安装所需的所有工具，然后从指南中复制粘贴。

[Kubernetes 上的 Spring Boot 入门](https://www.katacoda.com/springguides/scenarios/getting-started)：与本指南相同的材料，但在您的浏览器中运行。

【安装Kubernetes】（https://www.katacoda.com/springguides/scenarios/install-kubernetes）：使用Kind在本地安装Kubernetes的指南。如果您喜欢在笔记本电脑上运行教程，可以使用它在笔记本电脑上进行设置。

[Kubernetes Probes with Spring Boot](https://www.katacoda.com/springguides/scenarios/install-kubernetes)：使用 Spring Boot 进行活跃度和就绪度探测的指南。
[springguides](https://katacoda.com/springguides)
