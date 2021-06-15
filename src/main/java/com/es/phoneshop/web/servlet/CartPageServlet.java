package com.es.phoneshop.web.servlet;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.exception.OutOfStockException;
import com.es.phoneshop.model.cart.service.CartService;
import com.es.phoneshop.model.cart.service.DefaultCartService;
import com.es.phoneshop.model.product.exception.ProductNotFoundException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class CartPageServlet extends HttpServlet {
    private final CartService cartService;

    public CartPageServlet(CartService cartService) {
        this.cartService = cartService;
    }

    public CartPageServlet() {
        cartService = DefaultCartService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getParameter("delete") != null) {
            doDelete(req, resp);
        } else {
            String[] quantities = req.getParameterValues("quantity");
            String[] productIds = req.getParameterValues("productId");

            Map<Long, String> errors = new HashMap<>();
            for (int i = 0; i < productIds.length; i++) {
                Long productId = Long.valueOf(productIds[i]);

                try {
                    int quantity = parseQuantity(quantities[i], req);
                    Cart cart = cartService.getCart(req);
                    cartService.update(cart, productId, quantity);
                } catch (OutOfStockException | ProductNotFoundException | IllegalArgumentException e) {
                    errors.put(productId, e.getMessage());
                }
            }

            if (errors.isEmpty()) {
                resp.sendRedirect(req.getContextPath() + "/cart?updated=true");
            } else {
                req.setAttribute("errors", errors);
                process(req, resp);
            }
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Long productId = Long.valueOf(req.getParameter("deletingProduct"));
            cartService.delete(cartService.getCart(req), productId);
        } catch (NumberFormatException e) {
            req.setAttribute("deleteCartItemError", "Wrong product id format");
            process(req, resp);
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/cart?deleted=true");
    }

    private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/pages/cart.jsp").forward(request, response);
    }

    private int parseQuantity(String quantity, HttpServletRequest req) {
        try {
            return NumberFormat.getNumberInstance(req.getLocale()).parse(quantity).intValue();
        } catch (ParseException e) {
            throw new IllegalArgumentException("Wrong number format is entered");
        }
    }
}

