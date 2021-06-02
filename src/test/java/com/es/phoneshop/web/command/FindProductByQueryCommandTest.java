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
public class FindProductByQueryCommandTest {
    @Mock
    private DaoFactory daoFactory;
    @Mock
    private ProductDao productDao;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private FindProductByQueryCommand queryCommand;

    @Before
    public void setup() {
        when(daoFactory.getProductDaoImpl()).thenReturn(productDao);
        when(productDao.findProducts(any())).thenReturn(new ArrayList<>());
        when(request.getParameter("query")).thenReturn("query");
        queryCommand = new FindProductByQueryCommand(daoFactory);
    }

    @Test
    public void testExecute() {
        queryCommand.execute(request, response);
        verify(daoFactory).getProductDaoImpl();
        verify(productDao).findProducts(any());
        verify(request).getParameter("query");
    }
}
