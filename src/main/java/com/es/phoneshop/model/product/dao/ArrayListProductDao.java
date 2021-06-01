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

import static java.util.Comparator.reverseOrder;

class ArrayListProductDao implements ProductDao {
    private Long maxId = 0L;
    private final List<Product> products = new ArrayList<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    ArrayListProductDao() {
    }

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
        Function<Product, Long> countMatches = p -> Arrays.stream(queries)
                .filter(q -> p.getDescription().toLowerCase().contains(q.trim()))
                .count();
        Comparator<Product> comparator = Comparator.comparing(countMatches, reverseOrder());

        return filterProductsByStockPrice(products.stream()
                .filter(p -> {
                    int counter = (int) Arrays.stream(queries)
                            .filter(q -> p.getDescription().toLowerCase().contains(q.trim()))
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
    public List<Product> sortedProducts(List<Product> productList, String param, String order) {
        Map<String, Comparator<Product>> sortingFunctions = new HashMap<>();
        sortingFunctions.put("default", Comparator.comparing(Product::getId));
        sortingFunctions.put("desc", Comparator.comparing(Product::getDescription));
        sortingFunctions.put("price", Comparator.comparing(Product::getPrice));

        Comparator<Product> comparator;
        if (sortingFunctions.containsKey(param)) {
            comparator = sortingFunctions.get(param);
            if ("descend".equals(order)) {
                comparator = sortingFunctions.get(param).reversed();
            }
        } else {
            comparator = sortingFunctions.get("default");
        }

        if (productList == null) {
            List<Product> result = products.stream().sorted(comparator)
                    .collect(Collectors.toList());

            return filterProductsByStockPrice(result);
        } else {
            productList.sort(comparator);
            return filterProductsByStockPrice(productList);
        }
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