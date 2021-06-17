package com.es.phoneshop.model.order.service;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.dao.ArrayListOrderDao;
import com.es.phoneshop.model.order.dao.OrderDao;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class DefaultOrderService implements OrderService {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private OrderDao orderDao = ArrayListOrderDao.getInstance();

    private DefaultOrderService() {
    }

    private static class SingletonHolder {
        public static final DefaultOrderService INSTANCE = new DefaultOrderService();
    }

    public static DefaultOrderService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void setOrderDao(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    @Override
    public Order getOrder(Cart cart) {
        lock.writeLock().lock();
        Order order = new Order();
        order.setItems(cart.getItems().stream().map(i -> {
            try {
                return (CartItem) i.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList()));
        order.setSubtotal(cart.getTotalCost());
        order.setDeliveryCost(calculateDeliveryCost());
        order.setTotalCost(order.getSubtotal().add(order.getDeliveryCost()));

        lock.writeLock().unlock();
        return order;
    }

    private BigDecimal calculateDeliveryCost() {
        return BigDecimal.valueOf(ThreadLocalRandom.current().nextInt(5, 150));
    }

    @Override
    public void placeOrder(Order order) {
        lock.writeLock().lock();
        order.setSecureId(UUID.randomUUID().toString());
        orderDao.save(order);
        lock.writeLock().unlock();
    }
}