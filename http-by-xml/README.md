# XML格式请求和响应

Spring Boot中处理HTTP请求的实现是采用的Spring MVC。而在Spring MVC中有一个消息转换器这个概念，它主要负责处理各种不同格式的请求数据进行处理，并包转换成对象，以提供更好的编程体验。

在Spring MVC中定义了HttpMessageConverter接口，抽象了消息转换器对类型的判断、对读写的判断与操作，具体可见如下定义：

```java
public interface HttpMessageConverter<T> {

    boolean canRead(Class<?> clazz, @Nullable MediaType mediaType);
    boolean canWrite(Class<?> clazz, @Nullable MediaType mediaType);
    List<MediaType> getSupportedMediaTypes();
    T read(Class<? extends T> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException;
    void write(T t, @Nullable MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException;

}
```

HTTP请求的Content-Type有各种不同格式定义，如果要支持Xml格式的消息转换，就必须要使用对应的转换器。Spring MVC中默认已经有一套采用Jackson实现的转换器MappingJackson2XmlHttpMessageConverter。

第一步：引入Xml消息转换器

在Spring应用中，通过如下配置加入对Xml格式数据的消息转换实现

```java
@Configuration
public class MessageConverterConfig1 extends WebMvcConfigurerAdapter {
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder.xml();
        builder.indentOutput(true);
        converters.add(new MappingJackson2XmlHttpMessageConverter(builder.build()));
    }
}
```

在Spring Boot应用只需要加入jackson-dataformat-xml依赖，Spring Boot就会自动引入MappingJackson2XmlHttpMessageConverter的实现。

第二步：定义对象与Xml的关系

做好了基础扩展之后，下面就可以定义Xml内容对应的Java对象了，如 User.java ，其中 @Data、@NoArgsConstructor、@AllArgsConstructor是lombok简化代码的注解，主要用于生成get、set以及构造函数。@JacksonXmlRootElement、@JacksonXmlProperty注解是用来维护对象属性在xml中的对应关系。

User对象，其可以映射的Xml样例如下：

```xml
<User>
 <name>aaaa</name>
 <age>10</age>
</User>
```

第三步：创建接收xml请求的接口

完成了要转换的对象之后，可以编写一个接口来接收xml并返回xml，如 UserController.java 。

测试：POSTMAN Body 采用 xml 格式。