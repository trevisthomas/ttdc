<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="com.bvg.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">


<%
	   if(request.getParameterMap().size() > 0){
		   response.sendRedirect("http://photos.boyvsgirlphotography.com/?"+request.getQueryString());
	   }
	%>

<%
ZenfolioService zenfolio = new ZenfolioService();
PhotoSet photoSet = zenfolio.loadPhotoSet("616294023");
%>

	<head>

		<title>Boy vs Girl Photography</title>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8" />
		<meta name="description" content="Boy vs Girl Photography - Trevis and Chrissy Thomas"/>
		<meta name="medium" content="image"/>
		<link rel="image_src" href="http://www.boyvsgirlphotography.com/img/BvG_Signature_2013_final_fancy_100_fb.png"/>
		
		
		
		<link rel="stylesheet" href="css/supersized.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="theme/supersized.shutter.css" type="text/css" media="screen" />
		<link rel="stylesheet" href="css/bvg.css" type="text/css" media="screen" />
		<link rel="shortcut icon" href="/favicon.ico" type="image/x-icon"/>
		
		
		<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js"></script>
		<script type="text/javascript" src="js/jquery.easing.min.js"></script>
		
		<script type="text/javascript" src="js/supersized.3.2.6.min.js"></script>
		<script type="text/javascript" src="theme/supersized.shutter.min.js"></script>
		<script src="http://tab-slide-out.googlecode.com/files/jquery.tabSlideOut.v1.3.js"></script>
		
		<script type="text/javascript">
		    $(function(){
		        $('.slide-out-div').tabSlideOut({
		            tabHandle: '.about-us-slider',                     //class of the element that will become your tab
		            pathToTabImage: '/img/About-Tab_alpha70.png', //path to the image for the tab //Optionally can be set using css
		            imageHeight: '120px',                     //height of tab image           //Optionally can be set using css
		            imageWidth: '40px',                       //width of tab image            //Optionally can be set using css
		            tabLocation: 'right',                      //side of screen where tab lives, top, right, bottom, or left
		            speed: 300,                               //speed of animation
		            action: 'click',                          //options: 'click' or 'hover', action to trigger animation
		            topPos: '60px',                          //position from the top/ use if tabLocation is left or right
		            leftPos: '20px',                          //position from left/ use if tabLocation is bottom or top
		            fixedPosition: false                      //options: true makes it stick(fixed position) on scroll
		        });
		
		    });
		    
		    
		   
	
		    $(window).load(function () {
				//Automatically open the slide out tab
				var timer = setTimeout(function(){
					$('.about-us-slider').click();
				},100);
				
				//Creating an x to close the about slider
				 $('.slide-out-x').click(function() {
					 $('.about-us-slider').click();
			     });
				
				 $('.about-us-slider').click(function() {
					 clearTimeout(timer);
			     });
			});
		    
	    </script>
		<script type="text/javascript">
			
			jQuery(function($){
				
				$.supersized({
				
					// Functionality
					slideshow               :   1,			// Slideshow on/off
					autoplay				:	1,			// Slideshow starts playing automatically
					start_slide             :   0,			// Start slide (0 is random)
					stop_loop				:	0,			// Pauses slideshow on last slide
					random					: 	1,			// Randomize slide order (Ignores start slide)
					slide_interval          :   4000,		// Length between transitions
					transition              :   6, 			// 0-None, 1-Fade, 2-Slide Top, 3-Slide Right, 4-Slide Bottom, 5-Slide Left, 6-Carousel Right, 7-Carousel Left
					transition_speed		:	1000,		// Speed of transition
					new_window				:	0,			// Image links open in new window/tab
					pause_hover             :   0,			// Pause slideshow on hover
					keyboard_nav            :   1,			// Keyboard navigation on/off
					performance				:	1,			// 0-Normal, 1-Hybrid speed/quality, 2-Optimizes image quality, 3-Optimizes transition speed // (Only works for Firefox/IE, not Webkit)
					image_protect			:	1,			// Disables image dragging and right click with Javascript
															   
					// Size & Position						   
					min_width		        :   0,			// Min width allowed (in pixels)
					min_height		        :   0,			// Min height allowed (in pixels)
					vertical_center         :   1,			// Vertically center background
					horizontal_center       :   1,			// Horizontally center background
					fit_always				:	0,			// Image will never exceed browser width or height (Ignores min. dimensions)
					fit_portrait         	:   1,			// Portrait images will not exceed browser height
					fit_landscape			:   0,			// Landscape images will not exceed browser width
															   
					// Components							
					slide_links				:	false,	// Individual links for each slide (Options: false, 'num', 'name', 'blank')
					thumb_links				:	1,			// Individual thumb links for each slide
					thumbnail_navigation    :   0,			// Thumbnail navigation
					slides 					:  	[			// Slideshow Images
														<%
														for(Photo p : photoSet.getPhotos()){
													 	%>
														{image : '<%=p.getXxLarge()%>', title : '<%=p.getCreator()%>', thumb : '<%=p.getThumbUrl()%>'},
														<%
													  	}
													  	%>
												],
												
					// Theme Options			   
					progress_bar			:	0,			// Timer for each slide							
					mouse_scrub				:	0
					
				});
		    });
		    
		</script>

