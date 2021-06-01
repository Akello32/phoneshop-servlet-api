<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="sortParam" required="true" %>
<%@ attribute name="order" required="true" %>
<%@ attribute name="symbolOrder" required="true" %>
<%@ attribute name="sortLink" required="true" %>

<c:if test="${foundOnRequest}">
    <c:set var="queryParam" value="&query=${query}"></c:set>
    <c:set var="foundOnParam" value="&foundOnRequest=${foundOnRequest}"></c:set>
</c:if>
<a href="${sortLink}?order=${order}&sortParam=${sortParam}${queryParam}${foundOnParam}"
    class="sortLink">${symbolOrder}</a>