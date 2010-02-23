package org.ttdc.servlets;

import java.io.File;
import java.io.InputStream;

import org.hibernate.Session;
import org.ttdc.gwt.server.dao.ImageDataDao;
import org.ttdc.gwt.server.dao.PersonDao;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.ImageFull;
import org.ttdc.persistence.objects.Person;

/**
 * 
 * @author Trevis
 *
 */
public class ImageProxy {
	private Person person;
	public ImageProxy(String personId) {
		//Security check?
		if(personId == null) throw new RuntimeException("You have to be logged in to upload.");
		Persistence.beginSession();
		person = PersonDao.loadPerson(personId);
		Persistence.commit();
	}
	
	public ImageFull saveImageFile(File file, String saveAs){
		ImageDataDao dao = new ImageDataDao(person);
		try{
			Session session = Persistence.beginSession();
			ImageFull image = dao.createImage(file, saveAs);
			//session = Persistence.beginSession();
			session.save(image);
			return image;
		}
		finally{
			Persistence.commit();
		}
		
	}
	
	public ImageFull saveImageFile(InputStream stream, String saveAs){
		ImageDataDao dao = new ImageDataDao(person);
		try{
			Session session = Persistence.beginSession();
			ImageFull image = dao.createImage(stream, saveAs);
			//session = Persistence.beginSession();
			session.save(image);
			return image;
		}
		finally{
			Persistence.commit();
		}
		
	}
}
