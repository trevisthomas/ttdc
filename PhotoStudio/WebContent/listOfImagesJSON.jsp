<%@page import="org.ttdc.ImagesServlet"%>
<%@page import="org.ttdc.FolderMonitor"%>
<%@page import="java.util.List"%>
<%
	String who = request.getParameter("who");
	List<String> images = (List<String>)request.getAttribute("listOfImages");
%>
[ 
<%
	boolean comma = false;
	for(String name : images){
		if(comma){
			%>,<%
		}
		else{
			comma = true;
		}
%>
	
 	{"FileName": "<%= name %>"} 	
 <% } %>
]	   
	   
