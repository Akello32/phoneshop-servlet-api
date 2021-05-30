package com.es.phoneshop.model.product.dao;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.exception.ProductNotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public class ArrayListProductDao implements ProductDao {
    private Long maxId = 0L;
    private final List<Product> products;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public ArrayListProductDao() {
        this.products = new ArrayList<>();
    }

    @Override
    public Optional<Product> getProduct(Long id) throws ProductNotFoundException {
        return products.stream()
                .filter(p -> p.getId().equals(id))
                .findAny();
    }

    @Override
    public List<Product> findProducts() {
        return filterProductsByStockPrice(products);
    }

    @Override
    public List<Product> findProductsByQuery(String query) {
        String[] queries = query.toLowerCase().split("or");
        Comparator<Product> comparator = Comparator.comparing(p -> Arrays.stream(queries)
                .filter(q -> p.getDescription().
                        toLowerCase().contains(q.trim()))
                .count(), Comparator.reverseOrder());

        if (queries.length == 1) {
            return filterProductsByStockPrice(products.stream().
                    filter(p -> p.getDescription().toLowerCase().contains(query)).
                    collect(Collectors.toList()));
        } else {
            return filterProductsByStockPrice(products.stream()
                    .filter(p -> {
                        int counter = (int) Arrays.stream(queries)
                                .filter(q -> p.getDescription().
                                        toLowerCase().contains(q.trim()))
                                .count();
                        return counter != 0;
                    })
                    .sorted(comparator)
                    .collect(Collectors.toList())
            );
        }
    }

    private List<Product> filterProductsByStockPrice(List<Product> productList) {
        return productList.stream()
                .filter(p -> p.getPrice() != null)
                .filter(p -> p.getStock() > 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> sortedProducts(String param) {
        return param.equals("desc") ? products.stream()
                .sorted(Comparator.comparing(Product::getDescription))
                .collect(Collectors.toList())
                : products.stream()
                .sorted(Comparator.comparing(Product::getPrice))
                .collect(Collectors.toList());
    }

    @Override
    public void save(Product product) {
        lock.writeLock().lock();
        if (null == product.getId()) {
            product.setId(maxId++);
            products.add(product);
        } else {
            update(product);
        }
        lock.writeLock().unlock();
    }

    private void update(Product product) {
        int index = products.indexOf(product);
        if (index == -1) {
            throw new IllegalArgumentException();
        }
        products.set(index, product);
    }

    @Override
    public void delete(Long id) {
        lock.writeLock().lock();
        products.removeIf(p -> p.getId().equals(id));
        lock.writeLock().unlock();
    }
}