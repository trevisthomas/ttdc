<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%><%@page import="com.bvg.*"%><%
	String id = request.getParameter("id");
	ZenfolioService zenfolio = new ZenfolioService();
	
	Photo photo = zenfolio.loadPhoto(id);
%><%=photo.getCaption()%> 