package com.es.phoneshop.web.servlet;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.cart.service.CartService;
import com.es.phoneshop.model.cart.service.DefaultCartService;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.dao.DaoFactory;
import com.es.phoneshop.model.product.dao.ProductDao;
import com.es.phoneshop.web.command.Command;
import com.es.phoneshop.web.service.SaveSearchParamsService;
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
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProductListPageServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ProductDao productDao;
    @Mock
    private SaveSearchParamsService saveSearchParamsService;
    @Mock
    private Command command;
    @Mock
    private HttpSession session;
    @Mock
    private DaoFactory daoFactory;

    private ProductListPageServlet servlet;

    private DefaultCartService cartService = DefaultCartService.getInstance();

    Currency usd = Currency.getInstance("USD");

    private Product product = new Product(1l, "sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "jpg");

    @Before
    public void setup() {
        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getLocale()).thenReturn(new Locale("ru"));
        when(request.getSession()).thenReturn(session);

//        when(session.getAttribute("cart")).thenReturn(new Cart());

        when(daoFactory.getProductDaoImpl()).thenReturn(productDao);
        when(productDao.findProducts()).thenReturn(new ArrayList<>());
        when(productDao.getProduct(1l)).thenReturn(Optional.of(product));

        when(saveSearchParamsService.save(request)).thenReturn("str");

        cartService.setProductDao(productDao);

        servlet = new ProductListPageServlet(daoFactory, saveSearchParamsService, cartService);
    }

    @Test
    public void testDoGet() throws ServletException, IOException {
        when(request.getAttribute("command")).thenReturn(null);

        servlet.doGet(request, response);

        verify(request).setAttribute("products", productDao.findProducts());
        verify(productDao, atLeast(2)).findProducts();
        verify(requestDispatcher).forward(request, response);
    }

    @Test
    public void testDoGetWithCommand() throws ServletException, IOException {
        when(request.getAttribute("command")).thenReturn(command);
        when(command.execute(request, response)).thenReturn(anyString());

        servlet.doGet(request, response);

        verify(requestDispatcher).forward(request, response);
        verify(request).getAttribute("command");
        verify(command).execute(request, response);
    }

    @Test
    public void doPost() throws ServletException, IOException {
        when(request.getParameter("productId")).thenReturn("1");
        when(request.getParameter("quantity")).thenReturn("10");

        servlet.doPost(request, response);

        verify(response).sendRedirect(anyString());
        verify(request).getContextPath();
        verify(saveSearchParamsService).save(request);
    }

    @Test
    public void doPostIllegalQuantity() throws ServletException, IOException {
        when(request.getParameter("productId")).thenReturn("1");
        when(request.getParameter("quantity")).thenReturn("ggg");
        when(request.getAttribute("command")).thenReturn(command);

        servlet.doPost(request, response);

        verify(request).setAttribute("error", "Wrong number format is entered");
        verify(requestDispatcher).forward(request, response);
        verify(request).getAttribute("command");
        verify(command).execute(request, response);
    }
}