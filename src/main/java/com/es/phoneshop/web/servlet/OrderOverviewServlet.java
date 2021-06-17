package com.es.phoneshop.web.servlet;

import com.es.phoneshop.model.order.dao.ArrayListOrderDao;
import com.es.phoneshop.model.order.dao.OrderDao;
import com.es.phoneshop.model.order.exception.OrderNotFoundException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class OrderOverviewServlet extends HttpServlet {
    private final OrderDao orderDao;

    public OrderOverviewServlet(OrderDao orderDao) {
        this.orderDao = orderDao;
    }

    public OrderOverviewServlet() {
        orderDao = ArrayListOrderDao.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String secureId = parseSecureId(request);
        request.setAttribute("order", orderDao.getOrderBySecureId(secureId)
                .orElseThrow(() -> new OrderNotFoundException(secureId)));
        request.getRequestDispatcher("/WEB-INF/pages/orderOverview.jsp").forward(request, response);
    }

    private String parseSecureId(HttpServletRequest req) {
        return req.getPathInfo().substring(1);
    }
}

