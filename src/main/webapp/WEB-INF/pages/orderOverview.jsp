<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="order" type="com.es.phoneshop.model.order.Order" scope="request"/>
<tags:master pageTitle="Cart">
    <h2>Order Overview</h2>
    <table>
        <thead>
        <tr>
            <td>Image</td>
            <td>Description</td>
            <td>Quantity</td>
            <td class="price">Price</td>
        </tr>
        </thead>
        <c:set var="currencySymbol" value="${order.items[0].product.currency.symbol}"/>
        <c:forEach var="item" items="${order.items}">
            <c:url var="pdp" value="/products/${item.product.id}"/>
            <tr>
                <td>
                    <a href="${pdp}?">
                        <img class="product-tile"
                             src="${item.product.imageUrl}">
                    </a>
                </td>
                <td>${item.product.description}</td>
                <td>${item.quantity}
                    <input type="hidden" name="productId" value="${item.product.id}">
                </td>
                <td class="price">
                    <a class="priceButton"><fmt:formatNumber value="${item.product.price}" type="currency"
                                                             currencySymbol="${item.product.currency.symbol}"/></a>
                </td>
            </tr>
        </c:forEach>
        <tr>
            <td></td>
            <td></td>
            <td>
                Subtotal
            </td>
            <td class="price">
                    ${currencySymbol}${order.subtotal}
            </td>
        </tr>
        <tr>
            <td></td>
            <td></td>
            <td>
                Delivery Cost
            </td>
            <td class="price">
                    ${currencySymbol}${order.deliveryCost}
            </td>
        </tr>

        <tr>
            <td></td>
            <td></td>
            <td>
                Subtotal
            </td>
            <td class="price">
                    ${currencySymbol}${order.totalCost}
            </td>
        </tr>
    </table>
    <p></p>
    <h3>Order details</h3>
    <table>
        <thead>
        <tags:orderOverviewRow label="Firts name" name="${order.fistName}"/>
        <tags:orderOverviewRow label="Last name" name="${order.lastName}"/>
        <tags:orderOverviewRow label="Phone" name="${order.phone}"/>
        <tags:orderOverviewRow label="Delivery Date" name="${order.deliveryDate}"/>
        <tags:orderOverviewRow label="Delivery Address" name="${order.deliveryAddress}"/>
        <tags:orderOverviewRow label="PaymentMethod" name="${order.paymentMethod}"/>
        </thead>
    </table>
</tags:master>