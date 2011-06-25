<%@page import="com.ttdc.Image"%>
<%@page import="com.ttdc.ImageTool"%>
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ page import="com.google.appengine.api.users.User"%>
<%@ page import="com.google.appengine.api.users.UserService"%>
<%@ page import="com.google.appengine.api.users.UserServiceFactory"%>

<%@page import="com.google.appengine.api.datastore.FetchOptions"%>
<%@page import="com.google.appengine.api.datastore.Entity"%>
<%@page import="java.util.List"%>
<%@page import="com.google.appengine.api.datastore.Query"%>
<%@page import="com.google.appengine.api.datastore.Key"%>
<%@page import="com.google.appengine.api.datastore.KeyFactory"%>
<%@page import="com.google.appengine.api.datastore.DatastoreServiceFactory"%>
<%@page import="com.google.appengine.api.datastore.DatastoreService"%>

<html>
<body>

<%
	UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();
	if (user != null) {
%>
<p>Hello, <%=user.getNickname()%>! (You can <a
	href="<%=userService.createLogoutURL(request.getRequestURI())%>">sign out</a>.)</p>
<%
	} else {
%>
<p>Hello! <a
	href="<%=userService.createLoginURL(request.getRequestURI())%>">Sign in</a> to add images.</p>
<%
	}
%>

<%
  String id = request.getParameter("id");
  Image updateImage = null;
  String uuid = "";
  if(id != null){
  	updateImage = ImageTool.getImage(id);
  	uuid = updateImage.getKey().getName();
  }
%>

<% 
if(updateImage == null) 
{
%> 
 <form action="/saveImage" method="post">
 	<div>Order: <input name="order" type="text" value="<%=ImageTool.getCount() + 1%>"></input></div>
    <div>Title: <input name="title" type="text" value=""></input></div>
    <div>Flickr URL: <input name="url" type="text"></input></div>
    <div>Image SRC: <input name="src" type="text"></input></div>
    <div>Width: <input name="width" type="text"></input></div>
    <div>Height: <input name="height" type="text"></input></div>
    <div>description: <input name="description" type="text"></input></div>
    <div>dateTaken: <input name="dateTaken" type="text"></input></div>
    <div><input type="submit" value="Save" /></div>
  </form>
  
  
  <form action="/uploadImage" method="post" enctype='multipart/form-data'>
  	<div>File:<input type="file" name="uploadFormElement"></div>
  	<div>Order: <input name="order" type="text" value="<%=ImageTool.getCount() + 1%>"></input></div>
    <div>Title: <input name="title" type="text" value=""></input></div>
    <div>Flickr URL: <input name="url" type="text"></input></div>
    <div>Image SRC: <input name="src" type="text"></input></div>
    <div>description: <input name="description" type="text"></input></div>
    <div>dateTaken: <input name="dateTaken" type="text"></input></div>
    <div><input type="submit" value="Upload" /></div>
  </form>
  
<% 
} else {
%>
<form action="/saveImage" method="post">
 	<div>Order: <input name="order" type="text" value="<%=updateImage.getOrder()%>"></input></div>
    <div>Title: <input name="title" type="text" value="<%=updateImage.getTitle()%>"></input></div>
    <div>Flickr URL: <input name="url" type="text" value="<%=updateImage.getUrl()%>"></input></div>
    <div>description: <input name="description" type="text" value="<%=updateImage.getDescription()%>"></input></div>
    <div>dateTaken: <input name="dateTaken" type="text" value="<%=updateImage.getDateTaken()%>"></input></div>
    <div><input type="submit" value="Update" /></div>
    <div><a href="?">cancel</a></div>
    
    <input type="hidden" name="id" value="<%= uuid %>">
  </form>
  
  
  <form action="/saveImage" method="post">
 	<div>Order: <input name="order" type="text" value="<%=updateImage.getOrder()%>"></input></div>
    <div>Title: <input name="title" type="text" value="<%=updateImage.getTitle()%>"></input></div>
    <div>Flickr URL: <input name="url" type="text" value="<%=updateImage.getUrl()%>"></input></div>
    <div>Source <input name="src" type="text" value=""></input></div>
    <div>description: <input name="description" type="text" value="<%=updateImage.getDescription()%>"></input></div>
    <div>dateTaken: <input name="dateTaken" type="text" value="<%=updateImage.getDateTaken()%>"></input></div>
    <div><input type="submit" value="Replace" /></div>
    <div><a href="?">cancel</a></div>
    
    <input type="hidden" name="id" value="<%= uuid %>">
  </form>
  
<%
}
%>


   <table>
        <tr>
        	<th>User</th>
        	<th>Id</th>
        	<th>Title</th>
        	<th>Url</th>
        	<th>Date</th>
        	<th>Width</th>
        	<th>Height</th>
        </tr>
 	<%
	 	ImageTool imageTool = new ImageTool();
	 	List<Image> images = imageTool.fetchAll();
	 	for (Image image : images) {
	 %>
	 	
	 	 
                <tr>
                	<td><%= image.getCreator() %></td>
                	<td><%= image.getOrder() %></td>
                	<td><%= image.getTitle() %></td>
                	<td><%= image.getUrl() %></td>
                	<td><%= image.getDateAdded() %></td>
                	<td><%= image.getWidth() %></td>
                	<td><%= image.getHeight() %></td>
                	<td><a href="?id=<%=image.getKey().getName()%>">edit</a></td>
                	<td><a href="/deleteImage?id=<%=image.getKey().getName()%>">delete</a></td>
                </tr>
                
	 
	  <%
	  	}
	  %>  

  </table>
  
  
  
</body>
</html>

