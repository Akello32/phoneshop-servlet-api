package com.es.phoneshop.model.product.dao;

import com.es.phoneshop.model.general.dao.AbstractDao;
import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.dao.searchparam.SearchParams;
import com.es.phoneshop.model.product.dao.searchparam.SortOrder;
import com.es.phoneshop.model.product.dao.searchparam.SortParam;
import com.es.phoneshop.model.product.dao.searchparam.advancedsearch.AdvancedParam;
import com.es.phoneshop.model.product.dao.searchparam.advancedsearch.DescParam;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

class ArrayListProductDao extends AbstractDao<Product> implements ProductDao {
    private final Map<SortParam, Comparator<Product>> sortingFunctions;

    ArrayListProductDao(Map<SortParam, Comparator<Product>> sortingFunctions) {
        this.sortingFunctions = sortingFunctions;
    }

    @Override
    public Optional<Product> getProduct(Long id) {
        return getItem(id);
    }

    @Override
    public List<Product> findProducts() {
        return filterProductsByStockPrice(getItemList());
    }

    @Override
    public List<Product> findProducts(SearchParams params) {
        String query = params.getQuery();
        SortParam param = params.getParam();
        SortOrder order = params.getOrder();
        if (query == null && param == null) {
            return filterProductsByStockPrice(getItemList());
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

        if (query == null) {
            return getItemList();
        }

        String[] queries = query.toLowerCase().trim().split("\\s+");
        Map<Product, Long> countMatchesMap = new HashMap<>();
        List<Product> result;

        getItemList().forEach(p -> {
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

    @Override
    public List<Product> advancedSearchProducts(AdvancedParam params) {
        String desc = params.getDesc();
        BigDecimal minPrice = params.getMinPrice();
        BigDecimal maxPrice = params.getMaxPrice();
        DescParam descParam = params.getDescParam();

        List<Product> result;
        if (descParam == null || descParam.toString().equals("ANY_WORDS")) {
            result = filterProductsByQuery(desc, null, null);
        } else {
            result = filterProductsByDescByAllWord(desc);
        }

        if (maxPrice != null && minPrice != null) {
            return filterProductsByPrice(maxPrice, minPrice, result);
        } else if (maxPrice == null && minPrice == null) {
            return result;
        } else if (maxPrice == null) {
            return filterProductsByMinPrice(minPrice, result);
        } else {
            return filterProductsByMaxPrice(maxPrice, result);
        }
    }

    private List<Product> filterProductsByDescByAllWord(String desc) {
        if (desc == null) {
            return getItemList();
        }

        return getItemList().stream()
                .filter(p -> p.getDescription().toLowerCase().contains(desc.toLowerCase()))
                .collect(Collectors.toList());
    }

    private List<Product> filterProductsByPrice(BigDecimal maxPrice, BigDecimal minPrice, List<Product> products) {
        return products.stream()
                .filter(p -> {
                    BigDecimal price = p.getPrice();
                    int compareMax = price.compareTo(maxPrice);
                    int compareMin = price.compareTo(minPrice);
                    return compareMax == -1 || compareMax == 0 && compareMin == 1 || compareMin == 0;
                })
                .collect(Collectors.toList());
    }

    private List<Product> filterProductsByMinPrice(BigDecimal minPrice, List<Product> products) {
        return products.stream()
                .filter(p -> {
                    int compareMin = p.getPrice().compareTo(minPrice);
                    return compareMin == 1 || compareMin == 0;
                })
                .collect(Collectors.toList());
    }

    private List<Product> filterProductsByMaxPrice(BigDecimal maxPrice, List<Product> products) {
        return products.stream()
                .filter(p -> {
                    int compareMax = p.getPrice().compareTo(maxPrice);
                    return compareMax == -1 || compareMax == 0;
                })
                .collect(Collectors.toList());
    }

    private List<Product> sortedProducts(SortParam param, SortOrder order) {
        Comparator<Product> comparator = sortingFunctions.getOrDefault(param, Comparator.comparing(Product::getId));

        if (SortOrder.DESCEND == order) {
            comparator = comparator.reversed();
        }

        List<Product> result = getItemList().stream().sorted(comparator)
                .collect(Collectors.toList());

        return filterProductsByStockPrice(result);
    }
}