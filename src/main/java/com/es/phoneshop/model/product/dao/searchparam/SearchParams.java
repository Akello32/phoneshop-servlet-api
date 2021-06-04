package com.es.phoneshop.model.product.dao.searchparam;

public class SearchParams {
    private SortOrder order;
    private SortParam param;
    private final String query;

    public SearchParams(SortOrder order, SortParam param, String query) {
        this.order = order;
        this.param = param;
        this.query = query;
    }

    public SearchParams(SortOrder order, SortParam param) {
        this(order, param, null);
    }

    public SearchParams(String query) {
        this.query = query;
    }

    public SortOrder getOrder() {
        return order;
    }

    public SortParam getParam() {
        return param;
    }

    public String getQuery() {
        return query;
    }
}
