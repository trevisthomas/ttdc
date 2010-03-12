package org.ttdc.servlets;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

import javax.swing.ImageIcon;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Image;
import org.ttdc.persistence.objects.ImageFull;
import org.ttdc.persistence.objects.Person;
import org.ttdc.util.ImageUtils;
import org.ttdc.util.ServiceException;

public class ImageService {
	private static final Logger log = Logger.getLogger(ImageService.class);
	public static final int QUARE_IMAGE_SIZE = 50;
	
	private ImageService() {};

	public static ImageService getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder {
		private final static ImageService INSTANCE = new ImageService();
	}

	/**
	 * Reads an image from the database and streams the content into an output stream
	 * 
	 * @param name
	 * @param out
	 * @return
	 * @throws ServiceException
	 */
	public ImageFull readFullImage(String name, OutputStream out)
			throws ServiceException {

		ImageFull image = null;
		try {
			InputStream in = null;
			Session session = Persistence.beginSession();
			
			
			//Check to see if we're looking for the square
			boolean square = false;
			int index = name.indexOf(Image.SQUARE_THUMBNAIL_SUFFIX);
			if(index > 0){
				square = true;
				name = name.replaceAll(Image.SQUARE_THUMBNAIL_SUFFIX, "");
			}
				
			Query query = session.getNamedQuery("imageFull.getByName").setString("name", name);
			image = (ImageFull) query.uniqueResult();
			Blob blob = null;
			if(square){
				blob = image.getSqareImage();
			}
			else{
				blob = image.getImage();
			}
			

			in = blob.getBinaryStream();

			int length = (int) blob.length();
			int bufferSize = 1024;
			byte[] buffer = new byte[bufferSize];
			while ((length = in.read(buffer)) != -1) {
				out.write(buffer, 0, length);
			}
			in.close();
			out.flush();
			session.close();
			
		} catch (SQLException e) {
			throw new ServiceException(e);
		} catch (IOException e) {
			throw new ServiceException(e);
		} catch (Throwable t) {
			throw new ServiceException(t);
		}

		return image;

	}
	
	/**
	 * Reads only the image meta data from the database.  The blob content is ignored and should 
	 * not be accessed in the returned image object outside of a hibernate session.  This method
	 * is first being used by the servlet to handle client side caching.  
	 * 
	 * @param name
	 * @return
	 * @throws ServiceException
	 */
	public Image readImage(String name) throws ServiceException {
		Image image = null;
		try {
			Session session = Persistence.beginSession();
			name = name.replaceFirst(Image.SQUARE_THUMBNAIL_SUFFIX, "");
			
			Query query = session.getNamedQuery("image.getByName").setString("name", name);
			image = (Image) query.uniqueResult();
			Persistence.commit();
		}catch (Throwable t) {
			throw new ServiceException(t);
		}
		return image;
	}
	public Image readImageById(String imageId) throws ServiceException {
		Image image = null;
		try {
			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("image.getById").setString("imageId", imageId);
			image = (Image) query.uniqueResult();
			Persistence.commit();
		}catch (Throwable t) {
			throw new ServiceException(t);
		}
		return image;
	}
	
	public ImageFull createImageAndSave(Person person, File file, String name) throws ServiceException {
		return createImageAndSave(person,file,name,false);
	}
	
