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
    <form action="${findQueryUrl}">
        <input name="query" value="${param.query }"/>
        <button>Search</button>
    </form>

    <tags:addedOrNot/>
    <c:url var="sortProductsUrl" value="/productList/sortProducts.jsp"></c:url>
    <div class="wrap">
        <table>
            <thead>
            <tr>
                <td>Image</td>
                <td>Description
                    <tags:sortLink sortParam="desc" sortLink="${sortProductsUrl}" order="ascend"
                                   symbolOrder="&uArr;"></tags:sortLink>
                    <tags:sortLink sortParam="desc" sortLink="${sortProductsUrl}" order="descend"
                                   symbolOrder="&dArr;"></tags:sortLink></td>
                <td class="price">Quantity</td>
                <td class="price">Price
                    <tags:sortLink sortParam="price" sortLink="${sortProductsUrl}" order="ascend"
                                   symbolOrder="&uArr;"></tags:sortLink>
                    <tags:sortLink sortParam="price" sortLink="${sortProductsUrl}" order="descend"
                                   symbolOrder="&dArr;"></tags:sortLink></td>
                <td></td>
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
                    <td>
                        <form id="addToCart${product.id}" method="post">
                            <input type="number" max="${product.stock}" style="text-align: right" value="1" min="1"
                                   name="quantity">
                            <input type="hidden" name="productId" value="${product.id}">
                        </form>
                    </td>
                    <td class="price">
                        <a class="priceButton"><fmt:formatNumber value="${product.price}" type="currency"
                                                                 currencySymbol="${product.currency.symbol}"/></a>
                        <div class="priceHistory">
                            <c:forEach var="priceHistory" items="${product.histories}">
                                <div class="noteInHistory">
                                    <p>
                                        <fmt:parseDate value="${priceHistory.date}" pattern="yyyy-MM-dd"
                                                       var="parsedDate"/>
                                        <fmt:formatDate value="${parsedDate}"/></p>
                                    <p><fmt:formatNumber value="${priceHistory.price}" type="currency"
                                                         currencySymbol="${product.currency.symbol}"/></p>
                                </div>
                            </c:forEach>
                        </div>
                    </td>
                    <td>
                        <button style="margin-top: 10px" form="addToCart${product.id}">Add to cart</button>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </div>
</tags:master>