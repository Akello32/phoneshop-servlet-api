package com.es.phoneshop.web.servlet;

import com.es.phoneshop.model.cart.Cart;
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
import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CheckoutPageServletTest {
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

    private CheckoutPageServlet servlet;

    @Before
    public void setup() {
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(cart.getTotalCost()).thenReturn(BigDecimal.TEN);
        servlet = new CheckoutPageServlet(cartService);
    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        when(request.getAttribute("cart")).thenReturn(cart);

        servlet.doGet(request, response);

        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGetWithNullCart() throws ServletException, IOException {
        when(request.getAttribute("cart")).thenReturn(null);

        servlet.doGet(request, response);

        verify(request).setAttribute("cartEmpty", "The cart is empty :|");
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoPost() throws ServletException, IOException {
        when(request.getAttribute("cart")).thenReturn(cart);
        when(request.getParameter("firstName")).thenReturn("Matvei");
        when(request.getParameter("lastName")).thenReturn("Mukhlia");
        when(request.getParameter("deliveryAddress")).thenReturn("Minsk");
        when(request.getParameter("phone")).thenReturn("+375 29 1234567");
        when(request.getParameter("deliveryDate")).thenReturn("2021-06-24");
        when(request.getParameter("paymentMethod")).thenReturn("CACHE");

        servlet.doPost(request, response);

        verify(cartService).clearCart(cart);
        verify(response).sendRedirect(anyString());
    }

    @Test
    public void testDoPostWithErrors() throws ServletException, IOException {
        when(request.getAttribute("cart")).thenReturn(cart);

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("errors"), anyMap());
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoPostWithIncorrectFormats() throws ServletException, IOException {
        when(request.getAttribute("cart")).thenReturn(cart);
        when(request.getParameter("phone")).thenReturn("+375 79 1 234567");
        when(request.getParameter("deliveryDate")).thenReturn("2020624");
        when(request.getParameter("paymentMethod")).thenReturn("CAAACHE");

        servlet.doPost(request, response);

        verify(request).setAttribute(eq("errors"), anyMap());
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoPostWithNullCart() throws ServletException, IOException {
        when(request.getAttribute("cart")).thenReturn(null);

        servlet.doPost(request, response);

        verify(request).setAttribute("cartEmpty", "The cart is empty :|");
        verify(requestDispatcher).forward(request, response);
    }
}
