package com.es.phoneshop.web.command;

import com.es.phoneshop.model.product.dao.DaoFactory;
import com.es.phoneshop.model.product.dao.ProductDao;
import com.es.phoneshop.model.product.dao.searchParam.SearchParams;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FindProductByQueryCommand implements Command {
    private final ProductDao productDao;

    public FindProductByQueryCommand() {
        this.productDao  = DaoFactory.getInstance().getProductDaoImpl();
    }

    FindProductByQueryCommand(DaoFactory daoFactory) {
        this.productDao = daoFactory.getProductDaoImpl();
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        String query = request.getParameter("query");

        SearchParams params = new SearchParams(query);
        request.setAttribute("products", productDao.findProducts(params
        ));

        request.setAttribute("foundOnRequest", true);
        request.setAttribute("query", query);

        return "/WEB-INF/pages/productList.jsp";
    }
}
