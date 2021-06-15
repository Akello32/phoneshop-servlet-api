package com.es.phoneshop.web.servlet;

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
    private final ProductDao productDao;
    private final RecentlyViewedService recentlyViewedService;
    private final CartService cartService;

    public ProductDetailsPageServlet(ProductDao productDao, RecentlyViewedService recentlyViewedService, CartService cartService) {
        this.productDao = productDao;
        this.recentlyViewedService = recentlyViewedService;
        this.cartService = cartService;
    }

    public ProductDetailsPageServlet() {
        this.productDao = DaoFactory.getInstance().getProductDaoImpl();
        this.recentlyViewedService = RecentlyViewedServiceImpl.getInstance();
        this.cartService = DefaultCartService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp, null);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long productId = Optional.of(parseProductId(req)).get();
        try {
            int quantity = parseQuantity(req);
            Cart cart = cartService.getCart(req);
            cartService.add(cart, productId, quantity);
            resp.sendRedirect(req.getContextPath() + "/products/" + productId + "?added=true");
        } catch (OutOfStockException | ProductNotFoundException | IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            process(req, resp, productId);
        }
    }

    private void process(HttpServletRequest req, HttpServletResponse resp, Long id) {
        try {
            Optional<Long> productId = Optional.ofNullable(id);
            Product product = productDao.getProduct(productId.orElse(parseProductId(req)))
                    .orElseThrow(() -> new ProductNotFoundException(productId.orElse(parseProductId(req))));
            req.setAttribute("product", product);

            recentlyViewedService.addToRecentlyViewed(req, product);

            req.getRequestDispatcher("/WEB-INF/pages/product.jsp").forward(req, resp);

        } catch (NumberFormatException | ServletException | IOException ex) {
            throw new ProductNotFoundException("Invalid product id format");
        }
    }

    private Long parseProductId(HttpServletRequest req) {
        return Long.valueOf(req.getPathInfo().substring(1));
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