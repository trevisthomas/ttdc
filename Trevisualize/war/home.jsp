<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!-- The HTML 4.01 Transitional DOCTYPE declaration-->
<!-- above set at the top of the file will set     -->
<!-- the browser's rendering engine into           -->
<!-- "Quirks Mode". Replacing this declaration     -->
<!-- with a "Standards Mode" doctype is supported, -->
<!-- but may lead to some differences in layout.   -->

<%@page import="com.ttdc.Image"%>
<%@page import="com.ttdc.ImageTool"%>
<%@page import="com.google.appengine.api.datastore.FetchOptions"%>
<%@page import="com.google.appengine.api.datastore.Entity"%>
<%@page import="java.util.List"%>
<%@page import="com.google.appengine.api.datastore.Query"%>
<%@page import="com.google.appengine.api.datastore.Key"%>
<%@page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@page import="com.google.appengine.api.datastore.DatastoreServiceFactory"%>
<%@page import="com.google.appengine.api.datastore.DatastoreService"%>
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>Trevisualize</title>
    <link rel='stylesheet' href='css/dark.css'>
    
<style media="screen" type="text/css">

</style>

    
  </head>

<%
String pageNumber = request.getParameter("page");
%>
  <body>
    
    
    <div class="header"><a href="/">trevisualize</a></div>
    <div class="outer">
    
	 <div class="photoset">
	  
	 <%
	 	ImageTool imageTool = new ImageTool();
	 	//List<Image> images = imageTool.fetchAll();
	 	List<Image> images = imageTool.fetch(pageNumber);
	 	for (Image image : images) {
	 %>
	 
	  <div class="photo">
	  	 <img width="<%= image.getWidth() %>px" height="<%= image.getHeight() %>px" src="/loadImage?id=<%= image.getKey().getName() %>" />
	  	 
	  	 <div class="title"><%= image.getTitle() %> | <a target="_blank" href="<%=image.getUrl()%>">info</a> | <a href="/photo.jsp?id=<%= image.getKey().getName() %>">link</a> <% if(image.isForSale()) {%>| <a target="_blank" href="<%= image.getPurchaseUrl() %>">purchase</a><%} %></div> 
	  	 
	  </div>
	 
	  <%
	  	}
	  %>
	  
	  </div>
	 
	 <div class="nav">
	 <% boolean hasMore = imageTool.hasMore(pageNumber);
	 	boolean hasLess = imageTool.hasLess(pageNumber);
	   %>
	 <% if(hasLess) {%>
	 <a href="/home.jsp?page=<%= imageTool.prevPage(pageNumber) %>">prev</a>
	 <% } %>
	 <% if(hasLess && hasMore) {%>
	 &nbsp;|&nbsp;
	 <% } %>
	 <% if(hasMore) {%>
	 <a href="/home.jsp?page=<%= imageTool.nextPage(pageNumber) %>">next</a>
	 <% } %>
	 </div>
	 
	 </div>
	
	<%@ include file="footer.jsp" %>
  </body>
</html>