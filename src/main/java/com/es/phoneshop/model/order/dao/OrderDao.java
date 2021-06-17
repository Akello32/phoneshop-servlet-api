package com.es.phoneshop.model.order.dao;

import com.es.phoneshop.model.order.Order;

import java.util.Optional;

public interface OrderDao {
    Optional<Order> getOrder(Long id);

    void save(Order order);

    Optional<Order> getOrderBySecureId(String secureId);
}
