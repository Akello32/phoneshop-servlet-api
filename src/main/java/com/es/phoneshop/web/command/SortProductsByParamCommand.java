package com.es.phoneshop.web.command;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.dao.DaoFactory;
import com.es.phoneshop.model.product.dao.ProductDao;
import com.es.phoneshop.model.product.dao.searchparam.SearchParams;
import com.es.phoneshop.model.product.dao.searchparam.SortOrder;
import com.es.phoneshop.model.product.dao.searchparam.SortParam;
import com.es.phoneshop.web.service.ParseSearchParamsService;
import com.es.phoneshop.web.service.ParseSearchParamsServiceImpl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class SortProductsByParamCommand implements Command {
    private final ProductDao productDao;
    private final ParseSearchParamsService paramsService;

    public SortProductsByParamCommand() {
        paramsService = ParseSearchParamsServiceImpl.getInstance();
        productDao = DaoFactory.getInstance().getProductDaoImpl();
    }

    SortProductsByParamCommand(DaoFactory factory) {
        productDao = factory.getProductDaoImpl();
        paramsService = ParseSearchParamsServiceImpl.getInstance();
    }

    @Override
    public String execute(HttpServletRequest request, HttpServletResponse response) {
        SortOrder sortOrder = paramsService.parseSortOrder(request);
        SortParam sortParam = paramsService.parseSortParam(request);

        boolean foundOnRequest = paramsService.parseFoundOn(request);

        SearchParams params;
        List<Product> result;

        if (foundOnRequest) {
            String query = request.getParameter("query");
            params = new SearchParams(sortOrder, sortParam, query);
            request.setAttribute("foundOnRequest", true);
            request.setAttribute("query", query);
        } else {
            params = new SearchParams(sortOrder, sortParam);
        }
        result = productDao.findProducts(params);

        request.setAttribute("products", result);
        request.setAttribute("sortOrder", sortOrder);
        request.setAttribute("sortParam", sortParam);
        return "/WEB-INF/pages/productList.jsp";
    }
}
