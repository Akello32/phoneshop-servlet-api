package com.es.phoneshop.web.servlet;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.exception.OutOfStockException;
import com.es.phoneshop.model.cart.service.CartService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CartPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private CartService cartService;
    @Mock
    private Cart cart;

    private CartPageServlet servlet;

    @Before
    public void setup() {
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getLocale()).thenReturn(new Locale("ru"));
        when(cartService.getCart(request)).thenReturn(cart);
        servlet = new CartPageServlet(cartService);
    }


    @Test
    public void testDoGet() throws ServletException, IOException {
        servlet.doGet(request, response);

        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoDelete() throws ServletException, IOException {
        when(request.getParameter("deletingProduct")).thenReturn("1");

        servlet.doDelete(request, response);

        verify(cartService).delete(cart, 1L);
        verify(response).sendRedirect(anyString());
    }

    @Test
    public void testDoDeleteWithIllegalId() throws ServletException, IOException {
        when(request.getParameter("deletingProduct")).thenReturn("b");

        servlet.doDelete(request, response);

        verify(request).setAttribute("deleteCartItemError", "Wrong product id format");
    }

    @Test
    public void testDoPost() throws ServletException, IOException, OutOfStockException {
        when(request.getParameterValues("quantity")).thenReturn(new String[]{"11", "12", "13"});
        when(request.getParameterValues("productId")).thenReturn(new String[]{"1", "2", "3"});

        servlet.doPost(request, response);

        verify(request).getParameterValues("quantity");
        verify(request).getParameterValues("productId");
        verify(cartService, atLeast(3)).getCart(request);
        verify(cartService).update(cart, 1L, 11);
        verify(response).sendRedirect(anyString());
    }

    @Test
    public void testDoPostWithIllegalQuantity() throws ServletException, IOException {
        when(request.getParameterValues("quantity")).thenReturn(new String[]{"bdfbdfb", "12", "13"});
        when(request.getParameterValues("productId")).thenReturn(new String[]{"1", "2", "3"});
        Map<Long, String> errors = new HashMap<>();
        errors.put(1L, "Wrong number format is entered");

        servlet.doPost(request, response);

        verify(request).getParameterValues("quantity");
        verify(request).getParameterValues("productId");
        verify(request).setAttribute("errors", errors);
    }
}