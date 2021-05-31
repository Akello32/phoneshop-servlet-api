<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<jsp:useBean id="products" type="java.util.ArrayList" scope="request"/>
<tags:master pageTitle="Product List">
    <p>
        Welcome to Expert-Soft training!
    </p>
    <c:url var="findQueryUrl" value="/productList/findQuery.jsp"></c:url>
    <form method="post" action="${findQueryUrl}">
        <input name="query" value="${param.query }"/>
        <button>Search</button>
    </form>

    <c:url var="sortProductsUrl" value="/productList/sortProducts.jsp"></c:url>
    <table>
        <thead>
        <tr>
            <td>Image</td>
            <td>Description
                <tags:sortLink sortParam="desc" sortLink="${sortProductsUrl}" order="ascend"
                               symbolOrder="&uArr;"></tags:sortLink>
                <tags:sortLink sortParam="desc" sortLink="${sortProductsUrl}" order="descend"
                               symbolOrder="&dArr;"></tags:sortLink>
            <td class="price">Price
                <tags:sortLink sortParam="price" sortLink="${sortProductsUrl}" order="ascend"
                               symbolOrder="&uArr;"></tags:sortLink>
                <tags:sortLink sortParam="price" sortLink="${sortProductsUrl}" order="descend"
                               symbolOrder="&dArr;"></tags:sortLink></td>
        </tr>
        </thead>
        <c:forEach var="product" items="${products}">
            <c:url var="pdp" value="/products/${product.id}"></c:url>
            <tr>
                <td>
                    <a href="${pdp}?">
                        <img class="product-tile"
                             src="${product.imageUrl}">
                    </a>
                </td>
                <td>${product.description}</td>
                <td class="price">
                    <fmt:formatNumber value="${product.price}" type="currency"
                                      currencySymbol="${product.currency.symbol}"/>
                </td>
            </tr>
        </c:forEach>
    </table>
</tags:master>