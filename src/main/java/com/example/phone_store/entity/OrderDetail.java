package com.example.phone_store.entity;

public class OrderDetail {

    private Integer orderDetailId;
    private Order order;
    private Product product;
    private Integer quantity;
    private Long price;

    public Integer getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(Integer orderDetailId) {
        this.orderDetailId = orderDetailId;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long  getPrice() {
        return price;
    }

    public void setPrice(Long  price) {
        this.price = price;
    }

}