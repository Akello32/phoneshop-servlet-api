package com.es.phoneshop.model.order.dao;

import com.es.phoneshop.model.order.Order;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class ArrayListOrderDaoTest {
    private static ArrayListOrderDao orderDao;

    @BeforeClass
    public static void setup() {
        orderDao = ArrayListOrderDao.getInstance();
    }

    @BeforeClass
    public static void fillProductDao() {
        Order order = new Order();
        order.setSecureId("secId1");
        orderDao.save(order);
        order.setSecureId("secId2");
        orderDao.save(order);
        order.setSecureId("secId2");
        orderDao.save(order);
    }

    @Test
    public void testGetOrder() {
        assertNotNull(orderDao.getOrder(1L));
    }

    @Test
    public void testGetOrderBySecureId() {
        Order order = new Order();
        order.setSecureId("secureId");
        orderDao.save(order);
        assertNotNull(orderDao.getOrderBySecureId("secureId"));
    }
}
