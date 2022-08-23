package com.example.mybatisdemo.pojo;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

// @Entity
@Data // Generate setters&getters
@NoArgsConstructor
// 实现 Serializable 接口是为之后使用 Mapper 动态代理做准备
public class User implements Serializable {

    private static final long serialVersionUID = 7542033210412936278L;

    // @Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    
    private String name;
    private Integer age;
    private String email;

    public User(String name, Integer age, String email) {
        this.name = name;
        this.age = age;
        this.email = email;
    }

}