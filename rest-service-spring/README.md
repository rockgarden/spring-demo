# 使用 Spring 构建 REST 服务

REST 已迅速成为在 Web 上构建 Web 服务的事实标准，因为它们易于构建且易于使用。

关于 REST 如何适应微服务世界还有一个更大的讨论，但是——对于本教程——让我们看看构建 RESTful 服务。

为什么是 REST？ REST 包含 Web 的规则，包括其架构、优势和其他一切。这并不奇怪，因为它的作者 Roy Fielding 参与了十几个管理网络运行方式的规范。

有什么好处？ Web 及其核心协议 HTTP 提供了一系列功能：

- 合适的操作 Suitable actions（GET、POST、PUT、DELETE……）
- 缓存 Caching
- 重定向和转发 Redirection and forwarding
- 安全性（加密和身份验证） Security (encryption and authentication)

这些都是构建弹性服务的关键因素。但这还不是全部。网络是由许多微小的规范构成的，因此它能够轻松发展，而不会陷入“标准战争”的泥潭。

开发人员能够利用 3rd 方工具包来实现这些不同的规范，并立即让客户端和服务器技术触手可及。

通过在 HTTP 之上构建，REST API 提供了构建方法：

- 向后兼容的 API Backwards compatible
- 可演进的 API Evolvable
- 可扩展的服务 Scaleable services
- 安全的服务 Securable services
- 一系列无状态服务到有状态服务 A spectrum of stateless to stateful services

重要的是要意识到，无论多么普遍，REST 本身并不是一种标准，而是一种方法、一种风格、一组对您的架构的约束，可以帮助您构建 Web 规模的系统。在本教程中，我们将使用 Spring 产品组合来构建 RESTful 服务，同时利用 REST 的无堆栈特性。

What’s important to realize is that REST, however ubiquitous, is not a standard, per se, but an approach, a style, a set of constraints on your architecture that can help you build web-scale systems. In this tutorial we will use the Spring portfolio to build a RESTful service while leveraging the stackless features of REST.