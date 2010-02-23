<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="org.ttdc.persistence.Persistence"%>
<%@page import="org.ttdc.gwt.server.dao.PersonDao"%>
<%@page import="org.ttdc.gwt.server.dao.AccountDao"%>
<%@page import="org.ttdc.persistence.objects.Personon"%>
<%@page import="org.ttdc.servlets.SessionProxy"%>
<%
	String glitter = (String)request.getParameter("glitter");
	String magic = (String)request.getParameter("magic");
	String errorMessage = "Aww shucks";
	if(glitter != null && magic != null){
		try{
	Persistence.beginSession();
	Person p = AccountDao.resetPasswordMagicValidator(glitter,magic);
	session.setAttribute(SessionProxy.SESSION_KEY_PERSON_ID, p.getPersonId());
	Persistence.commit();
	response.sendRedirect("/"); //Redirect to the reset password page...
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
    <title>Password Reset Failed</title>
  </head>

  <body style="color: white; background-color:black">
  	<p>If you're seeing this something went wrong. Contact me for help.</p>
  	
  	<p style="color:red;"><%=errorMessage%></p>
  </body>
</html>