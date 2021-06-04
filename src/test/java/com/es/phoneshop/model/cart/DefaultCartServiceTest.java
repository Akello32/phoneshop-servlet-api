package com.es.phoneshop.model.cart;

import com.es.phoneshop.model.cart.service.DefaultCartService;
import com.es.phoneshop.model.product.Product;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.*;


@RunWith(MockitoJUnitRunner.class)
public class DefaultCartServiceTest {
    private DefaultCartService cartService;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession session;
    @Mock
    private Cart cart;

    @Before
    public void setup() {
        cartService = DefaultCartService.getInstance();
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(anyString())).thenReturn(new Cart());
    }

    @Test
    public void testGetCart() {
        assertNotNull(cartService.getCart(request));
        verify(request).getSession();
        verify(session).getAttribute(anyString());
    }

    @Test
    public void testOutputCart() {
        Currency usd = Currency.getInstance("USD");
        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(new CartItem(new Product(1L, "sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg")
                , 10));
        cartItems.add(new CartItem(new Product(1L, "sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg")
                , 10));
        cartItems.add(new CartItem(new Product(2L, "iphone", "Apple iPhone", new BigDecimal(200), usd, 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone.jpg")
                , 3));
        cartItems.add(new CartItem(new Product(2L, "iphone", "Apple iPhone", new BigDecimal(200), usd, 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone.jpg")
                , 3));
        String expected = "sgs : 20;  iphone : 6;  ";
        when(cart.getItems()).thenReturn(cartItems);

        assertEquals(expected, cartService.outPutCart(cart));
        verify(cart, atLeast(3)).getItems();
    }
}
