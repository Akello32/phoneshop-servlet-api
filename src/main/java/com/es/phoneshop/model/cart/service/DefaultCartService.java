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
    private ProductDao productDao = DaoFactory.getInstance().getProductDaoImpl();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private DefaultCartService() {}

    private static class SingletonHolder {
        public static final DefaultCartService INSTANCE = new DefaultCartService();
    }

    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
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
        Optional<CartItem> cartItem = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findAny();
        if (cartItem.isPresent()) {
            update(cart, cartItem.get(), quantity);
        } else {
            Product product = productDao.getProduct(productId).orElseThrow(() -> new ProductNotFoundException(productId));
            cart.getItems().add(new CartItem(product, quantity));
        }
        lock.writeLock().unlock();
    }

    private void update(Cart cart, CartItem cartItem, int quantity) throws OutOfStockException {
        int index = cart.getItems().indexOf(cartItem);
        if (index == -1) {
            throw new IllegalArgumentException();
        }
        int sumQuantity = cartItem.getQuantity() + quantity;
        if (cartItem.getProduct().getStock() < sumQuantity) {
            lock.writeLock().unlock();
            throw new OutOfStockException(cartItem.getProduct(), sumQuantity, cartItem.getProduct().getStock());
        }
        cart.getItems().set(index, new CartItem(cartItem.getProduct(), sumQuantity));
    }
}