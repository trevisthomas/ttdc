package org.ttdc.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.ttdc.biz.network.services.ImageService;
import org.ttdc.persistence.PopulateCache;
import org.ttdc.persistence.objects.Image;
import org.ttdc.persistence.objects.ImageFull;
import org.ttdc.util.UrlEncoder;

/**
 * 
 * @author Trevis
 *
 */
public class ImageServer extends HttpServlet {
	/**
	 * 
	 */
	
	private static final long serialVersionUID = -5063269479224216779L;
	private static final Logger log = Logger.getLogger(ImageServer.class);
	
	
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {
		doBoth(request,response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)	throws ServletException, IOException {
		doBoth(request,response);
	}
	
	private void doBoth(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		String name = "";
		ServletOutputStream out = null;
		try {
			out = response.getOutputStream();
			String qstr = request.getRequestURL().toString();
			name = UrlEncoder.decode(qstr.substring(qstr.lastIndexOf("/")+1));
			response.setContentType("image/jpeg");
			ImageService service = ImageService.getInstance();
			
			Image image = service.readImage(name);
			response.addHeader("Cache-Control", "max-age=15");//Tells the client how often to check for an update. 
			String etag = image.getName().hashCode()+"-"+image.getDate().hashCode();
		    String ifNoneMatch = request.getHeader("If-None-Match");
		    if (ifNoneMatch != null && ifNoneMatch.equals(etag)) {
		    	response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
		    	log.info("Instructing the browser to load "+name+" (ETag: "+etag+") from local cache.");
		    	return;
		    }
		    else{
		    	response.setHeader("ETag", etag);
		    	log.info("Loading "+name+" (ETag: "+etag+") from the DB because client version is out of date.");
		    	ImageFull imageFull = service.readFullImage(name, out);  
		    }
		    			
		} catch (Throwable e) {
			log.error(e);
			response.setContentType("text/html");
			out.println("<html>");
			out.println("<head><title>Image Read Error</title></title>");
			out.println("<body>");
			out.println("<h1>Caught an exception reading image: "+name+"</h1>");
			out.println("<p>"+e.getMessage()+"</p>");
			out.println("</body></html>");
		}
	}
	
}