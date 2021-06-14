package com.es.phoneshop.web.service;

import com.es.phoneshop.web.command.FindProductByQueryCommand;
import com.es.phoneshop.web.command.SortProductsByParamCommand;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SaveSearchParamsServiceTest {
    private SaveSearchParamsServiceImpl searchParamsService = SaveSearchParamsServiceImpl.getInstance();

    @Mock
    private HttpServletRequest request;

    @Before
    public void setup() {
        when(request.getParameter("order")).thenReturn("descend");
        when(request.getParameter("sortParam")).thenReturn("price");
    }

    @Test
    public void testSaveSortCommand() {
        when(request.getParameter("foundOnRequest")).thenReturn("false");
        when(request.getAttribute("command")).thenReturn(new SortProductsByParamCommand());

        assertEquals("sortProducts?order=descend&sortParam=price", searchParamsService.save(request));

        verify(request).getParameter("order");
        verify(request).getParameter("sortParam");
        verify(request).getParameter("foundOnRequest");
    }

    @Test
    public void testSaveSortCommandWithQuery() {
        when(request.getParameter("foundOnRequest")).thenReturn("true");
        when(request.getAttribute("command")).thenReturn(new SortProductsByParamCommand());
        when(request.getParameter("query")).thenReturn("query");

        assertEquals("sortProducts?order=descend&sortParam=price&query=query&foundOnRequest=true", searchParamsService.save(request));

        verify(request).getParameter("order");
        verify(request).getParameter("sortParam");
        verify(request).getParameter("foundOnRequest");
        verify(request).getParameter("query");
    }


    @Test
    public void testSaveQueryCommand() {
        when(request.getAttribute("command")).thenReturn(new FindProductByQueryCommand());
        when(request.getParameter("query")).thenReturn("query");

        assertEquals("findQuery?query=query", searchParamsService.save(request));

        verify(request).getParameter("query");
    }
}
