package org.ttdc.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.ttdc.persistence.PopulateCache;

public class HelloWorld extends HttpServlet  {
	private static final Logger log = Logger.getLogger(HelloWorld.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {
		doBoth(request,response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {
		doBoth(request,response);
	}
	
	private void doBoth(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		response.setContentType("text/html");
		PrintWriter pw = response.getWriter();
		pw.println("<html>");
		pw.println("<head><title>Hello World</title></title>");
		pw.println("<body>");
		pw.println("<h1>Hello World</h1><p>Hello servlet is working</p>");
		pw.println("</body></html>");
	}
	@Override
	public void init() throws ServletException {
		log.info("Building cache");
		PopulateCache.init();
		log.info("Cache load complete.");
	}
}
