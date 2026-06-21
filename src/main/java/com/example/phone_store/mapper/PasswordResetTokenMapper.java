package com.example.phone_store.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.example.phone_store.entity.PasswordResetToken;

import java.time.LocalDateTime;

@Mapper
public interface PasswordResetTokenMapper {

    int insert(PasswordResetToken token);

    PasswordResetToken findByToken(@Param("token") String token);

    int deleteByToken(@Param("token") String token);

    int deleteByUserId(@Param("userId") Long userId);

    int deleteExpired(@Param("now") LocalDateTime now);
}