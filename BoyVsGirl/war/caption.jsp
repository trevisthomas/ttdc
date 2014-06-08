<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%><%@page import="com.bvg.*"%><%
	String id = request.getParameter("id");
	ZenfolioService zenfolio = new ZenfolioService();
	
	Photo photo = zenfolio.loadPhoto(id);
	String who = request.getParameter("who");
%>
<div id="fb-root"></div>
<%=photo.getCaption()%> 
<script>
	$(document).attr('title', "<%=photo.getTitle()%> by <%=who%> | Boy vs Girl Photography"); 
</script>

