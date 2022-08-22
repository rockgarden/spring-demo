package com.example.accessingdatamysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccessingDataMysqlApplicationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void test() throws Exception {
        // 创建10条记录
        userRepository.save(new User("AAA", 10, "AAA@test.coom"));
        userRepository.save(new User("BBB", 20, "BBB@test.coom"));
        userRepository.save(new User("CCC", 30, "CCC@test.coom"));
        userRepository.save(new User("DDD", 40, "DDD@test.coom"));
        userRepository.save(new User("EEE", 96, "EEE@test.coom"));
        userRepository.save(new User("FFF", 60, "FFF@test.coom"));
        userRepository.save(new User("GGG", 70, "GGG@test.coom"));
        userRepository.save(new User("HHH", 80, "HHH@test.coom"));
        userRepository.save(new User("III", 90, "III@test.coom"));
        userRepository.save(new User("JJJ", 96, "JJJ@test.coom"));
        
        // 测试findAll, 查询所有记录
        Assert.assertEquals(10, userRepository.findAll().size());

        // 测试findByName, 查询姓名为FFF的User
        Assert.assertEquals(60, userRepository.findByName("FFF").getAge().longValue());

        // 测试findUser, 查询姓名为FFF的User
        Assert.assertEquals(60, userRepository.findUser("FFF").getAge().longValue());

        // 测试findByNameAndAge, 查询姓名为FFF并且年龄为60的User
        Assert.assertEquals("FFF", userRepository.findByNameAndAge("FFF", 60).getName());

        // 测试删除姓名为AAA的User
        userRepository.delete(userRepository.findByName("AAA"));

        // 测试findAll, 查询所有记录, 验证上面的删除是否成功
        Assert.assertEquals(9, userRepository.findAll().size());

    }

}
