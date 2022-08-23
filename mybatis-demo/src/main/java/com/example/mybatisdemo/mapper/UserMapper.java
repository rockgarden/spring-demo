package com.example.mybatisdemo.mapper;

import com.example.mybatisdemo.pojo.User;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 代理类
 * 
 * 在默认情况下，该 bean 的名字为 userMapper（即首字母小写）
 */
@Mapper
public interface UserMapper {

    @Select("SELECT * FROM USER WHERE id = #{id}")
    User findUserById(Integer id) throws Exception;

    @Select("SELECT * FROM USER WHERE NAME = #{name}")
    User findByName(@Param("name") String name);

    @Insert("INSERT INTO USER(NAME, AGE, EMAIL) VALUES(#{name}, #{age}, #{email})")
    int insert(@Param("name") String name, @Param("age") Integer age, @Param("email") String email);

    @Insert("INSERT INTO USER(NAME, AGE, EMAIL) VALUES(#{name}, #{age}, #{email})")
    int insertByUser(User user);

    @Insert("INSERT INTO USER(NAME, AGE, EMAIL) VALUES(#{name,jdbcType=VARCHAR}, #{age,jdbcType=INTEGER}, #{email,jdbcType=VARCHAR})")
    int insertByMap(Map<String, Object> map);

    @Update("UPDATE user SET age=#{age} WHERE name=#{name}")
    void update(User user);

    @Delete("DELETE FROM user WHERE id =#{id}")
    void delete(Integer integer);

    @Results({
            @Result(property = "name", column = "name"),
            @Result(property = "age", column = "age"),
            @Result(property = "email", column = "email")
    })
    @Select("SELECT name, age FROM user")
    List<User> findAll();
}