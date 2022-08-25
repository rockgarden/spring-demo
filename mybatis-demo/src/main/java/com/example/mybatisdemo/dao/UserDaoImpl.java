package com.example.mybatisdemo.dao;

import com.example.mybatisdemo.pojo.User;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.support.SqlSessionDaoSupport;

/**
 * SqlSessionDaoSupport 类是 MyBatis 与 Spring 整合的 jar 包中提供的，在该类中已经包含了
 * sqlSessionFactory 对象作为其成员变量，而且对外提供 get 和 set 方法，方便 Spring 从外部注入
 * sqlSessionFactory 对象。
 * 
 * UserDaoImpl 类要成功获取 sqlSessionFactory 对象，还需要在 Spring 配置文件
 * applicationContext.xml 中添加 UserDao 的 bean 配置，将其中定义的 sqlSessionFactory
 * 对象当做参数注入进去，这样 UserDaoImpl 继承 SqlSessionDaoSupport 类才会起到作用.
 */
public class UserDaoImpl extends SqlSessionDaoSupport implements UserDao {

    @Override
    public User findUserById(int id) throws Exception {
        // 继承 SqlSessionDaoSupport 类，通过 this.getSqlSession() 得到 sqlSession
        SqlSession sqlSession = this.getSqlSession();
        // statement 与要使用的语句匹配的唯一标识符 UserMapper.xml 的 mapper namespace + id。
        return sqlSession.selectOne("com.example.mybatisdemo.mapper.UserMapper.findUserById", id);
    }
}