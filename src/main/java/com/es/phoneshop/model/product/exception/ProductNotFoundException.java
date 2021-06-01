package com.es.phoneshop.model.product.exception;

public class ProductNotFoundException extends RuntimeException {
    private final Long productId;

    public ProductNotFoundException(Long productId) {
        super("Product with id " + productId + " not found");
        this.productId = productId;
    }

    public ProductNotFoundException(String message) {
        super(message);
        this.productId = -1L;
    }

    public Long getProductCode() {
        return productId;
    }
}
