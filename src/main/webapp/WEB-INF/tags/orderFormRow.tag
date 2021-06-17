<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="label" required="true" %>
<%@ attribute name="name" required="true" %>
<%@ attribute name="type"%>

<tr>
    <td>${label}</td>
    <td>
        <c:set var="error" value="${errors[name]}" />
        <input type="${type}" name="${name}" required>
        <c:if test="${not empty error}">
            <p style="color: red">Not Ordered. ${errors[name]}</p>
        </c:if>
    </td>
</tr>