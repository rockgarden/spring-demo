# RESTful and Swagger

## 构建 RESTful API与单元测试

- @Controller：修饰class，用来创建处理http请求的对象
- @RestController：Spring4之后加入的注解，原来在@Controller中返回json需要@ResponseBody来配合，如果直接用@RestController替代@Controller就不需要再配置@ResponseBody，默认返回json格式
- @RequestMapping：配置url映射。现在更多的也会直接用以Http Method直接关联的映射注解来定义，比如：GetMapping、PostMapping、DeleteMapping、PutMapping等

定义User实体

实现对User对象的操作接口

编写单元测试

- 函数不存在而报错。必须引入下面这些静态函数的引用：

  ```java
  import static org.hamcrest.Matchers.equalTo;
  import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
  import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
  import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
  ```

## 使用 Swagger2 构建API文档

<https://github.com/SpringForAll/spring-boot-starter-swagger>

- 第一步：添加 com.spring4all.swagger-spring-boot-starter依赖
- 第二步：application.properties 中配置文档相关内容

添加文档内容

在整合完Swagger之后，在 <http://localhost:8080/swagger-ui.html> 页面中可以看到，关于各个接口的描述还都是英文或遵循代码定义的名称产生的。这些内容对用户并不友好，所以我们需要自己增加一些说明来丰富文档内容。如下所示，我们通过@Api，@ApiOperation注解来给API增加说明、通过@ApiImplicitParam、@ApiModel、@ApiModelProperty注解来给参数增加说明。

API文档访问与调试

Swagger除了查看接口功能外，还提供了调试测试功能，我们可以点击上图中右侧的Model Schema（黄色区域：它指明了User的数据结构），此时Value中就有了user对象的模板，我们只需要稍适修改，点击下方“Try it out！”按钮，即可完成了一次请求调用！

此时，你也可以通过几个GET请求来验证之前的POST请求是否正确。

相比为这些接口编写文档的工作，我们增加的配置内容是非常少而且精简的，对于原有代码的侵入也在忍受范围之内。因此，在构建RESTful API的同时，加入Swagger来对API文档进行管理，是个不错的选择。

### 接口的分组

我们在Spring Boot中定义各个接口是以Controller作为第一级维度来进行组织的，Controller与具体接口之间的关系是一对多的关系。我们可以将同属一个模块的接口定义在一个Controller里。默认情况下，Swagger是以Controller为单位，对接口进行分组管理的。这个分组的元素在Swagger中称为Tag，但是这里的Tag与接口的关系并不是一对多的，它支持更丰富的多对多关系。

默认分组

首先，我们通过一个简单的例子，来看一下默认情况，Swagger是如何根据Controller来组织Tag与接口关系的。定义两个Controller，分别负责教师管理与学生管理接口，比如下面这样：

```java
@RestController
@RequestMapping(value = "/teacher")
static class TeacherController {}

@RestController
@RequestMapping(value = "/student")
static class StudentController {}
```

Swagger中这两个Controller是这样组织：

- teacher-controller
- student-controller

Swagger默认生成的Tag与Spring Boot中Controller展示的内容与位置对应。

自定义默认分组的名称

接着，我们可以再试一下，通过@Api注解来自定义Tag：

```java
@Api(tags = "教师管理")
@RestController
@RequestMapping(value = "/teacher")
static class TeacherController {}

@Api(tags = "学生管理")
@RestController
@RequestMapping(value = "/student")
static class StudentController {}
```

代码中@Api定义的tags内容替代了默认产生的teacher-controller和student-controller。

- 教师管理 controller
- 学生管理 controller

合并Controller分组

Swagger中还支持更灵活的分组！从@Api注解的属性中，tags属性其实是个数组类型：

```java
@Api(tags = {"教师管理", "教学管理"})
@RestController
@RequestMapping(value = "/teacher")
static class TeacherController {}

@Api(tags = {"学生管理", "教学管理"})
@RestController
@RequestMapping(value = "/student")
static class StudentController {}
```

更细粒度的接口分组

通过@Api可以实现将Controller中的接口合并到一个Tag中，但是如果我们希望精确到某个接口的合并呢？比如这样的需求：“教学管理”包含“教师管理”中所有接口以及“学生管理”管理中的“获取学生清单”接口（不是全部接口）。

