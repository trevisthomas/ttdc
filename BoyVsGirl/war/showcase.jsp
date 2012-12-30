<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="com.bvg.*"%>

<%
	String who = request.getParameter("who");
	String code;
	if(who != null && "chrissy".compareToIgnoreCase(who) == 0){
		code = "143596818";
		//143596818 - chrissy's showcase
		//31637159 - trevis showcase
	}
	else{
		code = "31637159";
	}

%>
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8" />
  <title>Boy vs Girl Photography | <%=who%></title>
  
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
  <!--[if lt IE 9]><script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script><![endif]-->
  
  <link rel="stylesheet" href="css/style_masonry.css" />
  <link rel="stylesheet" href="css/lightbox.css" />
  <link rel="stylesheet" href="css/bvg.css" />
  <link rel="stylesheet"
	href="http://cdn.zenfolio.net/zf/css/en-US/ie9/windows/52C3MPK7FQ2PU/layout.css"
	type="text/css" />
<link rel="Shortcut Icon" href="/img/s11/v32/u646298236-81.ico" />
<link rel="stylesheet"
	href="http://cdn.zenfolio.net/zf/theme/en-US/ie9/windows/678838PAD7XXK/418V/M/custom/custom.css"
	type="text/css" />
  
  
  <!-- scripts at bottom of page -->
<style>
	#content {
	  padding: 10px 10px 10px 250px;
	}
	
	#container{
		margin: 40px auto;
	}
	.box{background: #333;}	
	#site-footer {
	  clear: both;
	  margin: 0px 0px;
	  border-top: 0px solid black;
	  padding-top: 10px;
	  line-height: 30px;
	  font-size: 95%;
	  font-style: italic;
	}
</style>
</head>
<body style="overflow:auto;">	
  <section id="content">


<%	ZenfolioService zenfolio = new ZenfolioService();

	String str = request.getParameter("pageNumber");
	int pageNumber = 1;
	if(str != null){
		//pageNumber = Integer.valueOf((String)obj);
		pageNumber = Integer.valueOf(str);
	}
	
	PhotoSet photoSet = zenfolio.loadPhotoSetPhotos(code, pageNumber); //20547770
	%>
    

			<div id="logo-block">
				<span id="logo"><img src="/img/BvG_Signature_2013_final_fancy.png" /></span>
			</div>


			<%@ include file="menu.jsp" %>


<div id="container"  class="clearfix">

	<%
	for(Photo p : photoSet.getPhotos()){
 	%>
		<div class="box photo col3">

			<a href="<%=p.getPhotoNoSuffix()%>" rel="lightbox-bvggroup" title="<%= p.getTitle()%>">
				<img src="<%=p.getMedium()%>"/>
			</a>

		</div>
	<%
  	}
  	%>
  	
  	

</div> <!-- #container -->

<nav id="page-nav" style="visibility:hidden;">
  <a href="showcase.jsp?pageNumber=<%=pageNumber+1%>&who=<%=who!=null?who:""%>"> Next </a>
</nav>

<script src="js/jquery-1.7.2.min.js"></script>
<script src="js/jquery.masonry.min.js"></script>
<script src="js/jquery.infinitescroll.min.js"></script>
<script src="js/lightbox_resize.js"></script>
<script>

var $container = $('#container');

$container.imagesLoaded( function(){
  $container.masonry({
    itemSelector : '.box', 
    isAnimated: true
  });
});

  
    $container.infinitescroll({
        navSelector  : '#page-nav',    // selector for the paged navigation 
        nextSelector : '#page-nav a',  // selector for the NEXT link (to page 2)
        itemSelector : '.box',     // selector for all items you'll retrieve
        loading: {
            finishedMsg: 'No more pages to load.',
            img: 'http://i.imgur.com/6RMhx.gif'
          }
        },
        // trigger Masonry as a callback
        function( newElements ) {
          // hide new items while they are loading
          var $newElems = $( newElements ).css({ opacity: 0 });
          // ensure that images load before adding to masonry layout
          $newElems.imagesLoaded(function(){
            // show elems now they're ready
            $newElems.animate({ opacity: 1 });
            $container.masonry( 'appended', $newElems, true ); 
          });
        }
      );
 
</script>
    
  </section> <!-- #content -->
  
     <footer id="site-footer">
		<div class="footer footer-border1 footer-bgcolor1 footer-color1 footer-font1 footer-bgimage1  footer-mod" id="footer" style="visibility: inherit;">
		
		    <div class="footer-copyright footer-font2">
		    </div>
		     
		     <%@ include file="footer.jsp" %>
		</div>
    </footer>


</body>
</html>