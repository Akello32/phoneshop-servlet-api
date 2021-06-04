package com.es.phoneshop.web;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.exception.OutOfStockException;
import com.es.phoneshop.model.cart.service.CartService;
import com.es.phoneshop.model.cart.service.DefaultCartService;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.dao.DaoFactory;
import com.es.phoneshop.model.product.dao.ProductDao;
import com.es.phoneshop.model.product.exception.ProductNotFoundException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.Optional;


public class ProductDetailsPageServlet extends HttpServlet {
    private final ProductDao productDao = DaoFactory.getInstance().getProductDaoImpl();
    private final CartService cartService = DefaultCartService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp, Optional.ofNullable(null));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Optional<Long> productId = Optional.of(parseProductId(req));
        String quantityStr = req.getParameter("quantity");
        int quantity;

        try {
            quantity = NumberFormat.getNumberInstance(req.getLocale()).parse(quantityStr).intValue();
            if (quantity < 1) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException | ParseException e) {
            req.setAttribute("error", "Incorrect number format");
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

        resp.sendRedirect(req.getContextPath() + "/products/" + productId.get() + "?message=Product added to cart");
    }

    private void process(HttpServletRequest req, HttpServletResponse resp, Optional<Long> productId) {
        try {
            Long finalProductId = productId.orElse(parseProductId(req));
            Product product = productDao.getProduct(finalProductId)
                    .orElseThrow(() -> new ProductNotFoundException(finalProductId));
            req.setAttribute("product", product);
            req.setAttribute("cart", cartService.outPutCart(cartService.getCart(req)));

            LinkedList<Product> recentlyViewedProduct = (LinkedList<Product>) req.getSession().getAttribute("recentlyViewedProduct");
            if (recentlyViewedProduct == null) {
                recentlyViewedProduct = new LinkedList<>();
                recentlyViewedProduct.add(product);
                req.getSession().setAttribute("recentlyViewedProduct", recentlyViewedProduct);
            } else if (!recentlyViewedProduct.contains(product) || recentlyViewedProduct.getFirst().equals(product)) {
                if (recentlyViewedProduct.size() < 4 && !recentlyViewedProduct.getFirst().equals(product)) {
                    recentlyViewedProduct.add(product);
                } else {
                    recentlyViewedProduct.removeFirst();
                    recentlyViewedProduct.addLast(product);
                }
            }

            req.getRequestDispatcher("/WEB-INF/pages/product.jsp").forward(req, resp);

        } catch (NumberFormatException | ServletException | IOException ex) {
            throw new ProductNotFoundException("Invalid product id format");
        }
    }

    private Long parseProductId(HttpServletRequest req) {
        return Long.valueOf(req.getPathInfo().substring(1));
    }
}