package com.es.phoneshop.model.product.dao;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.exception.ProductNotFoundException;

import java.util.List;
import java.util.Optional;

public interface ProductDao {
    Optional<Product> getProduct(Long id) throws ProductNotFoundException;

    List<Product> findProducts();

    List<Product> sortedProducts(String param, String order);

    List<Product> findProductsByQuery(String query);

    void save(Product product);

    void delete(Long id);
}
