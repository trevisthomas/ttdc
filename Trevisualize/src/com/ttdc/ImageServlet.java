package com.ttdc;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class ImageServlet extends HttpServlet {
    private static final Logger log = Logger.getLogger(ImageServlet.class.getName());
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
        String width = req.getParameter("width");
        String height = req.getParameter("height");
        String order = req.getParameter("order"); 
        String description = req.getParameter("description");
        String dateTaken = req.getParameter("dateTaken");
        String src = req.getParameter("src");
        String uuid = req.getParameter("id");
        
        if(user == null){
        	log.info("Hack attempted: " + title);
        	return;
        }
        
        if("/deleteImage".equals(req.getServletPath())){
        	ImageTool.delete(uuid);
        }
        else{
        	ImageTool.saveOrUpdate(user, uuid, order, title, url, src, width, height, description, dateTaken);
        }
        
        //saveImage(user, title, url, width, height);
        
        log.info("Image added by user " + user.getNickname() + ": " + title);
                
        
        resp.sendRedirect("/admin.jsp");
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