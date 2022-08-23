package com.example.mybatisdemo;

import com.example.mybatisdemo.dao.UserDao;
import com.example.mybatisdemo.mapper.UserMapper;
import com.example.mybatisdemo.pojo.User;

import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class UserServiceTest {
    private ApplicationContext applicationContext;

    /**
     * Spring 配置文件对象
     * 
     * @Before 在执行本类所有测试方法之前先调用的方法
     * 
     * @throws Exception
     */
    @Before
    public void setup() throws Exception {
        applicationContext = new ClassPathXmlApplicationContext("classpath:config/ApplicationContext.xml");
    }

    /**
     * 使用 Mapper 动态代理和注解获取数据
     * 
     * 从 mapper 包中扫描出 Mapper 接口，自动创建代理对象并且在 Spring 容器中注入。自动扫描出来的 Mapper 的 bean 的 id
     * 为 mapper 类名（首字母小写），所以这里获取的就是名为 “userQueryMapper” 的 mapper 代理对象。
     * 
     * @throws Exception
     */
    @Test
    public void testFindUserByIdUseMapper() throws Exception {
        // 通过配置资源对象获取 userMapper 对象
        UserMapper userQueryMapper = (UserMapper) applicationContext.getBean("userQueryMapper");
        // 获取 User
        User user = userQueryMapper.findUserById(1);
        // 输出用户信息
        System.out.println("UseMapper:" + user.getId() + "-" + user.getName());
        Assert.assertEquals(1, user.getId().intValue());
    }

    /**
     * 使用 Dao 获取数据
     * 
     * @throws Exception
     */
    @Test
    public void testFindUserByIdUseDao() throws Exception {
        // 通过配置资源对象获取 userDao 对象
        UserDao userDao = (UserDao) applicationContext.getBean("userDao");
        // 调用 User
        User user = userDao.findUserById(1);
        // 输出用户信息
        System.out.println("UseDao:" + user.getId() + "-" + user.getName());
        Assert.assertEquals(1, user.getId().intValue());
    }
}