<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag trimDirectiveWhitespaces="true" %>

<%--<jsp:useBean id="cart" type="com.es.phoneshop.model.cart.Cart" scope="request"/>--%>

<div class="miniCart">
    <p>Your shopping cart</p>
    <table>
        <thead>
        <tr>
            <td>
                Total Quantity
            </td>
            <td>
                Total Cost
            </td>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td>
                ${cart.totalQuantity}
            </td>
            <td>
                ${cart.totalCost}
            </td>
        </tr>
        </tbody>
    </table>
    <c:url var="cartLink" value="/cart/"></c:url>
    <a href="${cartLink}">go to your shopping cart</a>
</div>