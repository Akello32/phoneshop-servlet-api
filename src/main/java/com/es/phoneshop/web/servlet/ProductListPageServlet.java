package com.es.phoneshop.web.servlet;

import com.es.phoneshop.model.product.dao.DaoFactory;
import com.es.phoneshop.model.product.dao.ProductDao;
import com.es.phoneshop.model.product.exception.ProductNotFoundException;
import com.es.phoneshop.web.command.Command;
import com.es.phoneshop.web.service.AddToCartService;
import com.es.phoneshop.web.service.AddToCartServiceImpl;
import com.es.phoneshop.web.service.SaveSearchParamsService;
import com.es.phoneshop.web.service.SaveSearchParamsServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Optional;

public class ProductListPageServlet extends HttpServlet {
    private final ProductDao productDao;
    private final AddToCartService addToCartService;
    private final SaveSearchParamsService searchParamsService;

    public ProductListPageServlet(DaoFactory daoFactory, AddToCartService addToCartService, SaveSearchParamsService searchParamsService) {
        this.productDao = daoFactory.getProductDaoImpl();
        this.addToCartService = addToCartService;
        this.searchParamsService = searchParamsService;
    }

    public ProductListPageServlet() {
        this.productDao = DaoFactory.getInstance().getProductDaoImpl();
        this.addToCartService = AddToCartServiceImpl.getInstance();
        this.searchParamsService = SaveSearchParamsServiceImpl.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long productId = Optional.of(parseProductId(req)).get();
        int quantity;
        try {
            quantity = parseQuantity(req, productId);
            if (addToCartService.add(req, quantity, productId)) {
                resp.sendRedirect(req.getContextPath() + "/productList/"
                        + searchParamsService.save(req) + "&added=true");
            } else {
                process(req, resp);
            }
        } catch (ParseException e) {
            req.setAttribute("error", "Wrong number format is entered");
            process(req, resp);
        }
    }

    private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Command command = (Command) request.getAttribute("command");
        StringBuilder forwardPath = new StringBuilder();
        if (command != null) {
            forwardPath.append(command.execute(request, response));
        } else {
            request.setAttribute("products", productDao.findProducts());
            forwardPath.append("/WEB-INF/pages/productList.jsp");
        }
        request.getRequestDispatcher(forwardPath.toString()).forward(request, response);
    }

    private Long parseProductId(HttpServletRequest req) {
        return Long.valueOf(req.getParameter("productId"));
    }

    private int parseQuantity(HttpServletRequest req, Long productId) throws ParseException {
        String quantityStr = Optional.ofNullable(req.getParameter("quantity" + productId))
                .orElseThrow(() -> new ProductNotFoundException(productId));
        return NumberFormat.getNumberInstance(req.getLocale()).parse(quantityStr).intValue();
    }
}

