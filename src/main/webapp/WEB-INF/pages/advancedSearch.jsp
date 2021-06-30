<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>

<tags:master pageTitle="Product List">
    <p>
        Advanced Search
    </p>
    <c:url var="findQueryUrl" value="/advancedSearch"/>
    <form action="${findQueryUrl}" method="post">
        <p>Description <input name="desc" value=""/>
            <select name="paramDesc">
                <option></option>
                <option>all_words</option>
                <option>any_words</option>
            </select></p>
        <p>Min price <input name="minPrice" value=""/>
            <c:if test="${not empty errors['minPrice']}">
        <p style="color: red">${errors['minPrice']}</p>
        </c:if>
        </p>
        <p>Max price <input name="maxPrice" value=""/>
            <c:if test="${not empty errors['maxPrice']}">
        <p style="color: red">${errors['maxPrice']}</p>
        </c:if></p>
        <button>Search</button>
    </form>

    <c:if test="${not empty products}">
        <table>
            <thead>
            <tr>
                <td>Image</td>
                <td>Description</td>
                <td class="price">Price</td>
            </tr>
            </thead>
            <c:forEach var="item" items="${products}">
                <c:url var="pdp" value="/products/${item.id}"/>
                <tr>
                    <td>
                        <a href="${pdp}?">
                            <img class="product-tile"
                                 src="${item.imageUrl}">
                        </a>
                    </td>
                    <td>${item.description}</td>
                    <td class="price">
                        <a class="priceButton"><fmt:formatNumber value="${item.price}" type="currency"
                                                                 currencySymbol="${item.currency.symbol}"/></a>
                    </td>
                </tr>
            </c:forEach>
        </table>
    </c:if>
</tags:master>