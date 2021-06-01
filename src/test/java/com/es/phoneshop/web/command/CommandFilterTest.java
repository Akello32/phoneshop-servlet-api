package com.es.phoneshop.web.command;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommandFilterTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private Map<String, Command> commands;
    @Mock
    private FilterChain chain;
    @Mock
    private FilterConfig filterConfig;

    private final CommandByUriFilter commandByUriFilter = new CommandByUriFilter(commands);

    @Before
    public void setup(){
        when(request.getContextPath()).thenReturn("/phoneshop-servlet-api");
        when(request.getRequestURI()).thenReturn("/phoneshop-servlet-api/productList");
        when(commands.get("/productList")).thenReturn(new MainCommand());
    }

    @Test
    public void testDoFilter() throws IOException, ServletException {
//        commandByUriFilter.init(filterConfig);
        commandByUriFilter.doFilter(request, response, chain);

        verify(commands).get("/productList");
    }
}
