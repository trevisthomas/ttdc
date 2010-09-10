<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="org.ttdc.servlets.SessionProxy"%>
<%@page import="org.ttdc.gwt.server.dao.PersonDao"%>
<%@page import="org.ttdc.persistence.objects.Person"%>
<%@page import="org.ttdc.persistence.Persistence"%>

<%@page import="org.ttdc.gwt.server.dao.StyleDao"%>
<%@page import="org.ttdc.gwt.server.dao.InitConstants"%>
<%@page import="org.ttdc.util.Cookies"%>
<%@page import="org.ttdc.gwt.client.presenters.util.CookieTool"%>
<%@page import="org.ttdc.gwt.server.dao.AccountDao"%><html>
  <head>
  
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>TTDC v7 Alpha</title>
    
    <link type="text/css" rel="stylesheet" href="/css/shacktags.css"/>
    <link type="text/css" rel="stylesheet" href="/css/basic.css"/>
    <link type="text/css" rel="stylesheet" href="/css/standard.css"/>
    <link type="text/css" rel="stylesheet" href="/css/corettdc.css"/>
    <%
    	Object obj = session.getAttribute(SessionProxy.SESSION_KEY_PERSON_ID);
    	Person person;
    	String cssFileName = InitConstants.DEFAULT_STYLE.getCss();
    	try{
    		Persistence.beginSession();
	    	if (obj != null) {
	    		String personId = obj.toString();
	    		person = PersonDao.loadPerson(personId);
	    	} else {
	    		String guid = Cookies.getCookieValue(request,CookieTool.COOKIE_USER_GUID);
	    		String pwd = Cookies.getCookieValue(request,CookieTool.COOKIE_USER_PWD);
	    		person = AccountDao.authenticate(guid,pwd);
	    	}
	    	
	    	if (person.getStyle() != null)
    			cssFileName = person.getStyle().getCss();
    		else
    			cssFileName = InitConstants.DEFAULT_STYLE.getCss();
	    	
	    	Persistence.commit();
    	}
    	catch(Exception e){
    		System.err.println("Failed badly during launch.jsp "+e);
    	}
    %>
    <link id="mainCss" href="<%=request.getContextPath()%>/css/<%=cssFileName%>" rel="stylesheet" type="text/css" />
    
    <script type="text/javascript" language="javascript" src="client/client.nocache.js"></script>
    
     <!-- GWTEXT2 -->
	<link rel="stylesheet" type="text/css" href="js/ext/resources/css/ext-all.css"/>
    <script type="text/javascript" src="js/ext/adapter/ext/ext-base.js"></script>
    <script type="text/javascript" src="js/ext/ext-all.js"></script>
    <!-- GWTEXT2 -->
    
   <script language='JavaScript'> 
   		function tggle_video(target,embedurl) { var s=document.getElementById(target); if ( s.innerHTML.length==0 ) s.innerHTML='<object width="560" height="340"><param name="movie" value="'+embedurl+'"></param><param name="allowFullScreen" value="true"></param><param name="allowscriptaccess" value="always"></param><embed src="'+embedurl+'" type="application/x-shockwave-flash" allowscriptaccess="always" allowfullscreen="true" width="560" height="340"></embed></object>'; else s.innerHTML=""; }
		//Never got teh one below to work
   		//function tggle_embed(target,embedHtml) { var s=document.getElementById(target); if ( s.innerHTML.length==0 ) s.innerHTML=embedHtml; else s.innerHTML=""; }
		function tggle_embed(target,embedHtml) { var s=document.getElementById('11E63E28-0961-45B4-91B7-61DB70944ADE'); if ( s.innerHTML.length==0 ) s.innerHTML=embedHtml; else s.innerHTML=""; }
		
		function tggle_embed7(embedHtml) { var s=document.getElementById('11E63E28-0961-45B4-91B7-61DB70944ADE'); if ( s.innerHTML.length==0 ) s.innerHTML=embedHtml; else s.innerHTML=""; document.getElementById('11E63E28-0961-45B4-91B7-61DB70944ADE').scrollIntoView();}

   		function toggleTest(target) { var s=document.getElementById(target); if ( s.innerHTML.length==0 ) s.innerHTML=embedHtml; else s.innerHTML=""; }
   </script>
   
   <script type="text/javascript">
    function toggle_visibility(id) {
       var e = document.getElementById(id);
       if(e.style.display == 'block')
          e.style.display = 'none';
       else
          e.style.display = 'block';
    }
	</script>
	
	<script type="text/javascript">
		function createCss(filename){
			var fileref=document.createElement("link");
			fileref.setAttribute("rel", "stylesheet");
			fileref.setAttribute("type", "text/css");
			fileref.setAttribute("id", "mainCss");
			fileref.setAttribute("href", filename);
			return fileref;
		}

		function replaceCss(newFilename){
			var newelement=createCss(newFilename);
			var element = getElementById('mainCss');
			element.parentNode.replaceChild(newelement);
		}
	</script>
	
	
	
  </head>

  <body>
    <iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>
	<div id="content" class="tt-view-container"></div>
  </body>
</html>