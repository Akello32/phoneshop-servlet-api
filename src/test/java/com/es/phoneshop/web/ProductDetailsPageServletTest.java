package com.es.phoneshop.web;

import com.es.phoneshop.model.product.dao.ArrayListProductDao;
import com.es.phoneshop.model.product.exception.ProductNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Currency;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ProductDetailsPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private ArrayListProductDao productDao;

    ProductDetailsPageServlet servlet = new ProductDetailsPageServlet();
    Currency usd = Currency.getInstance("USD");

    @Before
    public void setup() {
        when(request.getPathInfo()).thenReturn("/1");
    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        try {
            servlet.doGet(request, response);
        } catch (ProductNotFoundException ex) {
            assertEquals(ex.getProductCode(), Long.valueOf(request.getPathInfo().substring(1)));
        }
        verify(request, atLeast(2)).getPathInfo();
    }
}
