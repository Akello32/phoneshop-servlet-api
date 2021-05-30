package com.es.phoneshop.web;

import com.es.phoneshop.model.product.dao.DaoFactory;
import com.es.phoneshop.model.product.dao.ProductDao;
import com.es.phoneshop.web.command.Command;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ProductListPageServlet extends HttpServlet {
    private final DaoFactory daoFactory = DaoFactory.getInstance();
    private final ProductDao productDao = daoFactory.getProductDaoImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp);
    }

    private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException  {
        Command command = (Command) request.getAttribute("command");
        StringBuilder forwardPath = new StringBuilder("");
        if (command != null) {
            forwardPath.append(command.execute(request, response));
        } else {
            request.setAttribute("products", productDao.findProducts());
            forwardPath.append("/WEB-INF/pages/productList.jsp");
        }
        request.getRequestDispatcher(forwardPath.toString()).forward(request, response);
    }
}

