package com.example.phone_store.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import com.example.phone_store.entity.OrderDetail;

import java.util.List;

@Mapper
public interface OrderDetailMapper {
    void save(OrderDetail orderDetail);
    List<OrderDetail> findByOrderId(@Param("orderId") Integer orderId);
}