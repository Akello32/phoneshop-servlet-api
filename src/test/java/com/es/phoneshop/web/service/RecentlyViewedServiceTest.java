package com.es.phoneshop.web.service;

import com.es.phoneshop.model.product.Product;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RecentlyViewedServiceTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession session;

    private RecentlyViewedService viewedService = RecentlyViewedServiceImpl.getInstance();

    private Currency usd = Currency.getInstance("USD");


    @Before
    public void setup() {
        when(request.getSession()).thenReturn(session);
    }

    @Test
    public void testAddToRecentlyViewedWithNullList() {
        Product product = new Product(1l, "iphone", "Apple iPhone", new BigDecimal(200), usd, 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone.jpg");
        when(session.getAttribute(anyString())).thenReturn(null);

        viewedService.addToRecentlyViewed(request, product);

        verify(request, atLeast(2)).getSession();
        verify(session).getAttribute(anyString());
    }

    @Test
    public void testAddToRecentlyViewedWithNotNullList() {
        Product product = new Product(1l, "iphone", "Apple iPhone", new BigDecimal(200), usd, 10, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/Apple/Apple%20iPhone.jpg");
        Product productToAdd = new Product(2l, "htces4g", "HTC EVO Shift 4G", new BigDecimal(320), usd, 3, "https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/manufacturer/HTC/HTC%20EVO%20Shift%204G.jpg");
        LinkedList<Product> products = new LinkedList<>();
        products.add(product);
        when(session.getAttribute(anyString())).thenReturn(products);

        viewedService.addToRecentlyViewed(request, productToAdd);

        verify(request).getSession();
        verify(session).getAttribute(anyString());
        assertEquals(2, products.size());
    }
}
