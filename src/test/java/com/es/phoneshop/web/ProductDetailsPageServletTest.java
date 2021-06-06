package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.product.exception.ProductNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductDetailsPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private HttpSession session;

    ProductDetailsPageServlet servlet = new ProductDetailsPageServlet();

    @Before
    public void setup() {
        when(request.getPathInfo()).thenReturn("/1");
        when(request.getParameter(anyString())).thenReturn("11");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(anyString())).thenReturn(new Cart());
        when(request.getLocale()).thenReturn(new Locale("ru"));
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

    @Test
    public void testDoPost() throws ServletException, IOException {
        try {
            servlet.doPost(request, response);
        } catch (ProductNotFoundException ex) {
            assertEquals(ex.getProductCode(), Long.valueOf(request.getPathInfo().substring(1)));
        }
        verify(request).getPathInfo();
        verify(request).getParameter(anyString());
        verify(request).getSession();
        verify(session).getAttribute(anyString());
        verify(request).getLocale();
    }

    @Test
    public void testDoPostWithWrongQuantity() throws ServletException, IOException {
        when(request.getParameter(anyString())).thenReturn("0");
        try {
            servlet.doPost(request, response);
        } catch (ProductNotFoundException ex) {
            assertEquals(ex.getProductCode(), Long.valueOf(request.getPathInfo().substring(1)));
        }

        verify(request, atLeast(2)).getPathInfo();
        verify(request).getParameter(anyString());
        verify(request).getLocale();
    }
}
