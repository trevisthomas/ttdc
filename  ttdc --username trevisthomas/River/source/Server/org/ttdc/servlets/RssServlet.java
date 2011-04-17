package org.ttdc.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.ttdc.util.UrlEncoder;

public class RssServlet extends HttpServlet{
	private static final long serialVersionUID = 8141810843916333107L;
	private static final Logger log = Logger.getLogger(RssServlet.class);
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {
		doBoth(request,response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {
		doBoth(request,response);
	}
	
	private void doBoth(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		ServletOutputStream out = null;
		String feedIdentifier = "";
		try{
			out = response.getOutputStream();
			final String qstr = request.getRequestURL().toString();
			feedIdentifier = UrlEncoder.decode(qstr.substring(qstr.lastIndexOf("/")+1));
			RssFeedGenerator.buildFeed(out, feedIdentifier);
			
		} catch (Throwable e) {
			log.error(e);
			response.setContentType("text/html");
			out.println("<html>");
			out.println("<head><title>Image Read Error</title></title>");
			out.println("<body>");
			out.println("<h1>Caught an exception generating RSS: "+feedIdentifier+"</h1>");
			out.println("<p>"+e.getMessage()+"</p>");
			out.println("</body></html>");
		}
	}
}
