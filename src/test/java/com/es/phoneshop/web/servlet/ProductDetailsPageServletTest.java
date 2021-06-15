package com.es.phoneshop.web.servlet;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.service.DefaultCartService;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.dao.ProductDao;
import com.es.phoneshop.model.product.exception.ProductNotFoundException;
import com.es.phoneshop.web.service.RecentlyViewedService;
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
import java.math.BigDecimal;
import java.util.Currency;
import java.util.Locale;
import java.util.Optional;

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
    @Mock
    private ProductDao productDao;

    @Mock
    private RecentlyViewedService recentlyViewedService;

    private ProductDetailsPageServlet servlet;

    private DefaultCartService cartService = DefaultCartService.getInstance();

    private Currency usd = Currency.getInstance("USD");

    private Product product = new Product(1l, "sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "jpg");

    @Before
    public void setup() {
        when(request.getPathInfo()).thenReturn("/1");
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getParameter(anyString())).thenReturn("11");
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(anyString())).thenReturn(new Cart());
        when(request.getLocale()).thenReturn(new Locale("ru"));
        when(productDao.getProduct(1l)).thenReturn(Optional.of(product));

        cartService.setProductDao(productDao);

        servlet = new ProductDetailsPageServlet(productDao, recentlyViewedService, cartService);
    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        servlet.doGet(request, response);

        verify(productDao).getProduct(1l);
        verify(request).setAttribute("product", product);
        verify(recentlyViewedService).addToRecentlyViewed(request, product);
        verify(request).getRequestDispatcher(anyString());
    }

    @Test(expected = ProductNotFoundException.class)
    public void testDoGetWithIllegalId() throws ServletException, IOException {
        when(request.getPathInfo()).thenReturn("/b");

        servlet.doGet(request, response);
    }

    @Test
    public void testDoPost() throws ServletException, IOException {
        servlet.doPost(request, response);

        verify(request).getPathInfo();
        verify(response).sendRedirect(anyString());
    }

    @Test
    public void testDoPostWithWrongQuantity() throws ServletException, IOException {
        when(request.getParameter("quantity")).thenReturn("10000");
        servlet.doPost(request, response);

        verify(request).setAttribute("error", "Out of stock. Available " + 100 + ". Requested " + 10000);
        verify(request, atLeast(2)).getPathInfo();
        verify(request).getParameter(anyString());
        verify(request).getLocale();
    }
}
