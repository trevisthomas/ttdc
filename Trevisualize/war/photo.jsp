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
String id = request.getParameter("id");
if(id == null){
	response.sendRedirect("/");
}
%>
  <body>
    <div class="header"><a href="/">trevisualize</a></div>
    <div class="outer">
     <div class="photoset">
	  
	 <%
	 	Image image = ImageTool.getImage(id);
	 %>
	 
	  <div class="photo">
	  	 <img width="<%= image.getWidth() %>px" height="<%= image.getHeight() %>px" src="/loadImage?id=<%= image.getKey().getName() %>" />
	  	 
	  	 <div class="title"><%= image.getTitle() %> | <a target="_blank" href="<%=image.getUrl()%>">info</a></div>
	  </div>
	   
	 </div>
	 <div class="nav">
	 <a href="/">home</a>
	 </div>
	 
	 
	 
	</div>
	<%@ include file="footer.jsp" %>
  </body>
</html>