<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!-- The HTML 4.01 Transitional DOCTYPE declaration-->
<!-- above set at the top of the file will set     -->
<!-- the browser's rendering engine into           -->
<!-- "Quirks Mode". Replacing this declaration     -->
<!-- with a "Standards Mode" doctype is supported, -->
<!-- but may lead to some differences in layout.   -->

<%@page import="org.ttdc.ImagesServlet"%>
<%@page import="org.ttdc.FolderMonitor"%>
<%@page import="java.util.List"%>


<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>Boy vs Girl Studio!</title>
    <script type="text/javascript" src="js/jquery-1.9.1.min.js"></script>
    <link rel='stylesheet' href='css/dark.css'>
    
<style media="screen" type="text/css">
</style>




  <script language="javascript">
  	var lastFile;
  </script>  
  </head>
  
<%
String who = request.getParameter("who");
if(who == null){
	response.sendRedirect("/");
}
%>
  <body>
    <div class="outer">
     <div id="photoset">
	  
	 <%
	 	FolderMonitor monitor = ImagesServlet.getFolderMonitor(who);
	 	int count = 0;
	 	List<String> fileList = monitor.getAllFiles();
	 	
	 	String lastFile = "";
	 	if(fileList.size() > 0){
	 		lastFile = fileList.get(fileList.size()-1);
	 	}
	 %>
	 <script>
	 	lastFile = "<%=lastFile%>";
	 	//alert("last file is: " + lastFile);
	 </script>
	 <%
	 	for(String name : monitor.getAllFiles()){
	 %>
	  	
	  	 <img width="200px" src="images/<%=who%>/<%= name %>" />
	  <% } %>
	   
	 </div>
	</div>

  </body>
  
 <script>
 	var ajaxResponseProcessor = function(data){
        $.each(data, function(i,item){
      	  var name = data[i].FileName;
      	 //alert(name);
      	lastFile = name;
      	 $("<img/>").attr("src", 'images/<%=who%>/'+name).attr("width", 200).appendTo("#photoset");
        });
        //alert("last file is now: " + lastFile);
        setTimeout(refreshFunction, 5000);
      };
      
     var refreshFunction = function () {
 		$.getJSON('imagesSince?who=<%=who%>&last='+lastFile)
 		.done(ajaxResponseProcessor)
 		.fail(setTimeout(refreshFunction, 5000));
	};
 	
	$(window).load(function () { setTimeout(refreshFunction, 5000); });
 	
 </script>
</html>