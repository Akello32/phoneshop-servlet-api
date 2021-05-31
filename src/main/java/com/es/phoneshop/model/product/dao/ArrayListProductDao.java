package com.es.phoneshop.model.product.dao;

import com.es.phoneshop.model.product.Product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.stream.Collectors;

class ArrayListProductDao implements ProductDao {
    private Long maxId = 0L;
    private final List<Product> products = new ArrayList<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    ArrayListProductDao() {}

    @Override
    public Optional<Product> getProduct(Long id) {
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
        String[] queries = query.toLowerCase().trim().split("\\s+");
        Function<Product, Long> function = p -> Arrays.stream(queries)
                .filter(q -> p.getDescription().toLowerCase().contains(q.trim()))
                .count();
        Comparator<Product> comparator = Comparator.comparing(function, Comparator.reverseOrder());

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

    private List<Product> filterProductsByStockPrice(List<Product> productList) {
        return productList.stream()
                .filter(p -> p.getPrice() != null)
                .filter(p -> p.getStock() > 0)
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> sortedProducts(String param, String order) {
        Map<String, Function<Product, Object>> sortingFunctions = new HashMap<>();
        sortingFunctions.put("desc", Product::getDescription);
        sortingFunctions.put("price", Product::getPrice);

        Function function = sortingFunctions.get(param);
        Comparator<Product> comparator = Comparator.comparing(function);
        if (order.equals("descend")) {
            comparator = Comparator.comparing(function, Comparator.reverseOrder());
        }

        return filterProductsByStockPrice(products.stream()
                                                  .sorted(comparator)
                                                  .collect(Collectors.toList()));
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