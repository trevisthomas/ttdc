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
    <link rel='stylesheet' href='css/style.css'>
    
<style media="screen" type="text/css">

</style>

    
  </head>

  <body>
    
    
    
    <div class="outer">
    <div class="header">trevisualize</div>
	 <div class="photoset">
	 
	 
	 <!-- 
	  <div class="photo">
	  	 <img width="1000px" height="321px" src="/images/BabyLove.jpg" />
	  	 
	  	 <div class="title">Baby Love</div>
	  </div>
	  
	 
	  <div class="photo">
	  	 <img width="875px" height="700px" src="/images/PinkRose.jpg" />
	  	 
	  	 <div class="title">Pink Rose</div>
	  </div>
	  
	 
	  <div class="photo">
	  	 <img width="1000px" height="500px" src="/images/Tranquility.jpg" />
	  	 
	  	 <div class="title">Tranquility</div>
	  </div>
	 
	  -->
	  
	 <%
	 	ImageTool imageTool = new ImageTool();
	 	List<Image> images = imageTool.fetchAll();
	 	for (Image image : images) {
	 %>
	 
	  <div class="photo">
	  	 <img width="<%= image.getWidth() %>px" height="<%= image.getHeight() %>px" src="/loadImage?id=<%= image.getKey().getName() %>" />
	  	 
	  	 <div class="title"><%= image.getTitle() %></div>
	     </table>
	  </div>
	 
	  <%
	  	}
	  %>
	  
	   
	 </div>
	 <div class="footer">&copy;2011 Trevis Thomas | <a href="http://www.flickr.com/photos/trevisualize/"><span style="color: blue;">flick</span><span style="color: red;">r</span></a></div>
	 
	</div>
	
  </body>
</html>