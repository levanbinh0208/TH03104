package com.example.phone_store.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.example.phone_store.entity.User;

import java.util.List;

@Mapper
public interface UserMapper {
    User findByUsername(@Param("username") String username);
    User findById(Integer id);
    int insert(User user);
    List<User> findAll();
    int updateRole(@Param("userId") Integer userId, @Param("roleName") String roleName);
    int deleteById(@Param("userId") Integer userId);

    User login(@Param("username") String username, @Param("password") String password);

    User findByEmail(@Param("email") String email);

    int updatePassword(@Param("userId") Long userId, @Param("password") String password);
}