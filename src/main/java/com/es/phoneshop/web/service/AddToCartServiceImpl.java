package com.es.phoneshop.web.service;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.exception.OutOfStockException;
import com.es.phoneshop.model.cart.service.CartService;
import com.es.phoneshop.model.cart.service.DefaultCartService;
import com.es.phoneshop.model.product.exception.ProductNotFoundException;

import javax.servlet.http.HttpServletRequest;

public class AddToCartServiceImpl implements AddToCartService {
    private CartService cartService = DefaultCartService.getInstance();

    private AddToCartServiceImpl() {
    }

    private static class SingletonHolder {
        public static final AddToCartServiceImpl INSTANCE = new AddToCartServiceImpl();
    }

    public static AddToCartServiceImpl getInstance() {
        return AddToCartServiceImpl.SingletonHolder.INSTANCE;
    }

    @Override
    public boolean add(HttpServletRequest req, int quantity, Long productId) {
        try {
            if (quantity < 1) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Wrong number format is entered");
            return false;
        }

        try {
            Cart cart = cartService.getCart(req);
            cartService.add(cart, productId, quantity);
        } catch (OutOfStockException e) {
            req.setAttribute("error", "Out of stock. Available "
                    + e.getStockAvailable() + ". Requested " + e.getStockRequested());
            return false;
        } catch (ProductNotFoundException e) {
            req.setAttribute("error", "Product with id " + e.getProductCode() + " not found :<\n ");
            return false;
        }

        return true;
    }
}
