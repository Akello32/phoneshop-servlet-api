package com.es.phoneshop.web.command;

import com.es.phoneshop.model.product.dao.ArrayListProductDao;
import com.es.phoneshop.model.product.dao.DaoFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FindProductByQueryCommand implements Command {
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        ArrayListProductDao productDao = DaoFactory.getInstance().getProductDaoImpl();

        String query = request.getParameter("query");

        request.setAttribute("products", productDao.findProductsByQuery(query));

        return "/WEB-INF/pages/productList.jsp";
    }
}
