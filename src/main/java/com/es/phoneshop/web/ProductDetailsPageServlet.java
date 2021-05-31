package com.es.phoneshop.web;

import com.es.phoneshop.model.product.dao.DaoFactory;
import com.es.phoneshop.model.product.dao.ProductDao;
import com.es.phoneshop.model.product.exception.ProductNotFoundException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class ProductDetailsPageServlet extends HttpServlet {
    private final ProductDao productDao = DaoFactory.getInstance().getProductDaoImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long productId;
        try {
            productId = Long.valueOf(req.getPathInfo().substring(1));
        } catch (NumberFormatException ex) {
            throw new ProductNotFoundException("Invalid product id format");
        }
        req.setAttribute("product", productDao.getProduct(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId)));
        req.getRequestDispatcher("/WEB-INF/pages/product.jsp").forward(req, resp);
    }
}
