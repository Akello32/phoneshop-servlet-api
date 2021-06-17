<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="cart" type="com.es.phoneshop.model.cart.Cart" scope="request"/>
<tags:master pageTitle="Cart">
    <c:choose>
        <c:when test="${empty cart.items}">
            <h1>The cart is empty :|</h1>
        </c:when>
        <c:otherwise>
            <c:if test="${param.updated}">
                <p style="color: green">Your shopping cart has been updated</p>
            </c:if>
            <c:if test="${param.deleted}">
                <p style="color: green">Product removed successfully</p>
            </c:if>
            <form method="post" action="${pageContext.servletContext.contextPath}/cart">
                <h2>Your Cart</h2>
                <table>
                    <thead>
                    <tr>
                        <td>Image</td>
                        <td>Description</td>
                        <td>Quantity</td>
                        <td class="price">Price</td>
                        <td></td>
                    </tr>
                    </thead>
                    <c:forEach var="item" items="${cart.items}">
                        <c:url var="pdp" value="/products/${item.product.id}"></c:url>
                        <tr>
                            <td>
                                <a href="${pdp}?">
                                    <img class="product-tile"
                                         src="${item.product.imageUrl}">
                                </a>
                            </td>
                            <td>${item.product.description}</td>
                            <td><input type="number" name="quantity" value="${item.quantity}"
                                       max="${item.product.stock}" min="1">
                                <input type="hidden" name="productId" value="${item.product.id}">
                                <c:set var="paramQuantity" value="${param.get('quantity')}"/>
                                <c:if test="${not empty errors[item.product.id]}">
                                    <p style="color: red">Not updated. ${errors[item.product.id]}</p>
                                </c:if>
                            </td>
                            <td class="price">
                                <a class="priceButton"><fmt:formatNumber value="${item.product.price}" type="currency"
                                                                         currencySymbol="${item.product.currency.symbol}"/></a>
                            </td>
                            <td>
                                <button form="deleteCartItem" value="${item.product.id}" name="deletingProduct"
                                        formaction="${pageContext.servletContext.contextPath}/cart/deleteCartItem">
                                    &#10060;
                                </button>
                                <c:if test="${not empty deleteCartItemError}">
                                    <p>${deleteCartItemError}</p>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    <tr>
                        <td></td>
                        <td></td>
                        <td>
                            Total Cost
                        </td>
                        <td>
                                ${cart.totalCost}
                        </td>
                    </tr>
                </table>
                <p>
                    <button>Update Cart</button>
                </p>
            </form>
            <form id="deleteCartItem" method="post">
                <input type="hidden" name="delete" value="delete">
            </form>
            <form action="${pageContext.servletContext.contextPath}/checkout">
                <button>Checkout</button>
            </form>
        </c:otherwise>
    </c:choose>

</tags:master>