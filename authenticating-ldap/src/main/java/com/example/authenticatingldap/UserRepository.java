package com.example.authenticatingldap;

import org.springframework.data.repository.CrudRepository;

import javax.naming.Name;

public interface UserRepository extends CrudRepository<User, Name> {

}
