<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="order" type="com.es.phoneshop.model.order.Order" scope="request"/>
<tags:master pageTitle="Checkout">
    <c:choose>
        <c:when test="${empty order}">
            <h1>The cart is empty :|</h1>
        </c:when>
        <c:otherwise>
            <br>
            <h2>Your order</h2>
            <div class="wrap">
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
                <form method="post" action="${pageContext.servletContext.contextPath}/checkout"
                      style="margin-left: 200px">
                    <h2>Order details</h2>
                    <c:if test="${param.ordered}">
                        <p style="color: green">The order is made</p>
                    </c:if>
                    <table>
                        <thead>
                        <tags:orderFormRow label="Firts name" name="firstName"/>
                        <tags:orderFormRow label="Last name" name="lastName"/>
                        <tags:orderFormRow label="Phone" name="phone"/>
                        <tags:orderFormRow label="Delivery Date" name="deliveryDate" type="date"/>
                        <tags:orderFormRow label="Delivery Address" name="deliveryAddress"/>
                        <tr>
                            <td>Payment method</td>
                            <td>
                                <select name="paymentMethod" required>
                                    <option></option>
                                    <c:forEach var="paymentMethod" items="${paymentMethods}">
                                        <option>
                                            <p>${paymentMethod}</p>
                                        </option>
                                    </c:forEach>
                                </select>
                                <c:if test="${not empty errors['paymentMethod']}">
                                    <p style="color: red">Not Ordered. ${errors['paymentMethod']}</p>
                                </c:if>
                            </td>
                        </tr>
                        </thead>
                    </table>

                    <p>
                        <button>Order</button>
                    </p>
                </form>
            </div>
        </c:otherwise>
    </c:choose>

</tags:master>