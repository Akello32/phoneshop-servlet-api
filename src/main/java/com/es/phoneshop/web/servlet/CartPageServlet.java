package com.es.phoneshop.web.servlet;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.exception.OutOfStockException;
import com.es.phoneshop.model.cart.service.CartService;
import com.es.phoneshop.model.cart.service.DefaultCartService;

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
        if (request.getParameter("delete") != null) {
            doDelete(request, response);
        } else {
            process(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] quantities = req.getParameterValues("quantity");
        String[] productIds = req.getParameterValues("productId");

        Map<Long, String> errors = new HashMap<>();
        for (int i = 0; i < productIds.length; i++) {
            Long productId = Long.valueOf(productIds[i]);
            int quantity;

            try {
                quantity = parseQuantity(quantities[i], req);
                if (quantity < 1) {
                    throw new IllegalArgumentException();
                }
                Cart cart = cartService.getCart(req);
                cartService.update(cart, productId, quantity);
            } catch (IllegalArgumentException | ParseException | OutOfStockException e) {
                handleError(errors, e, productId);
            }
        }

        if (errors.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/cart?updated=true");
        } else {
            req.setAttribute("errors", errors);
            process(req, resp);
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

    private void handleError(Map<Long, String> errors, Exception ex, Long productId) {
        Class<? extends Exception> exClass = ex.getClass();
        if (exClass == ParseException.class) {
            errors.put(productId, "Wrong number format is entered");
        } else if (exClass == IllegalArgumentException.class) {
            errors.put(productId, "Can't be less than one");
        } else if (exClass == OutOfStockException.class) {
            errors.put(productId, "Out of stock. Available "
                    + ((OutOfStockException) ex).getStockAvailable()
                    + ". Requested " + ((OutOfStockException) ex).getStockRequested());
        }
    }

    private int parseQuantity(String quantity, HttpServletRequest req) throws ParseException {
        return NumberFormat.getNumberInstance(req.getLocale()).parse(quantity).intValue();
    }
}

