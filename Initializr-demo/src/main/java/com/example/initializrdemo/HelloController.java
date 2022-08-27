package com.example.initializrdemo;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;

@Slf4j
@RestController
public class HelloController {

    @Value("${example.name:}")
    private String name;

    @Value("${db:}")
    private String db;

    @Value("${mq:}")
    private String mq;

    @RequestMapping("/hello")
    public String index() {
        log.info(name);
        return name;
    }

    @RequestMapping("/group")
    public String group() {
        log.info("db：" + db);
        log.info("mq：" + mq);
        return db + "," + mq;
    }

}