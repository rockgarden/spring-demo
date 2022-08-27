package com.example.initializrdemo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "example")
public class DemoProperties {

    /**
     * 这是一个测试配置
     */
    private String greeting;
    private String name;

}
