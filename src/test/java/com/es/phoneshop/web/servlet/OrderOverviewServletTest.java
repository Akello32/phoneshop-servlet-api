package com.es.phoneshop.web.servlet;

import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.dao.OrderDao;
import com.es.phoneshop.model.order.exception.OrderNotFoundException;
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
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class OrderOverviewServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private OrderDao dao;

    private OrderOverviewServlet servlet;

    @Before
    public void setup() {
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        servlet = new OrderOverviewServlet(dao);
    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        when(dao.getOrderBySecureId(anyString())).thenReturn(Optional.of(new Order()));
        when(request.getPathInfo()).thenReturn("secureID");

        servlet.doGet(request, response);

        verify(requestDispatcher).forward(request, response);
    }

    @Test (expected = OrderNotFoundException.class)
    public void testDoGetOrderNotFound() throws ServletException, IOException {
        when(dao.getOrderBySecureId(anyString())).thenReturn(Optional.empty());
        when(request.getPathInfo()).thenReturn("secureID");

        servlet.doGet(request, response);
    }
}
