<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@page import="com.bvg.*"%>

<!DOCTYPE html
  PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
  "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
	xmlns:zf="http://www.zenfolio.com/xml/page-schema" xml:lang="en">
<!-- User-Agent: Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0) -->
<!-- UA Code: ie9 -->
<!-- Platform Code: windows -->
<!-- Country Code: US -->
<head>
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<title>Boy vs Girl Photography | Trevis</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta http-equiv="imagetoolbar" content="no" />
<meta name="description"
	content="Who is Boy vs Girl Photography, Trevis Thomas, Chrissy Thomas" />
<meta property="og:type" content="article" />
<meta property="og:site_name" content="Boy vs Girl Photography" />
<meta property="og:url"
	content="http://photos.boyvsgirlphotography.com/about" />
<meta property="og:title" content="About" />
<meta property="og:description"
	content="Who is Boy vs Girl Photography, Trevis Thomas, Chrissy Thomas" />
<script type="text/javascript">
	//<!--
	if (self != top && self.name.indexOf('zenfolio') != 0)
		top.location.href = location.href;
//-->
</script>
<link rel="stylesheet"
	href="http://cdn.zenfolio.net/zf/css/en-US/ie9/windows/52C3MPK7FQ2PU/layout.css"
	type="text/css" />
<link rel="Shortcut Icon" href="/img/s11/v32/u646298236-81.ico" />
<link rel="stylesheet"
	href="http://cdn.zenfolio.net/zf/theme/en-US/ie9/windows/678838PAD7XXK/418V/M/custom/custom.css"
	type="text/css" />
<script
	src="http://cdn.zenfolio.net/zf/script/en-US/ie9/windows/5HYFNCQA7YJ37/layout.js"
	type="text/javascript">
	
</script>



<link rel="stylesheet" href="css/style_masonry.css" />
<link rel="stylesheet" href="css/lightbox.css" />
<link rel="stylesheet" href="css/bvg.css" />

