package com.es.phoneshop.model.product.dao;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.dao.searchparam.SortParam;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;

public class DaoFactory {
    private static final DaoFactory INSTANCE = new DaoFactory();
    private final Map<SortParam, Comparator<Product>> sortingFunctions = new EnumMap<>(SortParam.class);

    private final ProductDao productDaoImpl = new ArrayListProductDao(sortingFunctions);

    private DaoFactory() {
        sortingFunctions.put(SortParam.DEFAULT, Comparator.comparing(Product::getId));
        sortingFunctions.put(SortParam.DESC, Comparator.comparing(Product::getDescription));
        sortingFunctions.put(SortParam.PRICE, Comparator.comparing(Product::getPrice));
    }

    public static DaoFactory getInstance() {
        return INSTANCE;
    }

    public ProductDao getProductDaoImpl() {
        return productDaoImpl;
    }
}