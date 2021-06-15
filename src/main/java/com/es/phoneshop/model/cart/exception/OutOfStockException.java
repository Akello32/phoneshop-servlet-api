package com.es.phoneshop.model.cart.exception;

import com.es.phoneshop.model.product.Product;

public class OutOfStockException extends Exception {
    private final Product product;
    private final int stockRequested;
    private final int stockAvailable;

    public OutOfStockException(Product product, int stockRequested, int stockAvailable) {
        super("Out of stock. Available " + stockAvailable + ". Requested " + stockRequested);
        this.product = product;
        this.stockRequested = stockRequested;
        this.stockAvailable = stockAvailable;
    }

    public Product getProduct() {
        return product;
    }

    public int getStockRequested() {
        return stockRequested;
    }

    public int getStockAvailable() {
        return stockAvailable;
    }
}
