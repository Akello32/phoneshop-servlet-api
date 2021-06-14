package com.es.phoneshop.web.servlet;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.dao.DaoFactory;
import com.es.phoneshop.model.product.dao.ProductDao;
import com.es.phoneshop.web.command.Command;
import com.es.phoneshop.web.service.AddToCartService;
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
    private AddToCartService addToCartService;
    @Mock
    private SaveSearchParamsService saveSearchParamsService;
    @Mock
    private Command command;
    @Mock
    private HttpSession session;
    @Mock
    private DaoFactory daoFactory;

    private Cart cart = new Cart();

    private ProductListPageServlet servlet;

    @Before
    public void setup() {
        Currency usd = Currency.getInstance("USD");
        cart.getItems().add(new CartItem(new Product(1l, "sgs", "Samsung Galaxy S", new BigDecimal(100), usd, 100, "jpg"), 10));

        when(request.getRequestDispatcher(anyString())).thenReturn(requestDispatcher);
        when(request.getLocale()).thenReturn(new Locale("ru"));
        when(request.getContextPath()).thenReturn(anyString());
        when(request.getSession()).thenReturn(session);

        when(session.getAttribute("cart")).thenReturn(cart);

        when(daoFactory.getProductDaoImpl()).thenReturn(productDao);
        when(productDao.findProducts()).thenReturn(new ArrayList<>());

        when(saveSearchParamsService.save(request)).thenReturn("str");

        servlet = new ProductListPageServlet(daoFactory, addToCartService, saveSearchParamsService);
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
        when(request.getParameter("quantity1")).thenReturn("10");
        when(addToCartService.add(request, 10, 1L)).thenReturn(true);

        servlet.doPost(request, response);

        verify(request).getParameter("productId");
        verify(request).getParameter("quantity1");
        verify(addToCartService).add(request, 10, 1L);
        verify(request).getContextPath();
        verify(saveSearchParamsService).save(request);
    }

    @Test
    public void doPostNotAdd() throws ServletException, IOException {
        when(request.getAttribute("command")).thenReturn(command);
        when(request.getParameter("productId")).thenReturn("1");
        when(request.getParameter("quantity1")).thenReturn("1000");

        servlet.doPost(request, response);

        verify(addToCartService).add(request, 1000, 1L);
        verify(requestDispatcher).forward(request, response);
        verify(request).getAttribute("command");
        verify(command).execute(request, response);
    }

    @Test
    public void doPostIllegalQuantity() throws ServletException, IOException {
        when(request.getAttribute("command")).thenReturn(command);
        when(request.getParameter("productId")).thenReturn("1");
        when(request.getParameter("quantity1")).thenReturn("ggg");

        servlet.doPost(request, response);

        verify(request).setAttribute("error", "Wrong number format is entered");
        verify(request).getParameter("productId");
        verify(request).getParameter("quantity1");
        verify(requestDispatcher).forward(request, response);
        verify(request).getAttribute("command");
        verify(command).execute(request, response);
    }
}