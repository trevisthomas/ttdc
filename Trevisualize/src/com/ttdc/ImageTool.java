package com.ttdc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.repackaged.com.google.common.base.StringUtil;

public class ImageTool {
	private static final Logger log = Logger.getLogger(ImageTool.class.getName());
	public List<Image> fetchAll(){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Query q = pm.newQuery(Image.class);
		q.setOrdering("dateAdded desc");
		List<Image> result = (List<Image>) q.execute();
		
		List<Image> all = new ArrayList<Image>();
		all.addAll(result);

		return all;
	}
	
	
	public static Image getImage(String id){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Key key = KeyFactory.createKey(Image.class.getSimpleName(), id);
		Image image = (Image)pm.getObjectById(Image.class, key);
		return image;
	}
	
	public static int getCount(){
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		com.google.appengine.api.datastore.Query query = new com.google.appengine.api.datastore.Query(Image.class.getSimpleName(), null);
	    int count = datastore.prepare(query).countEntities(FetchOptions.Builder.withChunkSize(5));
	    return count;
	}
	
	public static void saveOrUpdate(User user, String id, String order, String title, String url, String src,
			String width, String height, String description, String dateTaken) {
    	
    	PersistenceManager pm = PMF.get().getPersistenceManager();
    	
    	Image image = new Image();
    	image.setCreator(user.getEmail());
    	image.setDateAdded(new Date());
    	image.setDateTaken(dateTaken);
    	image.setDescription(description);
    	
    	if(StringUtil.isEmpty(id) ){
    		UUID uuid = UUID.randomUUID();
    		id = uuid.toString();
    	}
    	Key key = KeyFactory.createKey(Image.class.getSimpleName(), id);
    	image.setKey(key);
    	image.setHeight(new Integer(height));
    	image.setOrder(new Integer(order));
    	image.setTitle(title);
    	image.setWidth(new Integer(width));
    	image.setUrl(url);
    	image.setSrc(src);
    	
    	 try {
             pm.makePersistent(image);
         } finally {
             pm.close();
         }
		
	}


	public static void delete(String id) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Key key = KeyFactory.createKey(Image.class.getSimpleName(), id);
		Image image = (Image)pm.getObjectById(Image.class, key);
		
		log.info("Attempting to delete: " + id + "="+ image.getTitle());
		try{
			pm.deletePersistent(image);
		} finally {
            pm.close();
        }
		
	}
}
