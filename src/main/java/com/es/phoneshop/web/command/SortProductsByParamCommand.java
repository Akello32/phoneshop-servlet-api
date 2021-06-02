package com.es.phoneshop.web.command;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.dao.DaoFactory;
import com.es.phoneshop.model.product.dao.ProductDao;
import com.es.phoneshop.model.product.dao.searchParam.SearchParams;
import com.es.phoneshop.model.product.dao.searchParam.SortOrder;
import com.es.phoneshop.model.product.dao.searchParam.SortParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class SortProductsByParamCommand implements Command {
    private final ProductDao productDao;

    public SortProductsByParamCommand() {
        productDao = DaoFactory.getInstance().getProductDaoImpl();
    }

    SortProductsByParamCommand(DaoFactory factory) {
        productDao = factory.getProductDaoImpl();
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        SortOrder sortOrder;
        SortParam sortParam;
        try {
            sortOrder = SortOrder.valueOf(request.getParameter("order").toUpperCase());
        } catch (IllegalArgumentException ex) {
            sortOrder = SortOrder.ASCEND;
        }

        try {
            sortParam = SortParam.valueOf(request.getParameter("sortParam").toUpperCase());
        } catch (IllegalArgumentException ex) {
            sortParam = SortParam.DEFAULT;
        }

        boolean foundOnRequest = Boolean.parseBoolean(request.getParameter("foundOnRequest"));

        SearchParams params;
        List<Product> result;

        if (foundOnRequest) {
            String query = request.getParameter("query");
            params = new SearchParams(sortOrder, sortParam, query);
            request.setAttribute("foundOnRequest", true);
            request.setAttribute("query", query);
        } else {
            params = new SearchParams(sortOrder, sortParam);
        }
        result = productDao.findProducts(params);

        request.setAttribute("products", result);
        request.setAttribute("sortOrder", sortOrder);
        request.setAttribute("sortParam", sortParam);
        return "/WEB-INF/pages/productList.jsp";
    }
}
