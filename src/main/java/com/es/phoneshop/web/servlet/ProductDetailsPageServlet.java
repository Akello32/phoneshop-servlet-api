package com.es.phoneshop.web.servlet;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.dao.DaoFactory;
import com.es.phoneshop.model.product.dao.ProductDao;
import com.es.phoneshop.model.product.exception.ProductNotFoundException;
import com.es.phoneshop.web.service.AddToCartService;
import com.es.phoneshop.web.service.AddToCartServiceImpl;
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
    private final AddToCartService addToCartService;

    public ProductDetailsPageServlet(ProductDao productDao, AddToCartService addToCartService, RecentlyViewedService recentlyViewedService) {
        this.productDao = productDao;
        this.addToCartService = addToCartService;
        this.recentlyViewedService = recentlyViewedService;
    }

    public ProductDetailsPageServlet() {
        this.productDao = DaoFactory.getInstance().getProductDaoImpl();
        this.addToCartService = AddToCartServiceImpl.getInstance();
        this.recentlyViewedService = RecentlyViewedServiceImpl.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        process(req, resp, null);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Long productId = Optional.of(parseProductId(req)).get();
        int quantity;
        try {
            quantity = parseQuantity(req);
            if (addToCartService.add(req, quantity, productId)) {
                resp.sendRedirect(req.getContextPath() + "/products/" + productId + "?added=true");
            } else {
                process(req, resp, productId);
            }
        } catch (ParseException e) {
            req.setAttribute("error", "Wrong number format is entered");
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

    private int parseQuantity(HttpServletRequest req) throws ParseException {
        String quantityStr = req.getParameter("quantity");
        return NumberFormat.getNumberInstance(req.getLocale()).parse(quantityStr).intValue();
    }
}