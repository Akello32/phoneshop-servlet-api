<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:useBean id="product" type="com.es.phoneshop.model.product.Product" scope="request"/>

<tags:master pageTitle="product">
    <p>Welcome to the PDP!</p>
    <table>
        <thead>
        <tr>
            <td>Image</td>
            <td>Description</td>
            <td>Code</td>
            <td class="price">Price</td>
            <td>Stock</td>
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
        </tr>
    </table>
</tags:master>
