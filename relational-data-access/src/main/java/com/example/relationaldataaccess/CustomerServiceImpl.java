package com.example.relationaldataaccess;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceImpl implements CustomerService {

    private JdbcTemplate jdbcTemplate;

    CustomerServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int create(String firstName, String lastName) {
        return jdbcTemplate.update("INSERT INTO customers(first_name, last_name) VALUES (?,?)", firstName, lastName);
    }

    @Override
    public List<Customer> getByName(String firstName, String lastName) {
        return jdbcTemplate.query(
                "SELECT id, first_name, last_name FROM customers WHERE first_name = ? AND last_name = ?",
                (rs, rowNum) -> new Customer(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name")),
                firstName, lastName);
    }

    @Override
    public int deleteByName(String firstName, String lastName) {
        return jdbcTemplate.update("delete from customers WHERE first_name = ? AND last_name = ?", firstName, lastName);
    }

    @Override
    public int getAllCustomers() {
        return jdbcTemplate.queryForObject("select count(1) from customers", Integer.class);
    }

    @Override
    public int deleteAllCustomers() {
        return jdbcTemplate.update("delete from customers");
    }

}
