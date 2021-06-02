package com.es.phoneshop.web.command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommandFilterTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private ServletResponse response;
    @Mock
    private FilterChain chain;

    private final CommandByUriFilter commandByUriFilter = new CommandByUriFilter();

    @Before
    public void setup() {
        when(request.getContextPath()).thenReturn("/phoneshop-servlet-api");
        when(request.getRequestURI()).thenReturn("/phoneshop-servlet-api/productList");
    }

    @Test
    public void testDoFilter() throws IOException, ServletException {
        commandByUriFilter.doFilter(request, response, chain);
        verify(request).getRequestURI();
        verify(request).getContextPath();
    }
}
