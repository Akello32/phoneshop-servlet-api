package com.es.phoneshop.model.cart.service;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.cart.exception.OutOfStockException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.dao.DaoFactory;
import com.es.phoneshop.model.product.dao.ProductDao;
import com.es.phoneshop.model.product.exception.ProductNotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DefaultCartService implements CartService {
    private static final String CART_SESSION_ATTRIBUTE = DefaultCartService.class.getName() + ".cart";
    private final ProductDao productDao = DaoFactory.getInstance().getProductDaoImpl();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private DefaultCartService() {}

    private static class SingletonHolder {
        public static final DefaultCartService INSTANCE = new DefaultCartService();
    }

    public static DefaultCartService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    @Override
    public Cart getCart(HttpServletRequest request) {
        lock.writeLock().lock();
        Cart cart = (Cart) request.getSession().getAttribute(CART_SESSION_ATTRIBUTE);
        if (cart == null) {
            cart = new Cart();
            request.getSession().setAttribute(CART_SESSION_ATTRIBUTE, cart);
        }
        lock.writeLock().unlock();
        return cart;
    }

    @Override
    public void add(Cart cart, Long productId, int quantity) throws OutOfStockException {
        lock.writeLock().lock();
        Optional<Product> productOpt = productDao.getProduct(productId);
        Product product = productOpt.orElseThrow(() -> new ProductNotFoundException(productId));
        int sumQuantity = quantityOfRequestedProduct(cart, product) + quantity;
        if (product.getStock() < sumQuantity) {
            throw new OutOfStockException(product, sumQuantity, product.getStock());
        }
        cart.getItems().add(new CartItem(product, quantity));
        lock.writeLock().unlock();
    }

    @Override
    public String outPutCart(Cart cart) {
        StringBuilder result = new StringBuilder();
        cart.getItems().stream()
                .map(CartItem::getProduct).distinct()
                .forEach(p -> result.append(p.getCode())
                        .append(" : ")
                        .append(quantityOfRequestedProduct(cart, p))
                        .append(";  "));
        return result.toString();
    }

    private int quantityOfRequestedProduct(Cart cart, Product product) {
        return cart.getItems().stream().filter(p -> p.getProduct().equals(product))
                .map(CartItem::getQuantity).mapToInt(p -> p).sum();
    }
}
