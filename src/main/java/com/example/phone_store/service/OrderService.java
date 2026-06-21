package com.example.phone_store.service;

import com.example.phone_store.entity.Order;
import com.example.phone_store.entity.OrderDetail;
import com.example.phone_store.mapper.OrderDetailMapper;
import com.example.phone_store.mapper.OrderMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderMapper orderMapper;
    private final OrderDetailMapper orderDetailMapper;

    public OrderService(OrderMapper orderMapper, OrderDetailMapper orderDetailMapper) {
        this.orderMapper = orderMapper;
        this.orderDetailMapper = orderDetailMapper;
    }

    public void save(Order order) {
        orderMapper.save(order);
    }

    public List<Order> getOrdersByUser(Integer userId) {
        return orderMapper.findByUserId(userId);
    }

    public List<Order> getAllOrders() {
        return orderMapper.findAll();
    }

    public Order findById(Integer orderId) {
        return orderMapper.findById(orderId);
    }

    public List<Order> findAll() {
        return orderMapper.findAll();
    }

    public List<OrderDetail> findDetailsByOrderId(Integer orderId) {
        return orderDetailMapper.findByOrderId(orderId);
    }

    public boolean updateStatus(Integer orderId, String status) {
        return orderMapper.updateStatus(orderId, status) > 0;
    }

    public boolean deleteById(Integer orderId) {
        return orderMapper.deleteById(orderId) > 0;
    }
}