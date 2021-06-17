<%@ tag trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ attribute name="label" required="true" %>
<%@ attribute name="name" required="true" %>

<tr>
    <td>${label}</td>
    <td>
        ${name}
    </td>
</tr>