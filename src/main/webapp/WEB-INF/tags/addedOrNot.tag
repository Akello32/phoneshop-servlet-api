<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag trimDirectiveWhitespaces="true" %>

<c:choose>
    <c:when test="${not empty error}">
        <p style="color: red">Product not added to cart because ${error}</p>
    </c:when>
    <c:when test="${not empty param.added && param.added}">
        <p style="color: darkgreen">Product added to cart</p>
    </c:when>
</c:choose>