<style>
	.box{background: #333;}
	
	a {
	  color: #ccc;
	  text-decoration: none;
	  font-weight: bold;
	}
	
	a:hover {
	  color: #aaa;
	}
	
	a:active {
	  background: hsla( 0, 100%, 100%, 0.5 );
	}

</style>
</head>
<body style="overflow:auto; min-height:100%;">
	
	<section id="content">

	<div id="page-frame">

		
			<div id="header"
				class="header header-bgcolor1 header-bgimage1 header-border1 header-font1 header-left">

				<div class="header-inner header-search-compact">

					<div class="header-main">






						<div class="header-menu header-font1">



							<ul>



								<li class="header-link"><a class="header-color1"
									data-zf-index="1" href="http://www.boyvsgirlphotography.com">Home</a>



								</li>




								<li class="header-link"><span
									class="header-separator header-color6">|</span> <a
									class="header-color1" data-zf-index="2" href="/trevis.jsp">Trevis'
										Showcase</a></li>




								<li class="header-link"><span
									class="header-separator header-color6">|</span> <a
									class="header-color1" data-zf-index="3" href="/chrissy.jsp">Chrissy's
										Showcase</a></li>


								<li class="header-link"><span
									class="header-separator header-color6">|</span> <a
									class="header-color1" data-zf-index="4" href="http://photos.boyvsgirlphotography.com/shop">Shop</a>
								</li>


								<li class="header-link"><span
									class="header-separator header-color6">|</span> <a
									class="header-color1" data-zf-index="5" href="/about">About</a>
								</li>


								<li class="header-link"><span
									class="header-separator header-color6">|</span> <a
									class="header-color1" data-zf-index="6" href="/contact.html">Contact</a>
								</li>

							</ul>

						</div>





						<div class="header-photog" style="width: 130px;">

							<a class="header-photog-logo"
								href="http://www.boyvsgirlphotography.com"
								title="BoyVsGirlPhotography"
								style="width: 130px; height: 40px; margin: 0px; background-image: url(http://cdn.zenfolio.net/img/s11/v36/u603512342-71.png);">
							</a>


						</div>



					</div>


					<div class="header-search">
						<div
							class="header-search-slider header-bgcolor2 header-bgimage2 collapsed"
							id="header-search-slider">
							<div id="header_SearchInput" class="searchi searchi-local"
								onmouseout="_zf_header_SearchInput._onmouseout(event)">
								<div class="searchi-left"></div>
								<div class="searchi-right"></div>
								<div class="searchi-top"></div>
								<div class="searchi-inner header-bgcolor3">
									<input id="header_SearchInput-input" type="text" value="SEARCH"
										class="searchi-input header-bgcolor3 header-color3"
										onfocus="_zf_header_SearchInput._input_onfocus(this)"
										onblur="_zf_header_SearchInput._input_onblur(this)"
										onkeydown="_zf_header_SearchInput._input_onkeydown(event)" />
									<div class="searchi-icon"
										onclick="_zf_header_SearchInput.search('local')"></div>
								</div>
								<div class="searchi-bottom"></div>
							</div>
							<div class="header-search-icon"></div>
						</div>
					</div>


				</div>

			</div>

	<%	ZenfolioService zenfolio = new ZenfolioService();

	String str = request.getParameter("pageNumber");
	int pageNumber = 1;
	if(str != null){
		//pageNumber = Integer.valueOf((String)obj);
		pageNumber = Integer.valueOf(str);
	}
	
	//To switch the page, make sure that you get the title at the top, and the jsp link in the nav so that the more work.
	//143596818 - chrissy's showcase
	//31637159 - trevis showcase
	PhotoSet photoSet = zenfolio.loadPhotoSetPhotos("31637159", pageNumber); //20547770
	%>
		
	</div>

 
	<div id="container" class="clearfix">

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
 


	</div>
	

	<nav id="page-nav"> 
		<a href="trevis.jsp?pageNumber=<%=pageNumber+1%>"> Next </a> 
	</nav>


	</section>
	
	
	<!-- #content -->

	<script src="js/jquery-1.7.2.min.js"></script>
	<script src="js/jquery.masonry.min.js"></script>
	<script src="js/jquery.infinitescroll.min.js"></script>
	<script src="js/lightbox_resize.js"></script>
	<script>
		var $container = $('#container');

		$container.imagesLoaded(function() {
			$container.masonry({
				itemSelector : '.box',
				isAnimated : true,
				isFitWidth : true
			});
		});

		$container.infinitescroll({
			navSelector : '#page-nav', // selector for the paged navigation 
			nextSelector : '#page-nav a', // selector for the NEXT link (to page 2)
			itemSelector : '.box', // selector for all items you'll retrieve
			loading : {
				finishedMsg : 'No more pages to load.',
				img : 'http://i.imgur.com/6RMhx.gif'
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

	
<div class="footer footer-border1 footer-bgcolor1 footer-color1 footer-font1 footer-bgimage1 footer-mod" id="footer" style="visibility: inherit;">

    <div class="footer-copyright footer-font2">
    </div>
     
     <div id="footer-block">
    	<div id="footer"><a href="http://www.boyvsgirlphotography.com" target="_blank">© 2013 Boy Vs Girl Photography</a> | Trevis &amp; Chrissy Thomas</div>		
    </div>
    
</div>


	<div id="ctl04" class="prefetch"></div>
	<script type="text/javascript">
		//<!--
		zf_locale = 'en-US';
		zf_userId = 0;
		zf_userName = '';
		zf_userDomain = '';
		zf_currentHost = 'photos.boyvsgirlphotography.com';
		zf_defaultHost = 'www.zenfolio.com';
		zf_secureHost = 'secure.zenfolio.com';
		zf_defaultDomain = '.zenfolio.com';
		zf_cdnHost = 'cdn.zenfolio.net';
		zf_apiHost = 'api.zenfolio.com';
		zf_imageHosts = [ "img-a.cdn.zenfolio.net", "img-b.cdn.zenfolio.net",
				"img-c.cdn.zenfolio.net", "img-d.cdn.zenfolio.net" ];
		zf_imageVS = [ 9, 2, 10, 11, 2, 1, 1, 3, 3, 4, 4, 4, 4, 9, 9, 9, 10,
				10, 10, 1, 1, 1, 1, 3, 3, 3, 3, 11, 11, 11, 11, 11, 11, 11, 11,
				11, 11, 11, 3, 3, 3, 3, 3, 3, 3, 3, 1, 1, 1, 1, 2, 2, 2, 2, 1,
				1, 1, 1, 2, 2, 2, 2, 4, 4, 4, 4, 4, 4, 4, 4, 2, 2, 2, 2 ];
		_zf_stdSetTimezoneOffset();
		zf_clientIp = '24.107.204.117';
		zf_NumberFormat.current = _$({
			"$meta" : [ [ "NumberFormat", "currencyDecimalDigits",
					"currencyDecimalSeparator", "currencyGroupSeparator",
					"currencyGroupSizes", "currencySymbol", "negativeSign",
					"numberDecimalDigits", "numberDecimalSeparator",
					"numberGroupSeparator", "numberGroupSizes",
					"numberNegativePattern" ] ],
			"$root" : {
				"$obj" : [ 0, 2, ".", ",", [ 3 ], "$", "-", 2, ".", ",", [ 3 ],
						1 ]
			}
		});
		var _zfl_init = {};
		zf_userId = 0;
		zf_ownerId = '482142300';
		zf_ownerName = 'boyvsgirlphotography';
		zf_ownerDomain = 'photos.boyvsgirlphotography.com';
		_zf_stdRegisterDelayLoad('/zf/script/en-US/ie9/windows/34R8A2U26PHU6/configurator.js');
		_zf_stdRegisterDelayLoad('/zf/script/en-US/ie9/windows/Y2SPG5P1MWXV/jqueryui.js');
		_zf_stdRegisterDelayLoad('/zf/script/en-US/ie9/windows/41YK42JVKC637/scrapbook.js');
		zfl_SessionCookie.current = new zfl_SessionCookie(
				'HqU/8TRpvh7W2mqtKSYtwfwN');
		zfl_PasswordManager.init([]);
		_zfl_ctl02_init = {
			columns : {},
			bins : {},
			grids : {},
			data : {},
			pairs : {}
		};
		zfl_Layout.suppressClick()
		_zf_cartDefaultListTimestamp = 'dD4QCIE2kBwLIhcAFHlLQ68wyis=';
		var _zf_zenbar_GoToCart = new zfl_Button('zenbar_GoToCart', null);
		var _zf_zenbar_Configurator = new zfp_Configurator(
				'zenbar_Configurator', 0, '2GXJ11GQ2SA15', _$(null));
		var _zf_zenbar = new zfl_ZenBar('zenbar', _$({
			"$meta" : [ [ "CartSummary", "changeNumber", "size", "incomplete",
					"productPrice" ] ],
			"$root" : {
				"$obj" : [ 0, 0, 0, false, 0 ]
			}
		}), _zf_zenbar_Configurator, _$({
			"$meta" : [ [ "zfl_DecorationInfo", "className", "borderSize" ] ],
			"$root" : {
				"$obj" : [ 0, "prodcat", 4 ]
			}
		}), false);
		zfl_Mat.current = new zfl_Mat('mat-frame', 0, 0, 'ctl02');
		var _zf_header_SearchInput = new zfl_SearchInput('header_SearchInput',
				null, '/');
		var _zf_header = new zfl_Header('header', true);
		var _zf_ctl02_Center = new zfl_Column('ctl02_Center', null, [], false);
		var _zf_ctl02_ctl03 = new zfl_Copyright('ctl02_ctl03');
		var _zf_footer = new zfl_Footer('footer');
		var _zf_ctl05_Add = new zfl_Button('ctl05_Add', null);
		var _zf_ctl05_Cancel = new zfl_Button('ctl05_Cancel', null);
		var _zf_ctl05 = new zfb_ScrapbookAddShared('ctl05', _zf_ctl05_Add);
		var _zf_ctl06_Create = new zfl_Button('ctl06_Create', null);
		var _zf_ctl06_Cancel = new zfl_Button('ctl06_Cancel', null);
		var _zf_ctl06 = new zfb_ScrapbookCreate('ctl06', _zf_ctl06_Create);
		var _zf_ctl03 = new zfb_ScrapbookEdit('ctl03', _$(null), _$(null),
				482142300, true, true, 'zf_csb_boyvsgirlphotography',
				_zf_ctl05, _zf_ctl06);
		var _zf_ctl04 = new zf_Prefetch('ctl04', 18);
		_zfl_init.decorations = {
			"$meta" : [ [ "zfl_DecorationInfo", "className", "borderSize",
					"shadowType", "imagePad", "extraSize" ] ],
			"$objects" : [ [ 0, "square", 2, "system", null, null ] ],
			"$root" : {
				"DimLights" : {
					"$obj" : [ 0, "dimlights-decoration", 2, "system", null,
							null ]
				},
				"Slideshow" : {
					"$obj" : [ 0, "slideshow-decoration", null, "system", null,
							null ]
				},
				"default" : {
					"$obj" : [ 0, "defdec", 1, null, null, null ]
				},
				"CommentBrowser" : {
					"$obj" : [ 0, "comdec", 8, null, true, null ]
				},
				"CurrentPhoto" : {
					"$obj" : [ 0, "largeimage", 1, null, null, 8 ]
				},
				"PhotoNavList.1" : {
					"$ref" : 0
				},
				"PhotoVList.10" : {
					"$obj" : [ 0, "square", 2, "system", true, null ]
				},
				"PhotoNavThumbnail.1" : {
					"$ref" : 0
				},
				"BioPhoto" : {
					"$obj" : [ 0, "bioimage", 4, null, null, null ]
				},
				"PageSlideshow" : {
					"$obj" : [ 0, "psdec", 9, null, null, null ]
				},
				"ProductCategory" : {
					"$obj" : [ 0, "prodcat", 4, null, null, null ]
				}
			}
		};
		_zfl_ctl02_init.analytics = new zfl_Analytics(false, '', null, null);
		_zfl_ctl02_init.fixedWidth = false;
		_zfl_init.zenbar = _zf_zenbar;
		_zfl_init.header = _zf_header;
		_zfl_ctl02_init.columns.Center = _zf_ctl02_Center;
		_zfl_ctl02_init.fillers = [ 'ctl02_ctl02' ];
		_zfl_init.footer = _zf_footer;
		_zfl_init.scrapbook = _zf_ctl03;
		var _zf_ctl02 = new zfl_Layout('ctl02', _zfl_init, _zfl_ctl02_init);
	//-->
	</script>
</body>
</html>
