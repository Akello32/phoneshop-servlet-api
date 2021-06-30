package com.es.phoneshop.web.servlet;

import com.es.phoneshop.model.product.dao.DaoFactory;
import com.es.phoneshop.model.product.dao.ProductDao;
import com.es.phoneshop.model.product.dao.searchparam.advancedsearch.AdvancedParam;
import com.es.phoneshop.model.product.dao.searchparam.advancedsearch.DescParam;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class AdvancedSearchPageServlet extends HttpServlet {
    private static final String JSP = "/WEB-INF/pages/advancedSearch.jsp";

    private final ProductDao productDao = DaoFactory.getInstance().getProductDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        AdvancedParam params = new AdvancedParam();

        String desc = req.getParameter("desc");
        params.setDesc(desc);

        String minPriceStr = req.getParameter("minPrice");
        String maxPriceStr = req.getParameter("maxPrice");

        Map<String, String> errors = new HashMap<>();
        try {
            if (minPriceStr != null && !minPriceStr.isEmpty()) {
                BigDecimal minPrice = new BigDecimal(minPriceStr);
                params.setMinPrice(minPrice);
            }
        } catch (NumberFormatException e) {
            errors.put("minPrice", "Incorrect number format");
        }

        try {
            if (maxPriceStr != null && !maxPriceStr.isEmpty()) {
                BigDecimal maxPrice = new BigDecimal(maxPriceStr);
                params.setMaxPrice(maxPrice);
            }
        } catch (NumberFormatException e) {
            errors.put("maxPrice", "Incorrect number format");
        }

        String descParam = req.getParameter("paramDesc");
        if (descParam != null && !descParam.isEmpty()) {
            params.setDescParam(DescParam.valueOf(descParam.toUpperCase()));
        }

        if (errors.isEmpty()) {
            req.setAttribute("products", productDao.advancedSearchProducts(params));
        } else {
            req.setAttribute("errors", errors);
        }
        process(req, resp);
    }

    private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher(JSP).forward(request, response);
    }
}

