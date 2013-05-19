<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!-- The HTML 4.01 Transitional DOCTYPE declaration-->
<!-- above set at the top of the file will set     -->
<!-- the browser's rendering engine into           -->
<!-- "Quirks Mode". Replacing this declaration     -->
<!-- with a "Standards Mode" doctype is supported, -->
<!-- but may lead to some differences in layout.   -->

<%@page import="org.ttdc.ImagesServlet"%>
<%@page import="org.ttdc.FolderMonitor"%>
<%@page import="java.util.Set"%>

<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>Boy vs Girl Studio!</title>
    <link rel='stylesheet' href='css/dark.css'>
    
<style media="screen" type="text/css">

</style>

    
  </head>
  <body>
    <div class="outer">
	  
	 <%
	 	Set<String> monitored = ImagesServlet.getMonitoredNames();
	 %>
	 <% for(String name : monitored){ %>
	 	<a href="images.jsp?who=<%= name %>"><%= name %></a><br> 
	 <% } %>
	   
	</div>

  </body>
</html>