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
public class MainCommandTest {
    @Mock
    private DaoFactory daoFactory;
    @Mock
    private ProductDao productDao;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private MainCommand mainCommand;

    @Before
    public void setup() {
        when(daoFactory.getProductDaoImpl()).thenReturn(productDao);
        when(productDao.findProducts()).thenReturn(new ArrayList<>());
        mainCommand = new MainCommand(daoFactory);
    }

    @Test
    public void testExecute() {
        mainCommand.execute(request, response);
        verify(daoFactory).getProductDaoImpl();
        verify(productDao).findProducts();
    }
}
