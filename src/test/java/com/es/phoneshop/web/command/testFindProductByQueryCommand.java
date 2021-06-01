package com.es.phoneshop.web.command;

import com.es.phoneshop.model.product.dao.DaoFactory;
import com.es.phoneshop.model.product.dao.ProductDao;
import org.junit.Before;

public class testFindProductByQueryCommand {
    private ProductDao productDao;

    @Before
    public void setup() {
        productDao = DaoFactory.getInstance().getProductDaoImpl();
    }


}
