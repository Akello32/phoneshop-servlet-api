package com.es.phoneshop.model.order.service;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.order.Order;

public interface OrderService {
    Order getOrder(Cart cart);

    void placeOrder(Order order);
}
