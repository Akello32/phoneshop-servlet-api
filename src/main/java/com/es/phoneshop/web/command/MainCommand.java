package com.es.phoneshop.web.command;

import com.es.phoneshop.model.product.dao.DaoFactory;
import com.es.phoneshop.model.product.dao.ProductDao;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainCommand implements Command {
    private final ProductDao productDao;

    public MainCommand() {
        productDao = DaoFactory.getInstance().getProductDaoImpl();
    }

    MainCommand(DaoFactory daoFactory) {
        productDao = daoFactory.getProductDaoImpl();
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        request.setAttribute("products", productDao.findProducts());

        return "/WEB-INF/pages/productList.jsp";
    }
}
