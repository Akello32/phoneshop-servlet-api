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

    private void validateQuantity(int quantity, Product product) throws OutOfStockException {
        if (quantity < 1) {
            throw new IllegalArgumentException("Can't be less than 1");
        } else if (quantity > product.getStock()) {
            throw new OutOfStockException(product, quantity, product.getStock());
        }
    }

    @Override
    public void update(Cart cart, Long productId, int quantity) throws OutOfStockException {
        lock.writeLock().lock();
        Optional<CartItem> cartItem = getCartItem(cart, productId);

        if (cartItem.isPresent() && cartItem.get().getQuantity() != quantity) {
            try {
                validateQuantity(quantity, cartItem.get().getProduct());
                CartItem updCartItem = new CartItem(cartItem.get().getProduct(), quantity);
                cart.getItems().set(getIndex(cart, cartItem.get()), updCartItem);
            } catch (IllegalArgumentException | OutOfStockException e) {
                lock.writeLock().unlock();
                throw e;
            }
        }

        recalculateProductInCart(cart);
        calculateTotalCost(cart);
        lock.writeLock().unlock();
    }

    @Override
    public void delete(Cart cart, Long productId) {
        lock.writeLock().lock();
        cart.getItems().removeIf(i -> i.getProduct().getId().equals(productId));
        recalculateProductInCart(cart);
        calculateTotalCost(cart);
        lock.writeLock().unlock();
    }

    @Override
    public void add(Cart cart, Long productId, int quantity) throws OutOfStockException {
        lock.writeLock().lock();
        Optional<CartItem> cartItem = getCartItem(cart, productId);
        if (cartItem.isPresent()) {
            try {
                validateQuantity(quantity, cartItem.get().getProduct());
                update(cart, productId, cartItem.get().getQuantity() + quantity);
            } catch (IllegalArgumentException | OutOfStockException e) {
                lock.writeLock().unlock();
                throw e;
            }
        } else {
            Product product = productDao.getProduct(productId).orElseThrow(() -> {
                lock.writeLock().unlock();
                return new ProductNotFoundException(productId);
            });
            try {
                validateQuantity(quantity, product);
                cart.getItems().add(new CartItem(product, quantity));
            } catch (IllegalArgumentException | OutOfStockException e) {
                lock.writeLock().unlock();
                throw e;
            }
        }
        recalculateProductInCart(cart);
        calculateTotalCost(cart);
        lock.writeLock().unlock();
    }

    private void recalculateProductInCart(Cart cart) {
        cart.setTotalQuantity(cart.getItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum());
    }

    private void calculateTotalCost(Cart cart) {
        BigDecimal totalCost = cart.getItems().stream()
                .reduce(BigDecimal.ZERO, (q, p) -> BigDecimal.valueOf(p.getQuantity())
                        .multiply(p.getProduct().getPrice())
                        .add(q), BigDecimal::add);

        cart.setTotalCost(totalCost);
    }

    private Optional<CartItem> getCartItem(Cart cart, Long productId) {
        return cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findAny();
    }

    private int getIndex(Cart cart, CartItem cartItem) {
        int index = cart.getItems().indexOf(cartItem);
        if (index == -1) {
            throw new IllegalArgumentException("Wrong index format");
        }
        return index;
    }
}