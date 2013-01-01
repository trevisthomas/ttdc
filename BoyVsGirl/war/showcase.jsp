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
	String show = request.getParameter("show");

%>
<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8" />
<title>Boy vs Girl Photography | <%=who%></title>

<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
<!--[if lt IE 9]><script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script><![endif]-->

<meta name="medium" content="image" />


<%	
	ZenfolioService zenfolio = new ZenfolioService();
	
	PhotoSet photoSet = zenfolio.loadPhotoSetPhotos(code, 1);
	%>
	
<%
if(show==null){
%>
	<meta name="description" content="Boy vs Girl Photography - <%=who%>'s Showcase" />
	<link rel="image_src" href="<%=photoSet.getPhotos().get(0).getThumbUrl()%>" />
<% } else { 
	String photoId =  show.substring(show.lastIndexOf("/p") + 2, show.indexOf("-6.jpg"));
	Photo p = zenfolio.loadPhoto(photoId);
%>
	<meta name="description" content="<%=p.getCaption()%>&nbsp;&copy; Boy vs Girl Photography" />
	<link rel="image_src" href="<%=p.getThumbUrl()%>" />	
<% } %>


<link rel="stylesheet" href="css/style_masonry.css" />
<link rel="stylesheet" href="css/lightbox.css" />
<link rel="stylesheet" href="css/bvg.css" />
<link rel="shortcut icon" href="/favicon.ico" type="image/x-icon" />

<link rel="stylesheet" href="/fancybox/source/jquery.fancybox.css?v=2.1.3" type="text/css" media="screen" />
<link rel="stylesheet" href="/fancybox/source/helpers/jquery.fancybox-buttons.css?v=1.0.5" type="text/css" media="screen" />
<link rel="stylesheet" href="/fancybox/source/helpers/jquery.fancybox-thumbs.css?v=1.0.7" type="text/css" media="screen" />
	


<!-- scripts at bottom of page -->
<style>
#content {
	padding: 10px 10px 10px 180px;
}

#container {
	margin: 40px auto;
}

.box {
	background: rgba(50, 50, 50, 0.7);
}

#site-footer {
	clear: both;
	margin: 0px 0px;
	border-top: 0px solid black;
	padding-top: 10px;
	line-height: 30px;
	font-size: 95%;
	font-style: italic;
}

.fancybox-skin {
	position: relative;
	background: rgba(25, 25, 25, 0.7);
	color: #444;
	text-shadow: none;
	-webkit-border-radius: 4px;
	-moz-border-radius: 4px;
	border-radius: 4px;
}

.fancybox-title-float-wrap .child {
	background: rgba(25, 25, 25, 0.8);
	color: #666;
	line-height: 14px;
	padding: 5px;
}

.fancybox-nav {
    width: 60px;
}

.fancybox-nav span {
    visibility: visible;
    opacity: .1;
}

.fancybox-nav:hover span {
    opacity: .5;
}

.fancybox-close{
	opacity: .8;
}

.fancybox-caption{
	padding: 5px 0 0 0;
	font-size: 10px;
	font-style: italic;
	font-weight: lighter;
}
.social{
	padding:20px;
}
.fb-like:hover{
opacity: 1;
}

.fb-like{
opacity: .1;
}


​</style>
</head>

<body style="overflow: auto;">
<div id="fb-root"></div>
<script>(function(d, s, id) {
  var js, fjs = d.getElementsByTagName(s)[0];
  if (d.getElementById(id)) return;
  js = d.createElement(s); js.id = id;
  js.src = "//connect.facebook.net/en_US/all.js#xfbml=1";
  fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));</script>
