package com.es.phoneshop.web.service;

import com.es.phoneshop.model.product.dao.searchparam.SortOrder;
import com.es.phoneshop.model.product.dao.searchparam.SortParam;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ParseSearchParamsServiceTest {
    private ParseSearchParamsServiceImpl paramsService = ParseSearchParamsServiceImpl.getInstance();

    @Mock
    private HttpServletRequest request;

    @Test
    public void testParseSortOrder() {
        when(request.getParameter("order")).thenReturn("descend");

        assertEquals(SortOrder.DESCEND, paramsService.parseSortOrder(request));

        verify(request).getParameter("order");
    }

    @Test
    public void testParseSortOrderIllegalArg() {
        when(request.getParameter("order")).thenReturn("test");

        assertEquals(SortOrder.ASCEND, paramsService.parseSortOrder(request));

        verify(request).getParameter("order");
    }

    @Test
    public void testParseSortParam() {
        when(request.getParameter("sortParam")).thenReturn("price");

        assertEquals(SortParam.PRICE, paramsService.parseSortParam(request));

        verify(request).getParameter("sortParam");
    }

    @Test
    public void testParseSortParamIllegalArg() {
        when(request.getParameter("sortParam")).thenReturn("test");

        assertEquals(SortParam.DEFAULT, paramsService.parseSortParam(request));

        verify(request).getParameter("sortParam");
    }

    @Test
    public void parseFoundOn() {
        when(request.getParameter("foundOnRequest")).thenReturn("true");

        assertTrue(paramsService.parseFoundOn(request));

        verify(request).getParameter("foundOnRequest");
    }
}
