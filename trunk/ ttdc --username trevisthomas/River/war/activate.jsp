<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="org.ttdc.persistence.Persistence"%>
<%@page import="org.ttdc.gwt.server.dao.PersonDao"%>
<%@page import="org.ttdc.gwt.server.dao.AccountDao"%>
<%@page import="org.ttdc.persistence.objects.Personon"%>
<%@page import="org.ttdc.servlets.SessionProxy"%>
<%
	String guid = (String)request.getParameter("key");
	String errorMessage = "Aww shucks";
	if(guid != null){
		try{
	Persistence.beginSession();
	Person p = PersonDao.activate(guid);
	session.setAttribute(SessionProxy.SESSION_KEY_PERSON_ID, p.getPersonId());
	Persistence.commit();
	response.sendRedirect("/");
		}
		catch(Exception e){
	Persistence.rollback();
	errorMessage = e.getMessage();
		}
	}
%>

<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>Account Activation</title>
  </head>

  <body style="color: white; background-color:black">
  	<p>If you're seeing this something went wrong. Contact me for help.</p>
  	
  	<p style="color:red;"><%=errorMessage%></p>
  </body>
</html>