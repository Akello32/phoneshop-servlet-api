package com.es.phoneshop.web.command;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.dao.ArrayListProductDao;
import com.es.phoneshop.model.product.dao.DaoFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

public class SortProductsByParamCommand implements Command {
    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        String sortOrder = request.getParameter("order");
        String sortParam = request.getParameter("sortParam");

        ArrayListProductDao productDao = DaoFactory.getInstance().getProductDaoImpl();

        List<Product> result = productDao.sortedProducts(sortParam);

        if (sortOrder.equals("descend")) {
            Collections.reverse(result);
        }

        request.setAttribute("products", result);

        return "/WEB-INF/pages/productList.jsp";
    }
}
