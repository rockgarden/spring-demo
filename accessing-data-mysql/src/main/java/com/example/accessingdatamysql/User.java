package com.example.accessingdatamysql;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity // This tells Hibernate to make a table out of this class
@Data // Generate setters&getters
@NoArgsConstructor // Generates a no-args constructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
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
