package com.es.phoneshop.model.product.dao;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.dao.searchparam.SearchParams;

import java.util.List;
import java.util.Optional;

public interface ProductDao {
    Optional<Product> getProduct(Long id);

    List<Product> findProducts();

    List<Product> findProducts(SearchParams params);

    void save(Product product);

    void delete(Long id);
}