<script>!function(d,s,id){var js,fjs=d.getElementsByTagName(s)[0];if(!d.getElementById(id)){js=d.createElement(s);js.id=id;js.src="//platform.twitter.com/widgets.js";fjs.parentNode.insertBefore(js,fjs);}}(document,"script","twitter-wjs");</script>
	<section id="content">
		
		

		<%	

	String str = request.getParameter("pageNumber");
	int pageNumber = 1;
	if(str != null){
		//pageNumber = Integer.valueOf((String)obj);
		pageNumber = Integer.valueOf(str);
	}
	
	photoSet = zenfolio.loadPhotoSetPhotos(code, pageNumber); //20547770
	%>


		<div id="logo-block">
			<span id="logo"><img src="/img/BvG_Signature_2013_final_fancy.png" /></span>
		</div>


		<%@ include file="menu.jsp"%>

		
		
		<div id="container" class="clearfix">

		<%
		for(Photo p : photoSet.getPhotos()){
		%>
			<div class="box photo col3">
				<a class="fancybox" data-title-id="<%=p.getUniqueName()%>" rel="group" href="<%=p.getXxLarge()%>"><img src="<%=p.getMedium()%>" alt="" /></a>
				<div id="<%=p.getUniqueName()%>" class="hidden">
				    <%=p.getTitle()%> | <a href="<%=p.getPageUrl()%>">Buy!</a>
				    <br/>
				    <span class="fancybox-caption caption-<%=p.getUniqueName()%>">Loading...</span>
				    <br/>
				   
				    <%
				    //<div class="social">
				    //<a href="https://twitter.com/share" class="twitter-share-button" data-url="http://localhost:8888/showcase.jsp?who=Trevis">Tweet</a>
				    //
				    //</div>
				    //<a href="https://twitter.com/share" class="twitter-share-button" data-url="http://localhost:8888/showcase.jsp?who=Trevis">Tweet</a>
				    //<div class="fb-like" data-href="http://www.google.com" data-send="false" data-layout="button_count" data-width="450" data-show-faces="false" data-colorscheme="dark"></div>
				    //<div class="fb-like" data-href="http://localhost:8888/showcase.jsp?who=Trevis" data-send="false" data-width="450" data-show-faces="false" data-colorscheme="dark"></div>
				    %>
				</div>
				<div class="fb-like" data-href="http://www.boyvsgirlphotography.com/showcase.jsp?who=<%=who%>&show=<%=p.getXxLarge()%>" data-send="false" data-layout="button_count" data-width="450" data-show-faces="false" data-colorscheme="dark"></div>
			</div>
		<%
	  	}
	  	%>
		</div>
		<!-- #container -->
		
		
		<nav id="page-nav" style="visibility: hidden;">
			<a href="showcase.jsp?pageNumber=<%=pageNumber+1%>&who=<%=who!=null?who:""%>"> Next </a>
		</nav>

	</section>
	<!-- #content -->


	<div id="footer-block">
		<div class="footer">
			<%@ include file="footer.jsp"%>
		</div>
	</div>

	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.8/jquery.min.js"></script>
	<script src="js/jquery.masonry.min.js"></script>
	<script src="js/jquery.infinitescroll.min.js"></script>
	<script src="js/lightbox_resize.js"></script>
	
	
	
	<!-- Add mousewheel plugin (this is optional) -->
	<script type="text/javascript" src="/fancybox/lib/jquery.mousewheel-3.0.6.pack.js"></script>
	
	<!-- Add fancyBox -->
	<script type="text/javascript" src="/fancybox/source/jquery.fancybox.pack.js?v=2.1.3"></script>
	
	<!-- Optionally add helpers - button, thumbnail and/or media -->
	
	<script type="text/javascript" src="/fancybox/source/helpers/jquery.fancybox-buttons.js?v=1.0.5"></script>
	<script type="text/javascript" src="/fancybox/source/helpers/jquery.fancybox-media.js?v=1.0.5"></script>
	
	<script type="text/javascript" src="/fancybox/source/helpers/jquery.fancybox-thumbs.js?v=1.0.7"></script>
	
	
	
	<script>
		var $container = $('#container');

		$container.imagesLoaded(function() {
			$container.masonry({
				itemSelector : '.box',
				isAnimated : true
			});
		});

		$container.infinitescroll({
			navSelector : '#page-nav', // selector for the paged navigation 
			nextSelector : '#page-nav a', // selector for the NEXT link (to page 2)
			itemSelector : '.box', // selector for all items you'll retrieve
			loading : {
				finishedMsg : 'No more pages to load.',
				img : '/img/progress.gif'
			}
		},
		// trigger Masonry as a callback
		function(newElements) {
			// hide new items while they are loading
			var $newElems = $(newElements).css({
				opacity : 0
			});
			// ensure that images load before adding to masonry layout
			$newElems.imagesLoaded(function() {
				// show elems now they're ready
				$newElems.animate({
					opacity : 1
				});
				$container.masonry('appended', $newElems, true);
			});
		});
	</script>
	<script type="text/javascript">
		function beforeLoadFunction(component, caption) {
			var text = '<span class="fancybox-caption">'+caption+'</span>';
			component.title += "working?";
        }
			
		$(document).ready(function() {
			$(".fancybox").fancybox({
				beforeLoad: function (){
					var el, id = $(this.element).data('title-id');
		            if (id) {
		                el = $('#' + id);
		            
		                if (el.length) {
		                    this.title = el.html() + "<br/>";
		                }
		            }
				}
				,
				afterLoad: function(){
					var id = $(this.element).data('title-id');
		            if (id) {
						$.get('/caption.jsp?id='+id, function(data) {
							$('.caption-' + id).html(data);
						});
		            }
				}
		    });
		});
		
		<%
		if(show != null){
		%>
			$(window).load(function () {
				 $('.fancybox[href="<%=show%>"]').trigger('click');
				});
		<%
		}
		%>
	</script>


</body>
</html>