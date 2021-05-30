package com.es.phoneshop.web.command;

import com.es.phoneshop.model.product.dao.ArrayListProductDao;
import com.es.phoneshop.model.product.dao.DaoFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainCommand implements Command {
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        ArrayListProductDao productDao = DaoFactory.getInstance().getProductDaoImpl();
        request.setAttribute("products", productDao.findProducts());

        return "/WEB-INF/pages/productList.jsp";
    }
}
