package com.ttdc;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.repackaged.com.google.common.base.StringUtil;

public class ImageTool {
	private static final Logger log = Logger.getLogger(ImageTool.class.getName());
	private static long PAGE_SIZE = 5;
	
	public static List<Image> fetchAll(){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Query q = pm.newQuery(Image.class);
		q.setOrdering("dateAdded desc");
		List<Image> result = (List<Image>) q.execute();
		
		List<Image> all = new ArrayList<Image>();
		all.addAll(result);

		return all;
	}
	
	public List<Image> fetch(String pageParam){
		
//		int count = getCount();
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Query q = pm.newQuery(Image.class);
		
//		q.setResult("count(this)");
//		long count = (Long)q.execute();
		long page = 1;
		if(pageParam != null){
			page = Long.parseLong(pageParam); 
		}
		
		q.setOrdering("order desc");
		long start = (page - 1) * PAGE_SIZE;
		q.setRange(start , start + PAGE_SIZE);
		List<Image> result = (List<Image>) q.execute();
		
		List<Image> all = new ArrayList<Image>();
		all.addAll(result);
		
		pm.close();
		return all;
	}
	
	public boolean hasLess(String pageParam){
		long page = 1;
		if(pageParam != null){
			page = Long.parseLong(pageParam);	
		}
		if(page > 1){
			return true;
		}
		else{
			return false;
		}
	}
	
	
	
	public boolean hasMore(String pageParam){
		long page = 1;
		if(pageParam != null){
			page = Long.parseLong(pageParam);	
		}
		int count = getCount();
		if(page * PAGE_SIZE < count){
			return true;
		}
		else{
			return false;
		}
	}
	
	
	public long nextPage(String pageParam){
		long page = 1;
		if(pageParam != null){
			page = Long.parseLong(pageParam);	
		}
		return ++page;
	}
	
	public long prevPage(String pageParam){
		long page = 1;
		if(pageParam != null){
			page = Long.parseLong(pageParam);	
		}
		if(page > 1){
			return --page;
		}
		else{
			return 1;
		}
	}
	
	
	public static Image getImage(String id) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {

			Key key = KeyFactory.createKey(Image.class.getSimpleName(), id);
			Image image = (Image) pm.getObjectById(Image.class, key);
			return image;
		} finally {
			pm.close();
		}

	}
	
	public static int getCount(){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query q = pm.newQuery(Image.class);
		q.setResult("count(this)");
		int count = (Integer)q.execute();
		return count;
	}
	
//	public static void saveOrUpdate(User user, String id, String order, String title, String url, String src,
//			String width, String height, String description, String dateTaken) {
//    	
//    	PersistenceManager pm = PMF.get().getPersistenceManager();
//    	
//    	Image image = new Image();
//    	image.setCreator(user.getEmail());
//    	image.setDateAdded(new Date());
//    	image.setDateTaken(dateTaken);
//    	image.setDescription(description);
//    	
//    	if(StringUtil.isEmpty(id) ){
//    		UUID uuid = UUID.randomUUID();
//    		id = uuid.toString();
//    	}
//    	Key key = KeyFactory.createKey(Image.class.getSimpleName(), id);
//    	image.setKey(key);
//    	image.setHeight(new Integer(height));
//    	image.setOrder(new Integer(order));
//    	image.setTitle(title);
//    	image.setWidth(new Integer(width));
//    	image.setUrl(url);
//    	image.setSrc(src);
//    	
//    	 try {
//             pm.makePersistent(image);
//         } finally {
//             pm.close();
//         }
//		
//	}
	
	public static Image saveOrUpdate(User user, String id, String order, String title, String url, String src, 
			String description, String dateTaken, String purchaseUrl) {
    	
		Image image;
		if(src != null){
			image = createImage(src);
		}
		else{
			image = getImage(id);
		}
    	saveOrCreate(user, id, order, title, url, description, dateTaken, purchaseUrl, image);
    	return image;
		
	}
	
	public static Image saveOrUpdate(User user, String id, String order, String title, String url, Image image, 
			String description, String dateTaken, String purchaseUrl) {
    	
//    	Image image = createImage(in);
    	saveOrCreate(user, id, order, title, url, description, dateTaken, purchaseUrl, image);
    	return image;
	}


	private static void saveOrCreate(User user, String id, String order,
			String title, String url, String description, String dateTaken, String purchaseUrl,
			Image image) {
		image.setCreator(user.getEmail());
		image.setDateAdded(new Date());
		image.setDateTaken(dateTaken);
		image.setDescription(description);
		image.setPurchaseUrl(purchaseUrl);
		
		if(StringUtil.isEmptyOrWhitespace(id) ){
			UUID uuid = UUID.randomUUID();
			id = uuid.toString();
		}
		Key key = KeyFactory.createKey(Image.class.getSimpleName(), id);
		image.setKey(key);
		image.setOrder(new Integer(order));
		image.setTitle(title);
		image.setUrl(url);
		//image.setSrc(src);
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
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
	
	public static void readImage(String id, OutputStream out){
		Image image = getImage(id);
		if(image == null){
			log.warning("Failed to find image: " + id);
			return;
		}
		try {
			out.write(image.getBlob().getBytes(), 0, image.getBlob().getBytes().length);
			
		} catch (IOException e) {
			log.warning(e.toString());
			throw new RuntimeException(e);
		} 
	}
	
	private static Image createImage(String urlStr){
		try	{
			
			if(urlStr == null){
				throw new RuntimeException("Invalid URL");
			}
			
			
			URL url = new URL(urlStr);
			URLConnection con = url.openConnection();
			con.connect();
			
			Image i = createImage(con.getInputStream());

			return i;
		}
		catch(MalformedURLException e){
			throw new RuntimeException("Url is malformed.");
		}
		catch(FileNotFoundException e){
			throw new RuntimeException("Image could not be loaded from url.");
		}
		catch (Throwable t){
			log.info(t.toString());
			throw new RuntimeException(t);
		}
	}


	public static Image createImage(InputStream in) {
		try {
			BufferedInputStream bufferedIn = new BufferedInputStream(in);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int c;
			while ((c = bufferedIn.read()) != -1) {
				out.write((char) c);
			}
			out.flush();

			byte[] bytes = out.toByteArray();
			Image i = new Image();
			com.google.appengine.api.images.Image gImage = ImagesServiceFactory
					.makeImage(bytes);
			i.setHeight(gImage.getHeight());
			i.setWidth(gImage.getWidth());

			Blob blob = new Blob(bytes);
			i.setBlob(blob);
			return i;
		} catch (IOException e) {
			log.warning(e.getMessage());
			throw new RuntimeException(e);
		}
	}
}
