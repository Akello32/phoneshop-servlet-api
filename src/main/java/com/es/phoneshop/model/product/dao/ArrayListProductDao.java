package com.es.phoneshop.model.product.dao;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.dao.searchparam.SearchParams;
import com.es.phoneshop.model.product.dao.searchparam.SortOrder;
import com.es.phoneshop.model.product.dao.searchparam.SortParam;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

class ArrayListProductDao implements ProductDao {
    private Long maxId = 0L;
    private final List<Product> products = new ArrayList<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Map<SortParam, Comparator<Product>> sortingFunctions;

    ArrayListProductDao(Map<SortParam, Comparator<Product>> sortingFunctions) {
        this.sortingFunctions = sortingFunctions;
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
    public List<Product> findProducts(SearchParams params) {
        String query = params.getQuery();
        SortParam param = params.getParam();
        SortOrder order = params.getOrder();
        if (query == null && param == null) {
            return filterProductsByStockPrice(products);
        } else if (query != null && !query.trim().isEmpty() && param == null) {
            return filterProductsByQuery(query, null, null);
        } else if (param != null && query == null) {
            return sortedProducts(param, order);
        } else {
            return filterProductsByQuery(query, param, order);
        }
    }

    private List<Product> filterProductsByQuery(String query, SortParam param, SortOrder order) {
        Comparator<Product> comparator;
        String[] queries = query.toLowerCase().trim().split("\\s+");
        Map<Product, Long> countMatchesMap = new HashMap<>();
        List<Product> result;

        products.forEach(p -> {
            int counter = (int) Arrays.stream(queries)
                    .filter(q -> p.getDescription().toLowerCase().contains(q.trim()))
                    .count();
            if (counter != 0) {
                countMatchesMap.put(p, (long) counter);
            }
        });

        if (param == null || !sortingFunctions.containsKey(param)) {
            result = countMatchesMap.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            Collections.reverse(result);
        } else {
            comparator = sortingFunctions.get(param);
            if (SortOrder.DESCEND == order) {
                comparator = comparator.reversed();
            }
            result = new ArrayList<>(countMatchesMap.keySet());
            result.sort(comparator);
        }

        return filterProductsByStockPrice(result);
    }

    private List<Product> filterProductsByStockPrice(List<Product> productList) {
        return productList.stream()
                .filter(p -> p.getPrice() != null)
                .filter(p -> p.getStock() > 0)
                .collect(Collectors.toList());
    }

    private List<Product> sortedProducts(SortParam param, SortOrder order) {
        Comparator<Product> comparator = sortingFunctions.getOrDefault(param, Comparator.comparing(Product::getId));

        if (SortOrder.DESCEND == order) {
            comparator = comparator.reversed();
        }

        List<Product> result = products.stream().sorted(comparator)
                .collect(Collectors.toList());

        return filterProductsByStockPrice(result);
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