<%@page import="org.ttdc.ImagesServlet"%>
<%@page import="org.ttdc.FolderMonitor"%>
<%@page import="java.util.List"%>

<!doctype html>
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>Boy vs Girl Studio!</title>
    <script type="text/javascript" src="js/jquery-1.9.1.min.js"></script>
    <link rel='stylesheet' href='css/dark.css'>
    
	
		<!-- Add mousewheel plugin (this is optional) -->
	<script type="text/javascript" src="js/jquery.mousewheel-3.0.6.pack.js"></script>

	<!-- Add fancyBox main JS and CSS files -->
	<script type="text/javascript" src="fancybox/source/jquery.fancybox.js?v=2.1.4"></script>
	<link rel="stylesheet" type="text/css" href="fancybox/source/jquery.fancybox.css?v=2.1.4" media="screen" />

	<!-- Add Button helper (this is optional) -->
	<link rel="stylesheet" type="text/css" href="fancybox/source/helpers/jquery.fancybox-buttons.css?v=1.0.5" />
	<script type="text/javascript" src="fancybox/source/helpers/jquery.fancybox-buttons.js?v=1.0.5"></script>

	<!-- Add Thumbnail helper (this is optional) -->
	<link rel="stylesheet" type="text/css" href="fancybox/source/helpers/jquery.fancybox-thumbs.css?v=1.0.7" />
	<script type="text/javascript" src="fancybox/source/helpers/jquery.fancybox-thumbs.js?v=1.0.7"></script>

	<!-- Add Media helper (this is optional) -->
	<script type="text/javascript" src="fancybox/source/helpers/jquery.fancybox-media.js?v=1.0.5"></script>
	
	
    
<style media="screen" type="text/css">
body{
	background-color: black;
}
.box {
	margin: 5px;
	padding: 5px;
	background: #222;
	font-size: 11px;
	line-height: 1.4em;
	float: left;
	-webkit-border-radius: 5px;
	-moz-border-radius: 5px;
	border-radius: 5px;
}

#photoset{
	margin: 0 auto;
}
</style>




  <script>
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
	 <% for(String name : monitor.getAllFiles()){ %>
	 	<div class="box photo">
	  		<a class="fancybox" rel="group" href="images/<%=who%>/<%= name %>"><img width="200px" src="images/<%=who%>/<%= name %>" /></a>
	  	</div>
	  <% } %>
	 </div>
	</div>

  </body>
  
 <script>
 	var ajaxResponseProcessor = function(data){
 		var nowShowing = $(".fancybox-image").attr('src');
 		var isLastFileShowing = false;
 		if(nowShowing == 'images/<%=who%>/'+lastFile){
 			//alert("Last file is being viewed: " + lastFile);
 			isLastFileShowing = true;
 		}
 		var anchor;
 		$.each(data, function(i,item){
			var name = data[i].FileName;
			//alert(name);
			lastFile = name;
			//var test = $("<img/>").attr("src", 'images/<%=who%>/'+name).attr("width", 200).attr("class",'fancybox');
			//test.fancybox();
			//test.appendTo("#photoset");
			anchor = $("<a/>").attr("class", "fancybox").attr("href",'images/<%=who%>/'+name).attr("rel", "group");
			var test = $("<img/>").attr("src", 'images/<%=who%>/'+name).attr("width", 200);
			anchor.html(test);
			//anchor.fancybox();
			
			var div = $("<div/>").attr("class","photo box").html(anchor);
			div.appendTo("#photoset");
        });
        $(".fancybox").fancybox();
        //alert("last file is now: " + lastFile);
        
        //alert("now showing: " + nowShowing);
        if(isLastFileShowing){
        	//alert("Updating now showing to: " + lastFile);
        	anchor.click();
        }
        setTimeout(refreshFunction, 5000);
      };
      
     var refreshFunction = function () {
 		$.getJSON('imagesSince?who=<%=who%>&last='+lastFile)
 		.done(ajaxResponseProcessor)
 		.fail(setTimeout(refreshFunction, 5000));
	};
 	
	$(window).load(function () { setTimeout(refreshFunction, 5000); });
	
	
 	
 </script>
 
 <script type="text/javascript">
		$(document).ready(function() {
			$(".fancybox").fancybox();
		});
		
	</script>
</html>