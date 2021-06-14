package com.es.phoneshop.web.service;

import com.es.phoneshop.model.product.dao.searchparam.SortOrder;
import com.es.phoneshop.model.product.dao.searchparam.SortParam;

import javax.servlet.http.HttpServletRequest;

public interface ParseSearchParamsService {
    SortOrder parseSortOrder(HttpServletRequest request);

    SortParam parseSortParam(HttpServletRequest request);

    boolean parseFoundOn(HttpServletRequest request);
}
