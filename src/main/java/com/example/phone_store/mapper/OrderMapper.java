package com.example.phone_store.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.example.phone_store.entity.Order;

import java.util.List;

@Mapper
public interface OrderMapper {
    void save(Order order);
    List<Order> findByUserId(@Param("userId") Long userId);
    List<Order> findAll();
    Order findById(@Param("orderId") Integer orderId);
    int updateStatus(@Param("orderId") Integer orderId, @Param("status") String status);
    int deleteById(@Param("orderId") Integer orderId);
}