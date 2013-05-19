<%@page import="org.ttdc.ImagesServlet"%>
<%@page import="org.ttdc.FolderMonitor"%>
<%@page import="java.util.List"%>
<%
	String who = request.getParameter("who");
	List<String> images = (List<String>)request.getAttribute("listOfImages");
	//List<String> images = ${accountList}
%>
<%
	for(String name : images){
%>
 	
 	 <img width="200px" src="images/<%=who%>/<%= name %>" />
 <% } %>
	   
