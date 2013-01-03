<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@page import="com.bvg.*"%>
<%response.setContentType("text/xml"); %>
<%	
	ZenfolioService zenfolio = new ZenfolioService();
	//143596818 - chrissy's showcase
	//31637159 - trevis showcase
	
	PhotoSet photoSet = zenfolio.loadPhotoSet("143596818");
%>
<rss version="2.0" xmlns:media="http://search.yahoo.com/mrss/" xmlns:atom="http://www.w3.org/2005/Atom">
    <channel>
        <title>Chrissy's Showcase: Boy vs Girl Photography</title>
        <link>http://www.boyvsgirlphotography.com/showcase.jsp?who=chrissy</link> 
        <description>Chrissy's favorite images.</description>
        <language>en-us</language> 
        <copyright>(C) Boy vs Girl Photography</copyright>
        <managingEditor>photographers@boyvsgirlphotography.com (Boy vs Girl Photography)</managingEditor>
        <pubDate><%= photoSet.getCreatedOn() %></pubDate>
        <lastBuildDate><%= photoSet.getModifiedOn() %></lastBuildDate>
        
      <image>
            <url><%=photoSet.getPhotos().get(0).getSmallerThumbUrl() %></url>
            <title>Chrissy's Showcase: Boy vs Girl Photography</title>
            <link>http://www.boyvsgirlphotography.com/showcase.jsp?who=chrissy</link>
      </image>

<%
for(Photo photo : photoSet.getPhotos()){
	//Photo photo = zenfolio.loadPhoto(p.getUniqueName()); //Reloading image for caption.  If they ever fix the loadPhotoSet method to populate the damned caption this wouldnt be needed.
	String permaLink = "http://www.boyvsgirlphotography.com/showcase.jsp?who=chrissy&amp;show="+photo.getXxLarge();
%>
	    <item>
            <title><![CDATA[<%=photo.getTitle()%>]]></title> 
            <link><%=permaLink%></link> 
            <description><![CDATA[<p><a href="<%=permaLink%>"><img src="<%=photo.getXLarge()%>"/></a></p>]]><![CDATA[<p><%=photo.getCaption()%></p>]]></description>
            <author>photographers@boyvsgirlphotography.com (Boy vs Girl Photography)</author>
          	<media:thumbnail url="<%=photo.getThumbUrl()%>"/>
          	<media:content url="<%=photo.getXxLarge()%>" type="image/jpeg" medium="image"/>
          	<media:title><![CDATA[<%=photo.getTitle()%>]]></media:title>
          	<guid isPermaLink="true"><%=photo.getUniqueName()%></guid>
            <pubDate><%=photo.getTakenOn() %></pubDate>
        </item>
<%
}
%>
        
  </channel>
</rss>
