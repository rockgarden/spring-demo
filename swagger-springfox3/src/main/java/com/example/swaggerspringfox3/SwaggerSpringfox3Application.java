package com.example.swaggerspringfox3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.oas.annotations.EnableOpenApi;

@EnableOpenApi
@SpringBootApplication
public class SwaggerSpringfox3Application {

    public static void main(String[] args) {
        SpringApplication.run(SwaggerSpringfox3Application.class, args);
    }

}
