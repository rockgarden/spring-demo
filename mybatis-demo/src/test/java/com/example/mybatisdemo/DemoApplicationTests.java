package com.example.mybatisdemo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.example.mybatisdemo.mapper.UserMapper;
import com.example.mybatisdemo.pojo.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class DemoApplicationTests {

    @Test
    void contextLoads() {
        log.trace("load blank application context");
    }

    @Autowired
    private UserMapper userMapper;

    @Test
    @Rollback
    public void test() throws Exception {
        // insert a record, query and verification
        userMapper.insert("AAA", 20, "AAA@test.com");
        User u = userMapper.findByName("AAA");
        Assert.assertEquals(20, u.getAge().intValue());

        // update record, query and verification
        u.setAge(30);
        userMapper.update(u);
        u = userMapper.findByName("AAA");
        Assert.assertEquals(30, u.getAge().intValue());

        // delete record, query and verification
        userMapper.delete(u.getId());
        u = userMapper.findByName("AAA");
        Assert.assertEquals(null, u);

        // insert a record by map, query and verification
        Map<String, Object> map = new HashMap<>();
        map.put("name", "BBB");
        map.put("age", 40);
        map.put("email", "BBB@test.com");
        userMapper.insertByMap(map);
        u = userMapper.findByName("BBB");
        Assert.assertEquals(40, u.getAge().intValue());
    }

    @Test
    @Rollback
    public void testUserMapper() throws Exception {
        userMapper.insert("AAA", 20, "AAA@test.com");
        List<User> userList = userMapper.findAll();
        System.out.println(userList);
        Assert.assertEquals(1, userList.size());
        for (User user : userList) {
            Assert.assertEquals(null, user.getId());
            Assert.assertNotEquals(null, user.getName());
        }
    }

}
