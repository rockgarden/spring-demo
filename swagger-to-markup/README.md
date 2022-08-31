# Swagger静态文档的生成

无参考价值 Swagger2Markup 基本不更新。

## Swagger2Markup

Swagger2Markup是Github上的一个开源项目。该项目主要用来将Swagger自动生成的文档转换成几种流行的格式以便于静态部署和使用，比如：AsciiDoc、Markdown、Confluence。

项目主页：<https://github.com/Swagger2Markup/swagger2markup>

### 生成 AsciiDoc 文档

生成 AsciiDoc 文档的方式有两种：

通过Java代码来生成

第一步：编辑pom.xml增加需要使用的相关依赖和仓库

```xml
<project>
    <dependencies>
        <dependency>
            <groupId>io.github.swagger2markup</groupId>
            <artifactId>swagger2markup</artifactId>
            <version>1.3.3</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
    <repositories>
        <repository>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
            <id>jcenter-releases</id>
            <name>jcenter</name>
            <url>https://maven.aliyun.com/repository/public</url>
        </repository>
    </repositories>
</project>
```

> <http://jcenter.bintray.com> 关闭，替换为 <https://maven.aliyun.com/repository/public> ，参见 <https://developer.aliyun.com/mvn/guide>。

工具主要就临开发时使用，所以这里我们把scope设置为test，这样这个依赖就不会打包到正常运行环境中去。

第二步：编写一个单元测试用例来生成执行生成文档的代码 DemoApplicationTests.java

- MarkupLanguage.ASCIIDOC：指定了要输出的最终格式。除了ASCIIDOC之外，还有MARKDOWN和CONFLUENCE_MARKUP，分别定义了其他格式，后面会具体举例。
- from(remoteSwaggerFile：指定了生成静态部署文档的源头配置，可以是这样的URL形式，也可以是符合Swagger规范的String类型或者从文件中读取的流。如果是对当前使用的Swagger项目，我们通过使用访问本地Swagger接口的方式，如果是从外部获取的Swagger文档配置文件，就可以通过字符串或读文件的方式
- toFolder(outputDirectory)：指定最终生成文件的具体目录位置

在执行了上面的测试用例之后，我们就能在当前项目的src目录下获得如下内容：

```txt
src
--docs
----asciidoc
------generated
--------definitions.adoc
--------overview.adoc
--------paths.adoc
--------security.adoc
```

可以看到，这种方式在运行之后就生成出了4个不同的静态文件。

输出到单个文件

如果不想分割结果文件，也可以通过替换toFolder(Paths.get("src/docs/asciidoc/generated")为toFile(Paths.get("src/docs/asciidoc/generated/all"))，将转换结果输出到一个单一的文件中，这样可以最终生成html的也是单一的。

通过 Maven 插件来生成

除了通过上面编写Java代码来生成的方式之外，swagger2markup还提供了对应的Maven插件来使用。对于上面的生成方式，完全可以通过在pom.xml中增加如下插件来完成静态内容的生成。

```xml
<plugin>
    <groupId>io.github.swagger2markup</groupId>
    <artifactId>swagger2markup-maven-plugin</artifactId>
    <version>1.3.3</version>
    <configuration>
        <swaggerInput>http://localhost:8080/v2/api-docs</swaggerInput>
        <outputDir>src/docs/asciidoc/generated-by-plugin</outputDir>
        <config>
            <swagger2markup.markupLanguage>ASCIIDOC</swagger2markup.markupLanguage>
        </config>
    </configuration>
</plugin>
```

在使用插件生成前，需要先启动应用。然后执行插件，就可以在src/docs/asciidoc/generated-by-plugin目录下看到也生成了上面一样的adoc文件了。

### 生成HTML

在完成了从Swagger文档配置文件到AsciiDoc的源文件转换之后，就是如何将AsciiDoc转换成可部署的HTML内容了。这里继续在上面的工程基础上，引入一个Maven插件来完成。

```xml
<plugin>
    <groupId>org.asciidoctor</groupId>
    <artifactId>asciidoctor-maven-plugin</artifactId>
    <version>1.5.6</version>
    <configuration>
        <sourceDirectory>src/docs/asciidoc/generated</sourceDirectory>
        <outputDirectory>src/docs/asciidoc/html</outputDirectory>
        <backend>html</backend>
        <sourceHighlighter>coderay</sourceHighlighter>
        <attributes>
            <toc>left</toc>
        </attributes>
    </configuration>
</plugin>
```

通过上面的配置，执行该插件的asciidoctor:process-asciidoc命令之后，就能在src/docs/asciidoc/html目录下生成最终可用的静态部署HTML了。在完成生成之后，可以直接通过浏览器来看查看。

### Markdown 与 Confluence 的支持

要生成Markdown和Confluence的方式非常简单，与上一篇中的方法类似，只需要修改一个参数即可。

生成 Markdown 和 Confluence 文档

生成方式有一下两种：

- 通过Java代码来生成：只需要修改withMarkupLanguage属性来指定不同的格式以及toFolder属性为结果指定不同的输出目录。
  
  生成markdown的代码片段：

  ```java
  URL remoteSwaggerFile = new URL("http://localhost:8080/v2/api-docs");
  Path outputDirectory = Paths.get("src/docs/markdown/generated");
  //    输出Ascii格式
  Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder()
    .withMarkupLanguage(MarkupLanguage.MARKDOWN)
    .build();
  Swagger2MarkupConverter.from(remoteSwaggerFile)
    .withConfig(config)
    .build()
    .toFolder(outputDirectory);
  ```

  生成confluence的代码片段：
  
  ```java
  URL remoteSwaggerFile = new URL("http://localhost:8080/v2/api-docs");
  Path outputDirectory = Paths.get("src/docs/confluence/generated");
  // 输出Ascii格式
  Swagger2MarkupConfig config = new Swagger2MarkupConfigBuilder()
    .withMarkupLanguage(MarkupLanguage.CONFLUENCE_MARKUP)
    .build();
  Swagger2MarkupConverter.from(remoteSwaggerFile)
    .withConfig(config)
    .build()
    .toFolder(outputDirectory);
  ```

在执行了上面的设置内容之后，我们就能在当前项目的src目录下获得如下内容：

```txt
src
--docs
----confluence
------generated
--------definitions.txt
--------overview.txt
--------paths.txt
--------security.txt
----markdown
------generated
--------definitions.md
--------overview.md
--------paths.md
--------security.md
```

可以看到，运行之后分别在markdown和confluence目录下输出了不同格式的转换内容。如果读者想要通过插件来生成，直接参考上一节内容，只需要修改插件配置中的swagger2markup.markupLanguage即可支持输出其他格式内容。

Markdown的部署

Markdown目前在文档编写中使用非常常见，所以可用的静态部署工具也非常多，比如：Hexo、Jekyll等都可以轻松地实现静态化部署，也可以使用一些SaaS版本的文档工具，比如：语雀等。具体使用方法，这里按照这些工具的文档都非常详细，这里就不具体介绍了。

Confluence的部署

相信很多团队都使用Confluence作为文档管理系统，所以下面具体说说Confluence格式生成结果的使用。

第一步：在Confluence的新建页面的工具栏中选择{}Markup。

第二步：在弹出框的Insert选项中选择Confluence Wiki，然后将生成的txt文件中的内容，黏贴在左侧的输入框中；此时，在右侧的阅览框可以看到如下图的效果了。
