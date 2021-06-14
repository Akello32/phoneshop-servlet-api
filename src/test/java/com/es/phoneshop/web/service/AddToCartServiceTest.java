package com.es.phoneshop.web.service;

import com.es.phoneshop.model.cart.Cart;
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
import java.util.Currency;
import java.util.Optional;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddToCartServiceTest {
    private AddToCartService addToCartService;
    private DefaultCartService cartService;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession session;
    @Mock
    private ProductDao productDao;

    @Before
    public void setup() {
        Currency usd = Currency.getInstance("USD");
        addToCartService = AddToCartServiceImpl.getInstance();

        cartService = DefaultCartService.getInstance();

        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(anyString())).thenReturn(new Cart());
        when(productDao.getProduct(1L)).thenReturn(Optional.of(new Product(1l, "sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Samsung/Samsung%20Galaxy%20S.jpg")));

        cartService.setProductDao(productDao);
    }

    @Test
    public void testAdd() {
        assertTrue(addToCartService.add(request, 3, 1L));
    }

    @Test
    public void testAddOitOfStock() {
        assertFalse(addToCartService.add(request, 300, 1L));
    }

    @Test
    public void testAddWithIllegalQuantity() {
        assertFalse(addToCartService.add(request, 0, 1L));
    }
}
