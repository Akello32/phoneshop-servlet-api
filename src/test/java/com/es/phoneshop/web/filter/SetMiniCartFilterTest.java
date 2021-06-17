package com.es.phoneshop.web.filter;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.web.filter.SetMiniCartFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SetMiniCartFilterTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private ServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private FilterChain chain;
    @Mock
    private Cart cart;

    private SetMiniCartFilter filter = new SetMiniCartFilter();

    @Before
    public void setup() {
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(anyString())).thenReturn(cart);
    }

    @Test
    public void testDoFilter() throws IOException, ServletException {
        filter.doFilter(request, response, chain);

        verify(request).setAttribute("cart", cart);
        verify(chain).doFilter(request, response);
    }
}
