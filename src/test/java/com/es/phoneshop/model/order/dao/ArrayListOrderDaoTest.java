package com.es.phoneshop.model.order.dao;

import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.dao.DaoFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.junit.Assert.assertNotNull;

public class ArrayListOrderDaoTest {
    private static ArrayListOrderDao orderDao;

    @BeforeClass
    public static void setup() {
        orderDao = ArrayListOrderDao.getInstance();
    }

    @BeforeClass
    public static void fillProductDao() {
        Currency usd = Currency.getInstance("USD");
        orderDao.save(new Order());
        orderDao.save(new Order());
        orderDao.save(new Order());
    }

    @Test
    public void testGetOrder() {
        assertNotNull(orderDao.getOrder(1L));
    }

    @Test
    public void testGetOrderBySecureId() {
        Order order = new Order();
        order.setSecureId("secureId");
        orderDao.getItemList().clear();
        orderDao.save(order);
        assertNotNull(orderDao.getOrderBySecureId("secureId"));
    }
}
