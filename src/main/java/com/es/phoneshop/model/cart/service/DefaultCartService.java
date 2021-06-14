package com.es.phoneshop.model.cart.service;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.CartItem;
import com.es.phoneshop.model.cart.exception.OutOfStockException;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.dao.DaoFactory;
import com.es.phoneshop.model.product.dao.ProductDao;
import com.es.phoneshop.model.product.exception.ProductNotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DefaultCartService implements CartService {
    private static final String CART_SESSION_ATTRIBUTE = DefaultCartService.class.getName() + ".cart";
    private ProductDao productDao = DaoFactory.getInstance().getProductDaoImpl();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private DefaultCartService() {
    }

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
    public void update(Cart cart, Long productId, int quantity) throws OutOfStockException {
        lock.writeLock().lock();
        Optional<CartItem> cartItem = getCartItem(cart, productId);

        if (cartItem.isPresent() && cartItem.get().getQuantity() != quantity) {
            if (quantity < cartItem.get().getProduct().getStock()) {
                CartItem updCartItem = new CartItem(cartItem.get().getProduct(), quantity);
                cart.getItems().set(getIndex(cart, cartItem.get()), updCartItem);
            } else {
                lock.writeLock().unlock();
                throw new OutOfStockException(cartItem.get().getProduct(), cartItem.get().getProduct().getStock(), quantity);
            }
        }
        recalculateCart(cart);
        calcTotalCost(cart);
        lock.writeLock().unlock();
    }

    @Override
    public void delete(Cart cart, Long productId) {
        lock.writeLock().lock();
        cart.getItems().removeIf(i -> i.getProduct().getId().equals(productId));
        recalculateCart(cart);
        calcTotalCost(cart);
        lock.writeLock().unlock();
    }

    @Override
    public void add(Cart cart, Long productId, int quantity) throws OutOfStockException {
        lock.writeLock().lock();
        Optional<CartItem> cartItem = getCartItem(cart, productId);
        if (cartItem.isPresent()) {
            addToExistingProduct(cart, cartItem.get(), quantity);
        } else {
            Product product = productDao.getProduct(productId).orElseThrow(() -> new ProductNotFoundException(productId));
            if (product.getStock() < quantity) {
                lock.writeLock().unlock();
                throw new OutOfStockException(product, quantity, product.getStock());
            }
            cart.getItems().add(new CartItem(product, quantity));
        }
        recalculateCart(cart);
        calcTotalCost(cart);
        lock.writeLock().unlock();
    }

    private void addToExistingProduct(Cart cart, CartItem cartItem, int quantity) throws OutOfStockException {
        int sumQuantity = cartItem.getQuantity() + quantity;
        if (cartItem.getProduct().getStock() < sumQuantity) {
            lock.writeLock().unlock();
            throw new OutOfStockException(cartItem.getProduct(), sumQuantity, cartItem.getProduct().getStock());
        }
        cart.getItems().set(getIndex(cart, cartItem), new CartItem(cartItem.getProduct(), sumQuantity));
    }

    private void recalculateCart(Cart cart) {
        cart.setTotalQuantity(cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum());
    }

    private void calcTotalCost(Cart cart) {
        int totalCost = 0;
        for (CartItem i : cart.getItems()) {
            totalCost += i.getQuantity() * i.getProduct().getPrice().intValue();
        }
        cart.setTotalCost(BigDecimal.valueOf(totalCost));
    }

    private Optional<CartItem> getCartItem(Cart cart, Long productId) {
        return cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findAny();
    }

    private int getIndex(Cart cart, CartItem cartItem) {
        int index = cart.getItems().indexOf(cartItem);
        if (index == -1) {
            throw new IllegalArgumentException();
        }
        return index;
    }
}