package com.es.phoneshop.web.service;

import com.es.phoneshop.model.product.Product;

import javax.servlet.http.HttpServletRequest;

public interface RecentlyViewedService {
    public void addToRecentlyViewed(HttpServletRequest request, Product product);
}
