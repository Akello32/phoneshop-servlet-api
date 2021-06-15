package com.es.phoneshop.web.servlet;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.exception.OutOfStockException;
import com.es.phoneshop.model.cart.service.CartService;
import com.es.phoneshop.model.cart.service.DefaultCartService;
import com.es.phoneshop.model.product.dao.DaoFactory;
import com.es.phoneshop.model.product.dao.ProductDao;
import com.es.phoneshop.model.product.exception.ProductNotFoundException;
import com.es.phoneshop.web.command.Command;
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
    private final SaveSearchParamsService searchParamsService;
    private final CartService cartService;

    public ProductListPageServlet(DaoFactory daoFactory, SaveSearchParamsService searchParamsService, CartService cartService) {
        this.productDao = daoFactory.getProductDaoImpl();
        this.searchParamsService = searchParamsService;
        this.cartService = cartService;
    }

    public ProductListPageServlet() {
        this.productDao = DaoFactory.getInstance().getProductDaoImpl();
        this.searchParamsService = SaveSearchParamsServiceImpl.getInstance();
        this.cartService = DefaultCartService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Long productId = Optional.of(parseProductId(req)).get();
            int quantity = parseQuantity(req);
            String saveParams = searchParamsService.save(req);
            Cart cart = cartService.getCart(req);
            cartService.add(cart, productId, quantity);
            resp.sendRedirect(req.getContextPath() + "/productList/"
                     + (!saveParams.equals("") ? saveParams + "&added=true" : "?added=true"));
        } catch (OutOfStockException | ProductNotFoundException | IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
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

    private int parseQuantity(HttpServletRequest req) {
        String quantityStr = req.getParameter("quantity");

        try {
            return NumberFormat.getNumberInstance(req.getLocale()).parse(quantityStr).intValue();
        } catch (ParseException e) {
            throw new IllegalArgumentException("Wrong number format is entered");
        }
    }
}

