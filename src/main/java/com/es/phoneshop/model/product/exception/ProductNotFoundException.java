package com.es.phoneshop.model.product.exception;

public class ProductNotFoundException extends RuntimeException {
    private final Long productId;

    public ProductNotFoundException(Long productId) {
        this.productId = productId;
    }

    public Long getProductCode() {
        return productId;
    }
}