可以通过使用@ApiOperation注解中的tags属性做更细粒度的接口分类定义，比如上面的需求就可以这样子写：

```java
@Api(tags = {"教师管理","教学管理"})
@RestController
@RequestMapping(value = "/teacher")
static class TeacherController {

    @ApiOperation(value = "xxx")
    @GetMapping("/xxx")
    public String xxx() {
        return "xxx";
    }

}

@Api(tags = {"学生管理"})
@RestController
@RequestMapping(value = "/student")
static class StudentController {

    @ApiOperation(value = "获取学生清单", tags = "教学管理")
    @GetMapping("/list")
    public String bbb() {
        return "bbb";
    }

    @ApiOperation("获取教某个学生的老师清单")
    @GetMapping("/his-teachers")
    public String ccc() {
        return "ccc";
    }

}
```

### 内容的顺序

在完成了接口分组之后，对于接口内容的展现顺序又是众多用户特别关注的点，其中主要涉及三个方面：分组的排序、接口的排序以及参数的排序。

分组的排序

关于分组排序，也就是Tag的排序。目前版本的Swagger支持并不太好，通过文档我们可以找到关于Tag排序的配置方法。

第一种：原生Swagger用户，可以通过如下方式：`-tagsSorter(swaggerProperties.getUiConfig().getTagsSorter())`

第二种：Swagger Starter用户，可以通过修改配置的方式：`swagger.ui-config.tags-sorter=alpha`

```java
public enum TagsSorter {
  ALPHA("alpha");

  private final String value;

  TagsSorter(String value) {
    this.value = value;
  }

  @JsonValue
  public String getValue() {
    return value;
  }

  public static TagsSorter of(String name) {
    for (TagsSorter tagsSorter : TagsSorter.values()) {
      if (tagsSorter.value.equals(name)) {
        return tagsSorter;
      }
    }
    return null;
  }
}
```

Swagger只提供了一个选项，就是按字母顺序排列。那么我们要如何实现排序呢？为Tag的命名做编号，见 SwaggerSpring4allApplication。

```java
@Api(tags = {"1-教师管理","3-教学管理"})
@RestController
@RequestMapping(value = "/teacher")
static class TeacherController {}

@Api(tags = {"2-学生管理"})
@RestController
@RequestMapping(value = "/student")
static class StudentController {

    @ApiOperation(value = "获取学生清单", tags = "3-教学管理")
    @GetMapping("/list")
    public String bbb() {
        return "bbb";
    }

}
```

接口的排序

在完成了分组排序问题（虽然不太优雅…）之后，在来看看同一分组内各个接口该如何实现排序。同样的，凡事先查文档，可以看到Swagger也提供了相应的配置，下面也分两种配置方式介绍：

第一种：原生Swagger用户，可以通过如下方式：operationsSorter。

第二种：Swagger Starter用户，可以通过修改配置的方式：`swagger.ui-config.operations-sorter=alpha`

这个配置提供了两个配置项：alpha和method，分别代表了按字母表排序以及按方法定义顺序排序。当我们不配置的时候，改配置默认为alpha。

参数的排序

完成了接口的排序之后，更细粒度的就是请求参数的排序了。默认情况下，Swagger对Model参数内容的展现也是按字母顺序排列的。

可以按照Model中定义的成员变量顺序来展现，那么需要我们通过@ApiModelProperty注解的position参数来实现位置的设置，比如： User.java 。

### 校验逻辑应用

Swagger自身对JSR-303有一定的支持，但是支持的并那么完善，并没有覆盖所有的注解的。

其中：目前，Swagger共支持以下几个注解：@NotNull、@Max、@Min、@Size、@Pattern。在实际开发过程中，需要分情况来处理，对于Swagger支自动生成的可以利用原生支持来产生，如果有部分字段无法产生，则可以在@ApiModelProperty注解的描述中他，添加相应的校验说明，以便于使用方查看。

包的引用关系：spring-boot-starter-web -> spring-boot-starter-validation -> org.hibernate.validator.hibernate-validator
