package com.es.phoneshop.web.filter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DosFilterTest {
    private DosFilter filter = new DosFilter();

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private HttpSession session;
    @Mock
    private FilterChain chain;

    @Before
    public void setup() {
        when(request.getSession()).thenReturn(session);
        when(request.getRemoteAddr()).thenReturn("ip");
        when(session.getAttribute(anyString())).thenReturn(System.currentTimeMillis());
    }

    @Test
    public void testDoFilter() throws IOException, ServletException {
        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    public void testDoFilterSetStatus() throws IOException, ServletException {
        for (int i = 0; i < 21; i++) {
            filter.doFilter(request, response, chain);
        }

        filter.doFilter(request, response, chain);

        verify(response, atLeast(2)).setStatus(429);
    }
}
