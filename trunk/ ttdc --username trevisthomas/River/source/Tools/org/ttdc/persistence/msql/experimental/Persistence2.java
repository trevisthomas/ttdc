package org.ttdc.persistence.msql.experimental;

import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;


public final class Persistence2 {
	private static final Logger log = Logger.getLogger(Persistence2.class);
	private static final SessionFactory sessionFactory;
	
	static {
        try {
            sessionFactory = new AnnotationConfiguration()
            	.configure("hibernate.cfg.mysql.xml")
            	.addAnnotatedClass(ImageExperiment.class)
            	.buildSessionFactory();
            
        } catch (Throwable ex) {
        	log.error(ex);
            throw new ExceptionInInitializerError(ex);
        }
	}
	
	public static SessionFactory getSessionFactory(){
		return sessionFactory;
	}
	
	/**
	 * Gets the current session and starts a transaction.
	 * @return
	 */
	public static Session beginSession(){
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		return session;
	}
	
	public static FullTextSession fullTextSession(){
		Session session = sessionFactory.getCurrentSession();
		FullTextSession fullTextSession = Search.getFullTextSession(session);//Was createFullText...
		//Transaction tx = fullTextSession.beginTransaction();
		fullTextSession.beginTransaction();
		return fullTextSession;
	}
	
	/**
	 * This just retrieves the active current session, it assums that the transaction
	 * is already active.
	 * 
	 * @return
	 */
	public static Session session(){
		Session session = sessionFactory.getCurrentSession();
		return session;
	}
	
	/**
	 * Commits the current transaction and closes the session.
	 */
	public static void commit(){
		if(sessionFactory.getCurrentSession().isOpen() && sessionFactory.getCurrentSession().getTransaction().isActive()){
			try{
				sessionFactory.getCurrentSession().getTransaction().commit();
				sessionFactory.getCurrentSession().close();
			}
			catch(RuntimeException e){
				log.error("The world is in fire! Persistence caught an exception during commit! I'm going to roll back and rethrow it!");
				rollback();
				throw e;
			}
		}
	}
	
	public static void rollback(){
		try{
			sessionFactory.getCurrentSession().getTransaction().rollback();
			sessionFactory.getCurrentSession().close();
		}
		catch(Throwable t){
			log.error(t);
		}
	}
	
	public void finalize() {
		try {
			if (sessionFactory != null) {
				sessionFactory.close();
			}
		}
		catch (Throwable e) {
			log.error("Caught exception duing Persistence shutdown.", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Map getCache(String region){
		Map cacheEntries = sessionFactory.getStatistics()
        .getSecondLevelCacheStatistics(region)
        .getEntries();
		
		if(cacheEntries != null)
			log.info("Region:"+region+cacheEntries.toString());
		else
			log.info("Region:"+region+" None Found in Cache");
		return cacheEntries;
	}
	
	/**
	 * This method creates a new session and closes it when it is finished.
	 * it's just a helper you should just use your active session.
	 * @param obj
	 */
	public static void save(Object obj){
		Session session = Persistence2.beginSession();
		session.save(obj);
		Persistence2.commit();
	}
	
	public static void update(Object obj){
		Session session = Persistence2.beginSession();
		session.update(obj);
		Persistence2.commit();
	}

}
