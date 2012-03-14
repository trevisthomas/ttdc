<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@page import="com.bvg.*"%>

<html>
<head>
	<title>Boy vs Girl Photography - Comming Soon</title>
	
	<%
	ZenfolioService zenfolio = new ZenfolioService();
	PhotoSet photoSet = zenfolio.loadPhotoSet("616294023");
	%>

	<meta http-equiv="content-type" content="text/html; charset=UTF-8" />

</head>

<style type="text/css">
	ul#demo-block{ margin:0 15px 15px 15px; }
		ul#demo-block li{ margin:0 0 10px 0; padding:10px; display:inline; float:left; clear:both; color:#aaa; background:url('img/bg-black.png'); font:16px Helvetica, Arial, sans-serif; }
		ul#demo-block li a{ color:#eee; font-weight:bold; }
</style>
<body>

	<!--Demo styles (you can delete this block)-->
	
	<ul id="demo-block">
		<li>The future home of Boy vs Girl Photography.</li>
	</ul>
	
	
</body>
</html>


