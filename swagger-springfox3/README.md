# SpringFox 3

<https://github.com/springfox/springfox-demos>

1. pom.xml中添加依赖：io.springfox:springfox-boot-starter:3.0.0
2. 应用主类增加注解@EnableOpenApi。
3. 配置接口说明

启动应用！访问swagger页面：<http://localhost:8080/swagger-ui/index.html>

## 附

合法E-mail地址：

1. 必须包含一个并且只有一个符号“@”
2. 第一个字符不得是“@”或者“.”
3. 不允许出现“@.”或者.@
4. 结尾不得是字符“@”或者“.”
5. 允许“@”前的字符中出现“＋”
6. 不允许“＋”在最前面，或者“＋@”

正则字符描述：

- ^ ：匹配输入的开始位置。
- \：将下一个字符标记为特殊字符或字面值。
- `*` ：匹配前一个字符零次或几次。
- `+` ：匹配前一个字符一次或多次。
- (pattern) 与模式匹配并记住匹配。
- x|y：匹配 x 或 y。
- [a-z] ：表示某个范围内的字符。与指定区间内的任何字符匹配。
- \w ：与任何单词字符匹配，包括下划线。
- $ ：匹配输入的结尾。  

**[WARN]** `Regular expressions should not overflow the stack (java:S5998)`

问题：`^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$` （）引起
解决：
[Email Validation in Java](https://www.baeldung.com/java-email-validation-regex#:~:text=Simple%20Regular%20Expression%20Validation%20The%20simplest%20regular%20expression,result%20returns%20true%2C%20otherwise%2C%20the%20result%20is%20false.)
[Java Regex Email Validation Example](https://www.javadevjournal.com/java/java-regex-email-validation/)
