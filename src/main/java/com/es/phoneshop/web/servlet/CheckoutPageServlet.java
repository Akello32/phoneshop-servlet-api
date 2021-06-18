package com.es.phoneshop.web.servlet;

import com.es.phoneshop.model.cart.Cart;
import com.es.phoneshop.model.cart.service.CartService;
import com.es.phoneshop.model.cart.service.DefaultCartService;
import com.es.phoneshop.model.order.Order;
import com.es.phoneshop.model.order.PaymentMethod;
import com.es.phoneshop.model.order.service.DefaultOrderService;
import com.es.phoneshop.model.order.service.OrderService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class CheckoutPageServlet extends HttpServlet {
    private static final String JSP = "/WEB-INF/pages/checkout.jsp";
    private final CartService cartService;
    private final OrderService orderService = DefaultOrderService.getInstance() ;

    public CheckoutPageServlet(CartService cartService) {
        this.cartService = cartService;

    }

    public CheckoutPageServlet() {
        cartService = DefaultCartService.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        process(request, response);

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Cart cart = (Cart) req.getAttribute("cart");
        if (cart == null) {
            req.setAttribute("cartEmpty", "The cart is empty :|");
            req.getRequestDispatcher(JSP).forward(req, resp);
            return;
        }
        Order order = orderService.getOrder(cart);

        Map<String, String> errors = new HashMap<>();

        setRequiredParameter(req, errors, "firstName", order::setFistName);
        setRequiredParameter(req, errors, "lastName", order::setLastName);
        setRequiredParameter(req, errors, "deliveryAddress", order::setDeliveryAddress);

        setPhone(req, errors, order);
        setDeliveryDate(req, errors, order);
        setPaymentMethod(req, errors, order);

        if (errors.isEmpty()) {
            orderService.placeOrder(order);
            cartService.clearCart(cart);
            resp.sendRedirect(req.getContextPath() + "/order/overview/" + order.getSecureId());
        } else {
            req.setAttribute("errors", errors);
            process(req, resp);
        }
    }

    private void process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cart cart = (Cart) request.getAttribute("cart");
        if (cart == null) {
            request.setAttribute("cartEmpty", "The cart is empty :|");
            request.getRequestDispatcher(JSP).forward(request, response);
            return;
        }
        Order order = orderService.getOrder(cart);
        request.setAttribute("order", order);
        request.setAttribute("paymentMethods", PaymentMethod.values());
        request.getRequestDispatcher(JSP).forward(request, response);
    }

    private void setPhone(HttpServletRequest request, Map<String, String> errors, Order order) {
        String[] patterns = {"([\\+][375]{3}[29|33|44|25]{2}[\\d]{7})", "([\\+][375]{3}\\s[29|33|44|25]{2}\\s[\\d]{7})",
                "([\\+][375]{3}\\s[29|33|44|25]{2}\\s[\\d]{3}\\s[\\d]{2}\\s[\\d]{2})"};
        String phone = request.getParameter("phone");
        if (phone == null || phone.isEmpty()) {
            errors.put("phone", "Value is required");
        } else {
            for (String pattern : patterns) {
                if (Pattern.matches(pattern, phone)) {
                    order.setPhone(phone);
                    return;
                }
            }
            errors.put("phone", "Incorrect phone format");
        }
    }

    private void setDeliveryDate(HttpServletRequest request, Map<String, String> errors, Order order) {
        String date = request.getParameter("deliveryDate");
        if (date == null || date.isEmpty()) {
            errors.put("deliveryDate", "Value is required");
        } else {
            try {
                LocalDate deliveryDate = LocalDate.parse(date);
                order.setDeliveryDate(deliveryDate);
            } catch (DateTimeParseException e) {
                errors.put("deliveryDate", "Incorrect date format");
            }
        }
    }

    private void setPaymentMethod(HttpServletRequest request, Map<String, String> errors, Order order) {
        String value = request.getParameter("paymentMethod");
        if (value == null || value.isEmpty()) {
            errors.put("paymentMethod", "Value is required");
        } else {
            try {
                order.setPaymentMethod(PaymentMethod.valueOf(value));
            } catch (IllegalArgumentException e) {
                errors.put("paymentMethod", "Incorrect payment method format");
            }
        }
    }

    private void setRequiredParameter(HttpServletRequest request, Map<String, String> errors, String parameter,
                                      Consumer<String> consumer) {
        String value = request.getParameter(parameter);
        if (value == null || value.isEmpty()) {
            errors.put(parameter, "Value is required");
        } else {
            consumer.accept(value);
        }
    }
}

