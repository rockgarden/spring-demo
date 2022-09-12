package com.example.relationaldataaccess;

import java.util.List;

public interface CustomerService {

    int create(long id, String firstName, String lastName);

    List<Customer> getByName(String firstName, String lastName);

    int deleteByName(String firstName, String lastName);

    int getAllCustomers();

    int deleteAllCustomers();

}
