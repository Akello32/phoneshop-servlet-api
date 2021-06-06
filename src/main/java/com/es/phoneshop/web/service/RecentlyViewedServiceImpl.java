package com.es.phoneshop.web.service;

import com.es.phoneshop.model.product.Product;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedList;

public class RecentlyViewedServiceImpl implements RecentlyViewedService{
    private RecentlyViewedServiceImpl() {}

    private static class SingletonHolder {
        public static final RecentlyViewedServiceImpl INSTANCE = new RecentlyViewedServiceImpl();
    }

    public static RecentlyViewedServiceImpl getInstance() {
        return RecentlyViewedServiceImpl.SingletonHolder.INSTANCE;
    }

    @Override
    public void addToRecentlyViewed(HttpServletRequest req, Product product) {
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
    }
}