	public ImageFull createImageAndSave(Person person, File file, String name,boolean withThumbnail) throws ServiceException {
		try{
			
			ImageFull i = createImage(person,file,name,withThumbnail);
			
			Session session = Persistence.beginSession();
			session.save(i);
			
			Persistence.commit();
			return i;
		}
		catch(ServiceException e){
			Persistence.rollback();
			throw e;
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	
	public ImageFull createImageAndSave(Person person, String urlStr, String prefix) throws ServiceException{
		try{
			ImageFull i = createImage(person,urlStr,prefix);
			Session session = Persistence.beginSession();
			session.save(i);
			Persistence.commit();
			return i;
		}
		catch(ServiceException e){
			Persistence.rollback();
			throw e;
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
		
	}
	public ImageFull createImage(Person person, File file, String name) throws ServiceException {
		return createImage(person, file, name, false);	
	}
	
	/**
	 * Create image takes an image file and makes the image object for it.  It's not saved to the DB
	 * yet though.  I do it this way so that the caller can manage the hibernate transaction.  
	 * 
	 * @param person
	 * @param file
	 * @param name
	 * @return
	 * @throws ServiceException
	 */
	public ImageFull createImage(Person person, File file, String name, boolean thumbnail) throws ServiceException {
		try {
			ImageFull i = new ImageFull();
			
			//Check for duplicate image name
			if(readImage(name) != null){
				throw new ServiceException("Image with this name already exists.");
			}
			
			FileInputStream in = new FileInputStream(file);
			
			/* 
			 * I could just pass the stream into the Hibernate.createBlob method
			 * but i need the byte array to create the ImageIcon to get the dimensions 
			 * and i cant get the bytes from the blob. It throws an IllegalState exception.
			 * So i just read all of the bytes at the start an then create the blob and the imageicon.
			 */
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int c;
			while ((c = in.read()) != -1) {
				out.write((char) c);
			}
			out.flush();
			
			//Blob blob = Hibernate.createBlob(in);
			byte[] bytes = out.toByteArray();
			Blob blob = Hibernate.createBlob(bytes);
			i.setImage(blob);

			i.setName(name);
			i.setOwner(person);
			ImageIcon image = new ImageIcon(bytes);
			if(image.getIconWidth() < 0) throw new ServiceException("The image is not an image.");
			i.setWidth(image.getIconWidth());
			i.setHeight(image.getIconHeight());
			
			in.close();
			
			if(thumbnail)
				createSquareThumbnail(i, bytes);
			
			return i;
			
		}
		catch(ServiceException e){
			throw e;	
		}
		catch (FileNotFoundException e) {
			log.error(e);
			throw new ServiceException(e);
		} 
		catch (IOException e) {
			log.error(e);
			throw new ServiceException(e);
		} 
		catch (Throwable t) {
			throw new ServiceException(t);
		}
	}
	
	/**
	 * Creates a blob image by loading the image from another website
	 * 
	 * @param person
	 * @param urlStr
	 * @param name - Name is just a prefix. The actual dot extension is parsed from the url.
	 * @return
	 * @throws ServiceException
	 */
	public ImageFull createImage(Person person, String urlStr, String prefix) throws ServiceException{
		try	{
			ImageFull i = new ImageFull();	
			String name = "";
			if(urlStr == null){
				throw new ServiceException("Invalid URL");
			}
			else if(prefix == null){
				name = urlStr.substring(urlStr.lastIndexOf('/')+1);
			}
			else{
				String extension = urlStr.substring(urlStr.lastIndexOf('.'));
				if(extension.length() > 5)
					name = prefix;	//In case the image has no dot extension.
				else
					name = prefix + extension;
			}
			URL url = new URL(urlStr);
			URLConnection con = url.openConnection();
			con.connect();
			BufferedInputStream in  = new BufferedInputStream(con.getInputStream());
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int c;
			while ((c = in.read()) != -1) {
				out.write((char) c);
			}
			out.flush();
			
			byte[] bytes = out.toByteArray();
			Blob blob = Hibernate.createBlob(bytes);
			i.setImage(blob);

			i.setName(name);
			i.setOwner(person);
			ImageIcon image = new ImageIcon(bytes);
			if(image.getIconWidth() < 0) throw new ServiceException("The image is not an image.");
			i.setWidth(image.getIconWidth());
			i.setHeight(image.getIconHeight());
			
			return i;
		}
		catch(ServiceException e){
			throw e;	
		}
		catch(MalformedURLException e){
			throw new ServiceException("Url is malformed.");
		}
		catch(FileNotFoundException e){
			throw new ServiceException("Image could not be loaded from url.");
		}
		catch (Throwable t){
			log.error(t);
			throw new ServiceException(t);
		}
	}
	
	/**
	 * This method was created for the image editor.
	 * 
	 * @return
	 */
//	@SuppressWarnings("unchecked")
//	public List<Image> getAllImages(Person person) throws ServiceException{
//		try {
//			List<Image> list = null;
//			Session session = Persistence.beginSession();
//			Query query = session.getNamedQuery("image.getAllImages");
//			list = query.list();
//			if(list.size() > person.getNumCommentsThreadPage()){
//				Paginator paginator = new Paginator<Image>(list,person.getNumCommentsThreadPage());
//				list = paginator.getPage(1);
//			}
//			Persistence.commit();
//			
//			return list;
//		}catch (Throwable t) {
//			log.error(t);
//			Persistence.rollback();
//			throw new ServiceException(t);
//		}
//	}
	
//	/**
//	 * a single page of images.
//	 * @param page
//	 * @return
//	 * @throws ServiceException
//	 */
//	@SuppressWarnings("unchecked")
//	public List<Image> getPaginatedPage(int page) throws ServiceException{
//		try{
//			Paginator<Image> paginator = Paginator.getActivePaginator();
//			if(paginator != null){
//				List<Image> list = paginator.getPage(page);
//				return list;
//			}
//			else
//				throw new ServiceException("Paginated data couldn't be found.");
//		}
//		catch(ServiceException e){
//			throw e;
//		}
//		catch(Throwable t){
//			log.error(t);
//			Persistence.rollback();
//			throw new ServiceException(t);
//		}
//	}

	/**
	 * Delete an image.
	 * @param imageId
	 * @throws ServiceException
	 */
	public void deleteImage(String imageId) throws ServiceException {
		try {
			Image image = readImageById(imageId);
			if(image == null){
				throw new ServiceException("Couldn't find that image");
			}
			
			Session session = Persistence.beginSession();
			session.delete(image);
			//Trevis: you should probably not allow an image to be deleted if it's still associated with something. 
			Persistence.commit();
			
		}
		catch(ServiceException e){
			Persistence.rollback();
			throw(e);
		}
		catch (Throwable t) {
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	
	/**
	 * Rename an image
	 * @param imageId
	 * @param name
	 * @throws ServiceException
	 */
	public void renameImage(String imageId,String name) throws ServiceException {
		try {
			Image image = readImageById(imageId);
			Session session = Persistence.beginSession();
			if(image == null){
				throw new ServiceException("Couldn't find that image");
			}
			if(name.length() < 2){
				throw new ServiceException("Try again, that name is garbage.");
			}
			image.setName(name);
			session.update(image);
			//Trevis: you should probably not allow an image to be deleted if it's still associated with something. 
			Persistence.commit();
			
		}
		catch(ServiceException e){
			Persistence.rollback();
			throw(e);
		}
		catch (Throwable t) {
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	
	public BufferedImage createSquareThumbnail(ImageIcon image){
		int width = image.getIconWidth();
		int height = image.getIconHeight();
		
		BufferedImage square;
		//Trevis: test for images smaller than QUARE_IMAGE_SIZE
		if(height > width){
			//Tall
			java.awt.Image scaled = image.getImage().getScaledInstance(QUARE_IMAGE_SIZE, -1, java.awt.Image.SCALE_SMOOTH);
			BufferedImage bimg = ImageUtils.toBufferedImageAlt(scaled);
			square = bimg.getSubimage(0, (bimg.getHeight()-QUARE_IMAGE_SIZE)/2, QUARE_IMAGE_SIZE, QUARE_IMAGE_SIZE);
		}
		else{
			//wide
			java.awt.Image scaled = image.getImage().getScaledInstance(-1, QUARE_IMAGE_SIZE, java.awt.Image.SCALE_SMOOTH);
			BufferedImage bimg = ImageUtils.toBufferedImageAlt(scaled);
			square = bimg.getSubimage((bimg.getWidth()-QUARE_IMAGE_SIZE)/2, 0, QUARE_IMAGE_SIZE, QUARE_IMAGE_SIZE);
		}
		return square;
	}
	
	
	/**
	 * Takes one of my image objects and creates 
	 * 
	 * @param i
	 * @param bytes the bytes from the blob. (i tried to just read the bytes from the blob but that wasnt easy
	 * @return
	 * @throws ServiceException
	 */
	private void createSquareThumbnail(ImageFull i, byte [] bytes) throws ServiceException{
		
		ImageIcon oldImage = new ImageIcon(bytes);
		BufferedImage buff = createSquareThumbnail(oldImage);
		
		Blob newBlob;
		try {
			newBlob = Hibernate.createBlob(ImageUtils.bufferedImageToByteArray(buff));
		
			i.setSqareImage(newBlob);
		} 
		catch (IOException e) {
			log.error(e);
			throw new ServiceException(e.getMessage());
		}
	}
	
}
