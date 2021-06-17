package com.es.phoneshop.model.order.service;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.dao.ArrayListOrderDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultOrderServiceTest {
    private static DefaultOrderService orderService;

    @Mock
    private Cart cart;

    @Mock
    private ArrayListOrderDao dao;

    @Before
    public void setup() {
        orderService = DefaultOrderService.getInstance();
        orderService.setOrderDao(dao);
    }

    @Before
    public void fillProductDao() {
        when(cart.getItems()).thenReturn(new ArrayList<>());
        when(cart.getTotalCost()).thenReturn(BigDecimal.valueOf(100));
    }

    @Test
    public void testGetOrder() {
        Order result = orderService.getOrder(cart);

        verify(cart).getItems();
        verify(cart).getTotalCost();
        assertEquals(BigDecimal.valueOf(100), result.getSubtotal());
    }

    @Test
    public void testPlaceHolder() {
        Order order = new Order();

        orderService.placeOrder(order);

        assertNotNull(order.getSecureId());
        verify(dao).save(order);
    }
}
