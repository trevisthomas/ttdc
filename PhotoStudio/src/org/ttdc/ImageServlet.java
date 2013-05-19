package org.ttdc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



public class ImageServlet extends HttpServlet {
	private static final Logger log = Logger.getLogger(ImageServlet.class.toString());
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doBoth(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doBoth(req, resp);
	}
	
	private void doBoth(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String name = "";
		ServletOutputStream out = null;
		try {
			out = response.getOutputStream();
			String qstr = request.getRequestURL().toString();
			name = UrlEncoder.decode(qstr.substring(qstr.lastIndexOf("/")+1));
			
			String prefix = qstr.substring(0, qstr.lastIndexOf("/"));
			
			String who = UrlEncoder.decode(prefix.substring(prefix.lastIndexOf("/")+1));
			response.setContentType("image/jpeg");
			
			FolderMonitor fm = ImagesServlet.getFolderMonitor(who);
			
			if(fm == null){
				throw new RuntimeException("I got confused. Cant find: " + who + " using query: " + qstr);
			}
			
			String fileName = fm.getPath() + "/"+ name;
			
			
			
//			File file = new File(fileName);
			
			response.addHeader("Cache-Control", "max-age=15");//Tells the client how often to check for an update. 
//			String etag = image.getName().hashCode()+"-"+image.getDate().hashCode();
			String etag = fileName;
		    String ifNoneMatch = request.getHeader("If-None-Match");
		    if (ifNoneMatch != null && ifNoneMatch.equals(etag)) {
		    	response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
		    	log.info("Instructing the browser to load "+name+" (ETag: "+etag+") from local cache.");
		    	return;
		    }
		    else{
		    	response.setHeader("ETag", etag);
		    	log.info("Loading "+name+" (ETag: "+etag+") from the DB because client version is out of date.");
		    	loadImage(fileName, response);  
		    }
//		    			
		} catch (Throwable e) {
//			log.error(e);
			response.setContentType("text/html");
			out.println("<html>");
			out.println("<head><title>Image Read Error</title></title>");
			out.println("<body>");
			out.println("<h1>Caught an exception reading image: "+name+"</h1>");
			out.println("<p>"+e.getMessage()+"</p>");
			out.println("</body></html>");
		}
	}
	
	private void loadImage(String file, HttpServletResponse response) throws IOException{
		FileInputStream in = new FileInputStream(file);
	    
	    byte[] bytes = new byte[1024*1024];
	    
	    while( (in.read(bytes)) != -1){
	    	response.getOutputStream().write(bytes);
	    }
	}
	
	
}
