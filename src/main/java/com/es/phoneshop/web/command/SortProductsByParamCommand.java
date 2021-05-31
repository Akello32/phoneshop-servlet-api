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

        List<Product> result = productDao.sortedProducts(sortParam, sortOrder);

        request.setAttribute("products", result);

        return "/WEB-INF/pages/productList.jsp";
    }
}
