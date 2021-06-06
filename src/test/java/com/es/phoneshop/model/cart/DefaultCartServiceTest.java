package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.cart.exception.OutOfStockException;
import com.es.phoneshop.model.cart.service.DefaultCartService;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.dao.ProductDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DefaultCartServiceTest {
    private DefaultCartService cartService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession session;
    @Mock
    private Cart cart;
    @Mock
    private ProductDao productDao;

    @Before
    public void setup() {
        Currency usd = Currency.getInstance("USD");
        cartService = DefaultCartService.getInstance();
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(anyString())).thenReturn(new Cart());
        when(productDao.getProduct(1L)).thenReturn(Optional.of(new Product(1l, "sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg")));
        cartService.setProductDao(productDao);
        when(cart.getItems()).thenReturn(new ArrayList<>());
    }

    @Test
    public void testGetCart() {
        assertNotNull(cartService.getCart(request));
        verify(request).getSession();
        verify(session).getAttribute(anyString());
    }

    @Test
    public void testAddToCart() throws OutOfStockException {
        cartService.add(cartService.getCart(request), 1L, 5);
        verify(productDao).getProduct(1L);
        assertNotNull(cartService.getCart(request).getItems().get(0));
    }

    @Test
    public void testUpdate() throws OutOfStockException {
        Cart cart = cartService.getCart(request);
        cartService.add(cart, 1L, 5);
        cartService.add(cart, 1L, 10);
        assertEquals(15, cart.getItems().get(0).getQuantity());
    }
}
