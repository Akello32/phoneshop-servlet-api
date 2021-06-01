package com.es.phoneshop.web.command;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CommandByUriFilter implements Filter {
    private Map<String, Command> commands = new ConcurrentHashMap<>();

    public CommandByUriFilter() {
    }

    /**Constructor for tests*/
    public CommandByUriFilter(Map<String, Command> commands) {
        this.commands = commands;
    }

    @Override
    public void init(FilterConfig filterConfig) {
        commands.put("/productList/products", new MainCommand());
        commands.put("/productList/findQuery", new FindProductByQueryCommand());
        commands.put("/productList/sortProducts", new SortProductsByParamCommand());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String contextPath = httpRequest.getContextPath();
        String uri = httpRequest.getRequestURI();
        int beginAction = contextPath.length();
        int endAction = uri.lastIndexOf('.');
        String actionName;
        if (endAction >= 0) {
            actionName = uri.substring(beginAction, endAction);
        } else {
            actionName = uri.substring(beginAction);
        }
        Command command = commands.get(actionName);
        httpRequest.setAttribute("command", command);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        commands = null;
    }
}
