package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.exception.OutOfStockException;
import com.es.phoneshop.model.cart.service.CartService;
import com.es.phoneshop.model.cart.service.DefaultCartService;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.dao.DaoFactory;
import com.es.phoneshop.model.product.dao.ProductDao;
import com.es.phoneshop.model.product.exception.ProductNotFoundException;
import com.es.phoneshop.web.service.RecentlyViewedService;
import com.es.phoneshop.web.service.RecentlyViewedServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Optional;


public class ProductDetailsPageServlet extends HttpServlet {
    private final ProductDao productDao = DaoFactory.getInstance().getProductDaoImpl();
    private final CartService cartService = DefaultCartService.getInstance();
    private final RecentlyViewedService recentlyViewedService = RecentlyViewedServiceImpl.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp, Optional.ofNullable(null));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Optional<Long> productId = Optional.of(parseProductId(req));
        int quantity;

        try {
            quantity = parseQuantity(req);
            if (quantity < 1) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException | ParseException e) {
            req.setAttribute("error", "Wrong number format is entered");
            process(req, resp, productId);
            return;
        }

        try {
            Cart cart = cartService.getCart(req);
            cartService.add(cart, productId.get(), quantity);
        } catch (OutOfStockException e) {
            req.setAttribute("error", "Out of stock. Available "
                    + e.getStockAvailable() + ". Requested " + e.getStockRequested());
            process(req, resp, productId);
            return;
        }

        resp.sendRedirect(req.getContextPath() + "/products/" + productId.get() + "?added=true");
    }

    private void process(HttpServletRequest req, HttpServletResponse resp, Optional<Long> productId) {
        try {
            Long finalProductId = productId.orElse(parseProductId(req));
            Product product = productDao.getProduct(finalProductId)
                    .orElseThrow(() -> new ProductNotFoundException(finalProductId));
            req.setAttribute("product", product);
            req.setAttribute("cart", cartService.getCart(req));

            recentlyViewedService.addToRecentlyViewed(req, product);

            req.getRequestDispatcher("/WEB-INF/pages/product.jsp").forward(req, resp);

        } catch (NumberFormatException | ServletException | IOException ex) {
            throw new ProductNotFoundException("Invalid product id format");
        }
    }

    private Long parseProductId(HttpServletRequest req) {
        return Long.valueOf(req.getPathInfo().substring(1));
    }

    private int parseQuantity(HttpServletRequest req) throws ParseException {
        String quantityStr = req.getParameter("quantity");
        return NumberFormat.getNumberInstance(req.getLocale()).parse(quantityStr).intValue();
    }
}