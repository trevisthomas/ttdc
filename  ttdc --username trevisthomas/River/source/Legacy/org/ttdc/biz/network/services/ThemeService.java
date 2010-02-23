package org.ttdc.biz.network.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Style;
import org.ttdc.util.ServiceException;

/**
 * I'm hestitant to create this service but, no other service seems quite appropriate. Initially all this service does is
 * load the available styles.   I guess eventually if i make a full blown style editor this service can do the work.
 * 
 * @author Trevis
 *
 */
public class ThemeService {
	private static final Logger log = Logger.getLogger(ThemeService.class);
	private static class StyleServiceHolder{
		private static final ThemeService INSTANCE = new ThemeService();
	}
	public static ThemeService getInstance(){
		return StyleServiceHolder.INSTANCE;
	}
	private final String defaultStyleId;
	private ThemeService(){
		
		try{
			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("style.getDefault");
			Style style = (Style)query.uniqueResult();
			defaultStyleId = style.getStyleId();
			Persistence.commit();
		}
		catch(Exception e){
			log.error(e);
			throw new ExceptionInInitializerError(e);
		}
		
	}
	
	/**
	 * Does nothing.  Do not call. Throws UnsupportedOperationException
	 */
	@Override
	public boolean equals(Object obj) {
		throw new UnsupportedOperationException();
	}
	/**
	 * Does nothing.  Do not call. Throws UnsupportedOperationException
	 */
	@Override
	public int hashCode() {
		throw new UnsupportedOperationException();
	}
	/**
	 * Does nothing.  Do not call. Throws UnsupportedOperationException
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Gets the list of all style sheets
	 * 
	 *  
	 * @return returns a list of Style objects.
	 * @throws ServiceException in case of hibernate errors. 
	 */
	public List<Style> getAllStyles() throws ServiceException{
		try{
			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("style.getAll");
			@SuppressWarnings("unchecked") List<Style> styles = new ArrayList<Style>(query.list());
			Persistence.commit();
			return styles;
		}
		catch(Exception e){
			log.info(e);
			throw new ServiceException(e);
		}
	}
	
	/**
	 * gets the default style sheet 
	 * 
	 * @return
	 * @throws ServiceException in case of hibernate errors or if the object has not been properly initialized.
	 */
	/*
	 * This method uses the static final id value to load the style so that it can be read from the cache. 
	 * This will require the server to be restarted if default is changed.
	 *  
	 */
	public Style getDefaultStyle(){
		try{
			Session session = Persistence.beginSession();
			Style style = (Style)session.load(Style.class, defaultStyleId);
			Hibernate.initialize(style);
			return style;
		}
		catch(Exception e){
			log.info(e);
			throw new Error(e);
		}
	}
	/**
	 * Create a new style sheet for users to select.
	 *
	 * @param creator
	 * @param cssFileName
	 * @param description
	 * @param displayName
	 * 
	 * @return Returns the newly created stylesheet.
	 * @throws throws ServiceException in case of fault
	 */
	public Style createStyle(Person creator,String cssFileName,String description,String displayName) throws ServiceException{
		try{
			Session session = Persistence.beginSession();
			
			Style s = new Style();
			s.setCreator(creator);
			s.setCss(cssFileName);
			s.setDescription(description);
			s.setName(displayName);
			
			session.save(s);
			
			Persistence.commit();
			return s;
		}
		catch(Exception e){
			log.info(e);
			Persistence.rollback();
			throw new ServiceException(e);
		}
		
	}
	/**
	 * Delete the style referenced by this id.
	 * 
	 * @param styleId
	 * @throws ServiceException
	 */
	public void deleteStyle(String styleId) throws ServiceException{
		try{
			Session session = Persistence.beginSession();
			Style style = (Style)session.load(Style.class, styleId);
			session.delete(style);
			Persistence.commit();
		}
		catch(Exception e){
			log.info(e);
			Persistence.rollback();
			throw new ServiceException(e);
		}
	}

	/**
	 * Does what it says
	 * 
	 * @param styleId
	 * @return
	 * @throws ServiceException
	 */
	public Style readStyle(String styleId) throws ServiceException{
		try{
			Session session = Persistence.beginSession();
			Style style = (Style)session.load(Style.class, styleId);
			Hibernate.initialize(style);
			Persistence.commit();
			return style;
		}
		catch(Exception e){
			log.info(e);
			Persistence.rollback();
			throw new ServiceException(e);
		}
	}
	
	/**
	 * Update 
	 * 
	 * @param styleId
	 * @param s
	 * @throws ServiceException
	 */
	public void updateStyle(String styleId, Style s) throws ServiceException{
		try{
			Session session = Persistence.beginSession();
			Style style = (Style)session.load(Style.class, styleId);
			
			style.setCss(s.getCss());
			style.setName(s.getName());
			style.setDescription(s.getDescription());
			
			session.update(style);
			Persistence.commit();
		}
		catch(Exception e){
			log.info(e);
			Persistence.rollback();
			throw new ServiceException(e);
		}
	}
	
}
