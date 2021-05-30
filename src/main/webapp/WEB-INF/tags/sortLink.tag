<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="sortParam" required="true" %>
<%@ attribute name="order" required="true" %>
<%@ attribute name="symbolOrder" required="true" %>
<%@ attribute name="sortLink" required="true" %>

<a href="${sortLink}?order=${order}&sortParam=${sortParam}" class="sortLink">${symbolOrder}</a>