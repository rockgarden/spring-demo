package com.example.relationaldataaccess;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RelationalDataAccessApplicationTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        jdbcTemplate.execute("DROP TABLE customers IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE customers(" + "id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255))");
        // 准备，清空表
        customerService.deleteAllCustomers();
    }

    @Test
    public void test() throws Exception {
        // 插入用户
        List<Object[]> splitUpNames = Arrays.asList("John Woo", "Jeff Dean", "Josh Bloch", "Josh Long", "Oscar Long")
                .stream()
                .map(name -> name.split(" ")).collect(Collectors.toList());
        splitUpNames.forEach(name -> customerService.create(name[0].toString(), name[1].toString()));

        // 查询名为Oscar的用户，判断年龄是否匹配
        List<Customer> userList = customerService.getByName("Oscar", "Long");
        Assert.assertEquals("Oscar", userList.get(0).getFirstName());

        // 查数据库，应该有5个用户
        Assert.assertEquals(5, customerService.getAllCustomers());

        // 删除1个用户
        customerService.deleteByName("Oscar", "Long");

        // 查数据库，应该有4个用户
        Assert.assertEquals(4, customerService.getAllCustomers());

    }

}