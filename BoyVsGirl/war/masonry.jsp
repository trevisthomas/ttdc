<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="com.bvg.*"%>

<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8" />
  
  <title>Animating with jQuery &middot; jQuery Masonry</title>
  
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
  <!--[if lt IE 9]><script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script><![endif]-->
  
  <link rel="stylesheet" href="css/style.css" />
  <link rel="stylesheet" href="css/lightbox.css" />
  
  
  <!-- scripts at bottom of page -->

</head>
<body>
  <section id="content">


<%	ZenfolioService zenfolio = new ZenfolioService();

	String str = request.getParameter("pageNumber");
	int pageNumber = 1;
	if(str != null){
		//pageNumber = Integer.valueOf((String)obj);
		pageNumber = Integer.valueOf(str);
	}
	
	PhotoSet photoSet = zenfolio.loadPhotoSetPhotos("31637159", pageNumber); //20547770
	%>
    
      <h1>Trevis</h1>
</div>

<div id="container" class="clearfix">

	<%
	for(Photo p : photoSet.getPhotos()){
 	%>
 	<div class="box photo col3">
 		
 		<a href="<%=p.getXxLarge()%>" rel="lightbox-images" ><img src="<%=p.getMedium()%>" /></a>
 		
 	</div>
	<%
  	}
  	%>
  	
  	

</div> <!-- #container -->

<nav id="page-nav">
  <a href="masonry.jsp?pageNumber=<%=pageNumber+1%>"> <%=pageNumber+1%> </a>
</nav>

<script src="js/jquery-1.7.2.min.js"></script>
<script src="js/jquery.masonry.min.js"></script>
<script src="js/jquery.infinitescroll.min.js"></script>
<script src="js/lightbox.js"></script>
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
    
    <footer id="site-footer">
      jQuery Masonry by <a href="http://desandro.com">David DeSandro</a>
    </footer>
    
  </section> <!-- #content -->


</body>
</html>