package com.example.mybatisdemo.pojo;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data // Generate setters&getters
@NoArgsConstructor
public class Student implements Serializable {

    private static final long serialVersionUID = -339516038496531943L;
    @Id
    private String sno;
    private String name;
    private String sex;

}
