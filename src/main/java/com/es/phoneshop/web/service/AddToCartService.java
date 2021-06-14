package com.es.phoneshop.web.service;

import javax.servlet.http.HttpServletRequest;

public interface AddToCartService {
    boolean add(HttpServletRequest request, int quantity, Long productId);
}
