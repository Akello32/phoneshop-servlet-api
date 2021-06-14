package com.es.phoneshop.web;

import com.es.phoneshop.model.product.dao.ProductDao;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DemoDataListenerTest {
    @Mock
    ServletContextEvent sce;
    @Mock
    ServletContext context;
    @Mock
    private ProductDao productDao;

    private DemoDataServletContextListener contextListener;

    @Before
    public void setup() {
        when(sce.getServletContext()).thenReturn(context);
        when(productDao.findProducts()).thenReturn(new ArrayList<>());

        contextListener = new DemoDataServletContextListener(productDao);
    }

    @Test
    public void testContextInitialized() {
        when(context.getInitParameter("insertDemoData")).thenReturn("true");

        contextListener.contextInitialized(sce);

        verify(productDao, atLeast(12)).save(any());
    }
}
