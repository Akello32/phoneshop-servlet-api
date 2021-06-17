<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<jsp:useBean id="product" type="com.es.phoneshop.model.product.Product" scope="request"/>

<tags:master pageTitle="product">
    <p>${product.description}</p>
    <tags:addedOrNot />
    <div class="wrap">
        <table>
            <thead>
            <tr>
                <td>Image</td>
                <td>Description</td>
                <td>Code</td>
                <td class="price">Price</td>
                <td>Stock</td>
                <td>Order a product</td>
            </tr>
            </thead>
            <tr>
                <td>

                    <img class="product-tile"
                         src="${product.imageUrl}" alt="productImg">
                </td>
                <td>${product.description}</td>
                <td>${product.code}</td>
                <td class="price">
                    <fmt:formatNumber value="${product.price}" type="currency"
                                      currencySymbol="${product.currency.symbol}"/>
                </td>
                <td>
                        ${product.stock}
                </td>
                <td>
                    <form method="post" action="${pageContext.servletContext.contextPath}/products/${product.id}">
                        <input type="number" max="${product.stock}"
                               value="1" min="1" name="quantity">
                        <button style="margin-top: 10px">Add to cart</button>
                        <c:if test="${not empty error}">
                            <p class="error">${error}</p>
                        </c:if>
                    </form>
                </td>
            </tr>
        </table>
    </div>
    <c:if test="${fn:length(recentlyViewedProduct) >= 2}">
        <div class="wrapRv">
            <p>Recently viewed</p>
            <div class="recentlyViewed">
                <c:forEach var="productRv" items="${recentlyViewedProduct}"
                           end="${fn:length(recentlyViewedProduct) - 1}">
                    <c:if test="${product.id != productRv.id}">
                        <c:url var="pdp" value="/products/${productRv.id}"></c:url>
                        <div class="rvProduct">
                            <a href="${pdp}">
                                <img class="product-tile"
                                     src="${productRv.imageUrl}">
                            </a>
                            <p>${productRv.description}</p>
                            <p><fmt:formatNumber value="${productRv.price}" type="currency"
                                                 currencySymbol="${productRv.currency.symbol}"/></p>
                        </div>
                    </c:if>
                </c:forEach>
            </div>
        </div>
    </c:if>
</tags:master>
