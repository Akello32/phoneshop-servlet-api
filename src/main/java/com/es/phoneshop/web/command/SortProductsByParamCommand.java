package com.es.phoneshop.web.command;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.dao.DaoFactory;
import com.es.phoneshop.model.product.dao.ProductDao;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class SortProductsByParamCommand implements Command {
    private final ProductDao productDao = DaoFactory.getInstance().getProductDaoImpl();

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        String sortOrder = request.getParameter("order");
        String sortParam = request.getParameter("sortParam");
        boolean foundOnRequest = Boolean.parseBoolean(request.getParameter("foundOnRequest"));

        List<Product> result;

        if (foundOnRequest) {
            String query = request.getParameter("query");
            result = productDao.findProductsByQuery(query);
            result = productDao.sortedProducts(result, sortParam, sortOrder);
            request.setAttribute("foundOnRequest", true);
            request.setAttribute("query", query);
        } else {
            result = productDao.sortedProducts(null, sortParam, sortOrder);
        }

        request.setAttribute("products", result);
        request.setAttribute("sortOrder", sortOrder);
        request.setAttribute("sortParam", sortParam);
        return "/WEB-INF/pages/productList.jsp";
    }
}
