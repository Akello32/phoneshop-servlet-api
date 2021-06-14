package com.es.phoneshop.web.service;

import com.es.phoneshop.model.product.dao.searchparam.SortOrder;
import com.es.phoneshop.model.product.dao.searchparam.SortParam;

import javax.servlet.http.HttpServletRequest;

public class ParseSearchParamsServiceImpl implements ParseSearchParamsService {
    private ParseSearchParamsServiceImpl() {
    }

    private static class SingletonHolder {
        public static final ParseSearchParamsServiceImpl INSTANCE = new ParseSearchParamsServiceImpl();
    }

    public static ParseSearchParamsServiceImpl getInstance() {
        return ParseSearchParamsServiceImpl.SingletonHolder.INSTANCE;
    }

    @Override
    public SortOrder parseSortOrder(HttpServletRequest request) {
        try {
            return SortOrder.valueOf(request.getParameter("order").toUpperCase());
        } catch (IllegalArgumentException ex) {
            return SortOrder.ASCEND;
        }
    }

    @Override
    public SortParam parseSortParam(HttpServletRequest request) {
        try {
            return SortParam.valueOf(request.getParameter("sortParam").toUpperCase());
        } catch (IllegalArgumentException ex) {
            return SortParam.DEFAULT;
        }
    }

    @Override
    public boolean parseFoundOn(HttpServletRequest request) {
        return Boolean.parseBoolean(request.getParameter("foundOnRequest"));
    }
}
