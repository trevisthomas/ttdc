package com.ttdc;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.repackaged.com.google.common.base.StringUtil;

public class ImageProcessorServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(ImageProcessorServlet.class.getName());
    public static final String ENTTY_NAME = "imageSet";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
    		throws ServletException, IOException {
    	doPost(req, resp);
    }
    
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
                throws IOException {
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();

        String title = req.getParameter("title");
        String url = req.getParameter("url");
        String order = req.getParameter("order"); 
        String description = req.getParameter("description");
        String dateTaken = req.getParameter("dateTaken");
        String src = req.getParameter("src");
        String uuid = req.getParameter("id");
        
        if("/deleteImage".equals(req.getServletPath())){
        	validate(user);
        	ImageTool.delete(uuid);
        }
        else if("/loadImage".equals(req.getServletPath())){
        	String qstr = req.getRequestURL().toString();
        	String name = URLDecoder.decode(qstr.substring(qstr.lastIndexOf("/")+1),"UTF-8");
        	
        	resp.setContentType("image/jpeg");
        	
        	Image image = ImageTool.getImage(uuid);
        	resp.addHeader("Cache-Control", "max-age=15");//Tells the client how often to check for an update. 
			String etag = image.getTitle().hashCode()+"-"+image.getDateAdded().hashCode();
		    String ifNoneMatch = req.getHeader("If-None-Match");
		    if (ifNoneMatch != null && ifNoneMatch.equals(etag)) {
		    	resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
		    	log.info("Instructing the browser to load "+uuid+" (ETag: "+etag+") from local cache.");
		    	return;
		    }
		    else{
		    	resp.setHeader("ETag", etag);
		    	log.info("Loading "+uuid+" (ETag: "+etag+") from the DB because client version is out of date.");
		    	ImageTool.readImage(uuid, resp.getOutputStream());
		    }
		    
		    
		    resp.flushBuffer();
		    
        	return;
        }
        else if("/saveImage".equals(req.getServletPath())){
        	ImageTool.saveOrUpdate(user, uuid, order, title, url, src, description, dateTaken);
        }
        else{
        	throw new RuntimeException("Nothing to do");
        }
        //saveImage(user, title, url, width, height);
        
        log.info("Image added by user " + user.getNickname() + ": " + title);
                
        
        resp.sendRedirect("/admin.jsp");

    }

//    catch (Throwable e) {
//		log.error(e);
//		response.setContentType("text/html");
//		out.println("<html>");
//		out.println("<head><title>Image Read Error</title></title>");
//		out.println("<body>");
//		out.println("<h1>Caught an exception reading image: "+name+"</h1>");
//		out.println("<p>"+e.getMessage()+"</p>");
//		out.println("</body></html>");
//	}
    
	private void validate(User user) {
		if(user == null){
			log.info("Hack attempted: ");
			throw new RuntimeException("You have to be logged in for that");
		}
	}

    

//	private void saveImage(User user, String title, String url, String width, String height) {
//		if(user == null){
//			return;
//		}
//		
//		 
//        Key key = KeyFactory.createKey("Image", ENTTY_NAME);
//        
//        Date date = new Date();
//        Entity greeting = new Entity("Image", key);
//        greeting.setProperty("user", user);
//        greeting.setProperty("id", getCounter()+1);
//        greeting.setProperty("date", date);
//        greeting.setProperty("title", title);
//        greeting.setProperty("url", url);
//        greeting.setProperty("width", width);
//        greeting.setProperty("height", height);
//
//        DatastoreService datastore =
//                DatastoreServiceFactory.getDatastoreService();
//        datastore.put(greeting);
//
//	}

}