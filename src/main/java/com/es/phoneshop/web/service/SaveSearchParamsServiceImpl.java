package com.es.phoneshop.web.service;

import com.es.phoneshop.web.command.Command;
import com.es.phoneshop.web.command.FindProductByQueryCommand;
import com.es.phoneshop.web.command.SortProductsByParamCommand;

import javax.servlet.http.HttpServletRequest;

public class SaveSearchParamsServiceImpl implements SaveSearchParamsService {
    private final ParseSearchParamsServiceImpl paramsService = ParseSearchParamsServiceImpl.getInstance();

    private SaveSearchParamsServiceImpl() {}

    private static class SingletonHolder {
        public static final SaveSearchParamsServiceImpl INSTANCE = new SaveSearchParamsServiceImpl();
    }

    public static SaveSearchParamsServiceImpl getInstance() {
        return SaveSearchParamsServiceImpl.SingletonHolder.INSTANCE;
    }

    public String save(HttpServletRequest request) {
        Command command = (Command) request.getAttribute("command");
        StringBuilder result = new StringBuilder();

        if (command instanceof SortProductsByParamCommand) {
            boolean foundOnRequest = paramsService.parseFoundOn(request);
            result.append("sortProducts")
                    .append("?order=")
                    .append(paramsService.parseSortOrder(request).toString().toLowerCase())
                    .append("&sortParam=")
                    .append(paramsService.parseSortParam(request).toString().toLowerCase());
            if (foundOnRequest) {
                  result.append("&query=")
                          .append(request.getParameter("query"))
                          .append("&foundOnRequest=").append(true);
            }
        } else if (command instanceof FindProductByQueryCommand) {
            result.append("findQuery")
                    .append("?query=")
                    .append(request.getParameter("query"));
        }

        return result.toString();
    }
}
