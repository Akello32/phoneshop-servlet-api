package com.es.phoneshop.web.command;

import com.es.phoneshop.model.product.dao.DaoFactory;
import com.es.phoneshop.model.product.dao.ProductDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SortedProductsCommandTest {
    @Mock
    private DaoFactory daoFactory;
    @Mock
    private ProductDao productDao;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private SortProductsByParamCommand sortProduct;

    @Before
    public void setup() {
        when(daoFactory.getProductDaoImpl()).thenReturn(productDao);
        when(productDao.findProducts(any())).thenReturn(new ArrayList<>());
        when(request.getParameter("order")).thenReturn("ascend");
        when(request.getParameter("sortParam")).thenReturn("desc");
        when(request.getParameter("foundOnRequest")).thenReturn("true");
        when(request.getParameter("query")).thenReturn("query");
        sortProduct = new SortProductsByParamCommand(daoFactory);
    }

    @Test
    public void testExecute() {
        sortProduct.execute(request, response);
        verify(daoFactory).getProductDaoImpl();
        verify(productDao).findProducts(any());
        verify(request).getParameter("order");
        verify(request).getParameter("sortParam");
        verify(request).getParameter("foundOnRequest");
        verify(request).getParameter("query");
    }
}
