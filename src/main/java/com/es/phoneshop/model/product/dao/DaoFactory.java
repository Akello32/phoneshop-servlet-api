package com.es.phoneshop.model.product.dao;

public final class DaoFactory {
    private static final DaoFactory INSTANCE = new DaoFactory();

    private final ProductDao productDaoImpl = new ArrayListProductDao();

    private DaoFactory() {}

    public static DaoFactory getInstance() {
        return INSTANCE;
    }

    public ProductDao getProductDaoImpl() {
        return productDaoImpl;
    }
}
