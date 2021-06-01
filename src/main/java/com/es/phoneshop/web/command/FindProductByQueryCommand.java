package com.es.phoneshop.web.command;

import com.es.phoneshop.model.product.dao.DaoFactory;
import com.es.phoneshop.model.product.dao.ProductDao;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FindProductByQueryCommand implements Command {
    private final ProductDao productDao;

    public FindProductByQueryCommand() {
        this.productDao  = DaoFactory.getInstance().getProductDaoImpl();
    }

    /**Constructor for tests*/
    public FindProductByQueryCommand(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        String query = request.getParameter("query");

        request.setAttribute("products", productDao.findProductsByQuery(query));

        request.setAttribute("foundOnRequest", true);
        request.setAttribute("query", query);

        return "/WEB-INF/pages/productList.jsp";
    }
}
