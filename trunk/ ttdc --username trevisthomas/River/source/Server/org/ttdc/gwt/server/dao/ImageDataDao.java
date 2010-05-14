package org.ttdc.gwt.server.dao;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Blob;

import javax.swing.ImageIcon;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.ttdc.gwt.shared.util.StringUtil;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Image;
import org.ttdc.persistence.objects.ImageFull;
import org.ttdc.persistence.objects.Person;
import org.ttdc.util.ImageUtils;
import org.ttdc.util.ServiceException;

public class ImageDataDao {
	private static final Logger log = Logger.getLogger(ImageDataDao.class);
	public static final int QUARE_IMAGE_SIZE = 50;
	private Person person;
	public ImageDataDao(Person person){
		this.person = person;
	}
	
	public void renderImageToOutputStream(){}
	
	public Image readImage(String name){
		Image image = null;
		name = name.replaceFirst(Image.SQUARE_THUMBNAIL_SUFFIX, "");
		
		Query query = Persistence.session().getNamedQuery("image.getByName").setString("name", name);
		image = (Image) query.uniqueResult();
		
		return image;
	}
	
	public ImageFull createImage(InputStream in, String saveAs) {
		validatePerson();
		try {
			
			//Check for duplicate image name
			saveAs = validateFileName(saveAs);
			
			//FileInputStream in = new FileInputStream(file);
			
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
			
			byte[] bytes = out.toByteArray();
			ImageFull i = createImageBlobFromByteArray(saveAs, bytes);
			
			in.close();
			
			//Switched to make thumbnails everytime
			createSquareThumbnail(i, bytes);
			
			return i;
			
		}
		catch (FileNotFoundException e) {
			log.error(e);
			throw new RuntimeException(e);
		} 
		catch (IOException e) {
			log.error(e);
			throw new RuntimeException(e);
		} 
	}
	public ImageFull createImage(File file, String saveAs){
		validatePerson();
		try {
			
			//Check for duplicate image name
			if(StringUtils.isEmpty(saveAs)){
				saveAs = file.getName();
			}
			saveAs = validateFileName(saveAs);
			
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
			
			byte[] bytes = out.toByteArray();
			ImageFull i = createImageBlobFromByteArray(saveAs, bytes);
			
			in.close();
			
			//Switched to make thumbnails everytime
			createSquareThumbnail(i, bytes);
			
			return i;
			
		}
		catch (FileNotFoundException e) {
			log.error(e);
			throw new RuntimeException(e);
		} 
		catch (IOException e) {
			log.error(e);
			throw new RuntimeException(e);
		} 
	}

	/**
	 * This method at the moment throws an exception if the image exists. But i think that 
	 * i will change the app so that it will do whatever it takes the make a unique name for the 
	 * file.
	 * 
	 * @param saveAs
	 * @return
	 */
	private String validateFileName(final String saveAs) {
		int count = 0;
		String goodFileName = saveAs;
		int dotndx = saveAs.lastIndexOf('.');
		String prefix = saveAs.substring(0,dotndx);
		String ext = saveAs.substring(dotndx+1);
		while(true){
			if(readImage(goodFileName) != null){
				goodFileName = String.format("%s_%03d.%s",prefix,count,ext);
				count++;
			}
			else{
				break;
			}
		}
		
		return goodFileName;
	}
	
	/**
	 * Creates a blob image by loading the image from another website
	 * 
	 * @return
	 */
	public ImageFull createImage(String urlStr, String newName){
		validatePerson();
		try	{
				
			String name = "";
			if(urlStr == null){
				throw new RuntimeException("Invalid URL");
			}
			
			if(StringUtil.notEmpty(newName)){
				name = appendDotExtentionIfNecessary(urlStr, newName);
			}
			else{
				name = urlStr.substring(urlStr.lastIndexOf('/')+1);
			}
			
			name = validateFileName(name);
			
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
			ImageFull i = createImageBlobFromByteArray(name, bytes);
			//Always make a thumbnail
			createSquareThumbnail(i, bytes);
			
			return i;
		}
		catch (FileNotFoundException e) {
			log.error(e);
			throw new RuntimeException(e);
		} 
		catch (IOException e) {
			log.error(e);
			throw new RuntimeException(e);
		} 
	}

	private String appendDotExtentionIfNecessary(String urlStr, String newName) {
		String name;
		if(newName.indexOf('.') < 0){
			String extension = urlStr.substring(urlStr.lastIndexOf('.'));
			if(extension.length() > 5)
				name = newName;	//In case the image has no dot extension.
			else
				name = newName + extension;
		}
		else{
			name = newName;
		}
		return name;
	}

	private void validatePerson() {
		if(person == null || person.isAnonymous()) 
			throw new RuntimeException("Anonymous users have no access to create images");
	}

	/**
	 * Create a hybernate blob image from a byte array.
	 * 
	 * @param name
	 * @param bytes
	 * @return
	 */
	private ImageFull createImageBlobFromByteArray(String name, byte[] bytes) {
		Blob blob = Hibernate.createBlob(bytes);
		ImageFull i = new ImageFull();
		i.setImage(blob);

		i.setName(name);
		i.setOwner(person);
		ImageIcon image = new ImageIcon(bytes);
		if(image.getIconWidth() < 0) throw new RuntimeException("The image is not an image.");
		i.setWidth(image.getIconWidth());
		i.setHeight(image.getIconHeight());
		return i;
	}
	
	/**
	 * Takes one of my image objects and creates 
	 * 
	 * @param i
	 * @param bytes the bytes from the blob. (i tried to just read the bytes from the blob but that wasnt easy
	 * @return
	 * @throws ServiceException
	 */
	private void createSquareThumbnail(ImageFull i, byte [] bytes) throws IOException{
		ImageIcon oldImage = new ImageIcon(bytes);
		BufferedImage buff = createSquareThumbnail(oldImage);
		
		Blob newBlob;
		newBlob = Hibernate.createBlob(ImageUtils.bufferedImageToByteArray(buff));
	
		i.setSqareImage(newBlob);
	}
	

	private BufferedImage createSquareThumbnail(ImageIcon image){
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

	
	
}