<style type="text/css">
span#logo {
	margin: 0px 0px 0px 0px;
}

div#logo-block {
	
}

div#footer-block {
	position: absolute;
	bottom: 0px;
}


.main A{
	color: #fff;
}


div#social_networks {
	position: absolute;
	right: 20px;
	bottom: 40px;
	width: 170px;
}

.slide-out-x {
	background-image: url('img/closebtn.png');
	width: 20px;
	height: 20px;
	right: 0px;
	margin-right: 10px;
	position: absolute;
	cursor: pointer;
}

.slide-out-div {
	position: relative;
}

#facebook-likebox{
	position: absolute;
	right: 0px;
	top: 0px;
}


</style>
		
	</head>
	
	<style type="text/css">
		ul#demo-block{ margin:0 15px 15px 15px; }
			ul#demo-block li{ margin:0 0 10px 0; padding:10px; display:inline; float:left; clear:both; color:#aaa; background:url('img/bg-black.png'); font:11px Helvetica, Arial, sans-serif; }
			ul#demo-block li a{ color:#eee; font-weight:bold; }
	</style>

<body>
<div id="fb-root"></div>
<script>(function(d, s, id) {
  var js, fjs = d.getElementsByTagName(s)[0];
  if (d.getElementById(id)) return;
  js = d.createElement(s); js.id = id;
  js.src = "//connect.facebook.net/en_US/all.js#xfbml=1";
  fjs.parentNode.insertBefore(js, fjs);
}(document, 'script', 'facebook-jssdk'));</script>

	<div id="logo-block">
		<span id="logo"><img src="/img/BvG_Signature_2013_final_fancy.png" /></span>
	</div>	
	
	
	<%@ include file="menu.jsp" %>

			<div class="slide-out-div">
				<a class="about-us-slider" href="http://link-for-non-js-users.html">Content</a>
				<div class="slide-out-x"></div>
				<div class="main">
					<h1>Boy vs Girl Photography</h1>
					<div>&nbsp;</div>
					<div>Boy vs Girl Photography is a husband and wife team dedicated to the art of beauty and portrait photography. &nbsp;We are always looking for ideas and for people to network and
						collaborate with. &nbsp;We both have full time non-photography related careers but we spend most of our free time crafting our skills in the art of all things photography.</div>
					<div>&nbsp;</div>
					<div>
						If you like our style and are interested in hiring<em> Boy vs Girl</em> for a shoot feel free to contact us with what you&#39;d like to do and we can discuss our rates. &nbsp;
					</div>
					<div>&nbsp;</div>
					<div>
						If you are a <strong>model</strong>,<strong> hair stylist</strong> or <strong>makeup artist</strong> interested in collaborating with <em>Boy vs Girl</em> just send us a message and we&rsquo;ll
						get back with you.&nbsp;
					</div>
					<div>&nbsp;</div>
					<div>Trevis and Chrissy Thomas</div>
					<div>Boy vs Girl Photography</div>
					<div>
						<a href="mailto:photographers@boyvsgirlphotography.com?subject=Contact%20BvG%20from%20About%20Us">photographers@boyvsgirlphotography.com</a>
					</div>
					<div>&nbsp;</div>
				</div>
			</div>


			<div id="social_networks">
				<a href="http://www.boyvsgirlphotography.com/plus" title="google plus page" target="_blank"><img src="http://www.boyvsgirlphotography.com/img/google-plus-48.png"></a> <a
					href="http://twitter.com/boyvsgirlphoto" title="follow us on twitter" target="_blank" wrc_done="true"><img src="http://www.boyvsgirlphotography.com/img/twitter-48.png"></a> <a
					href="http://www.facebook.com/boyvsgirlphotography" title="find us on Facebook" target="_blank"><img src="http://www.boyvsgirlphotography.com/img/facebook-48.png"></a>
			</div>
			
			<div id="facebook-likebox">
				<div class="fb-like" data-href="http://www.boyvsgirlphotography.com" data-send="true" data-width="450" data-show-faces="false" data-colorscheme="dark"></div>
			</div>



			<div class="footer" id="footer">
				<%@ include file="footer.jsp" %>
			</div>
			
	
	<!--End of styles-->

	<!--Thumbnail Navigation-->
	
	
	
	</body>
		
</html>
