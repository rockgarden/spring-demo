package com.example.swagger2markupdoc;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableSwagger2Doc
@SpringBootApplication
public class SwaggerToDocApplication {

    public static void main(String[] args) {
        SpringApplication.run(SwaggerToDocApplication.class, args);
    }

}
