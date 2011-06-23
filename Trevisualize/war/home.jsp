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
<%@page import="com.ttdc.ImageServlet"%>
<%@page import="com.google.appengine.api.datastore.Key"%>
<%@page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@page import="com.google.appengine.api.datastore.DatastoreServiceFactory"%>
<%@page import="com.google.appengine.api.datastore.DatastoreService"%>
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>Trevisualize</title>
    
<style media="screen" type="text/css">
a{color:#348;text-decoration:none;outline:none;}
a:hover{text-decoration:underline;}
body{
 text-align: center;
 background-color: white;
 color: black;
}
.header{
 font-size: 20px;
 text-align: left;
}             
.footer{
 margin: 50px 0 0;
 font-size: 12px;
 text-align: right;
 width: 100%;
}
.photoset{
 
}
.photo{
 border: 0;
 padding: 0 0 150px 0;
}
.outer{
 width: 1004px;
 margin: 0 auto;
}
TABLE.info{
 width:100%;
 border-collapse: collapse;
 border: solid #cecece;
 border-width: 0px;
 margin: 4px 0 0 0;
 
}
TH, TD {
 padding: 0;
}
.number{
 background-color: #cecece;
 color: white;
 font-size: 25px;
 padding: 0 10px;
}
.title{
 width: 100%;
 text-align: center;
}
 
IMG{
 padding: 0; margin: 0;
 border: 2px solid black;
 display: block;
 margin: 0 auto;
}
</style>

    
  </head>

  <body>
    
    <div class="outer">
	 <div class="header">trevisualize</div>
	 <div class="photoset">
	 <%
	 	ImageTool imageTool = new ImageTool();
	 	List<Image> images = imageTool.fetchAll();
	 	for (Image image : images) {
	 %>
	  <div class="photo">
	  	 <img width="<%= image.getWidth() %>px" height="<%= image.getHeight() %>px" src="<%= image.getSrc() %>" />
	  	 
	  	 <div class="title"><%= image.getTitle() %></div>
	  	 <!-- 
	     <table class="info">
	       <tr>
			 <td class="number"><%= image.getOrder() %></td>
	         <td class="title"><%= image.getTitle() %></td>
	       </tr>
	     </table>
	     
	      -->
	     
	  </div>
	  <%
	  	}
	  %>
	 </div>
	 <div class="footer">&copy;2011 Trevis Thomas | <a href="http://www.flickr.com/photos/trevisualize/"><span style="color: blue;">flick</span><span style="color: red;">r</span></a></div>
	 
	</div>
	
  </body>
</html>