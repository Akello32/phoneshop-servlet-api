package com.es.phoneshop.model.product.dao;

public final class DaoFactory {
    private static final DaoFactory INSTANCE = new DaoFactory();

    private final ArrayListProductDao productDaoImpl = new ArrayListProductDao();

    private DaoFactory() {}

    public static DaoFactory getInstance() {
        return INSTANCE;
    }

    public ArrayListProductDao getProductDaoImpl() {
        return productDaoImpl;
    }
}
