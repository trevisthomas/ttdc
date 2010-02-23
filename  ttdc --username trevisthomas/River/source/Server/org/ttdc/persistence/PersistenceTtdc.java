package org.ttdc.persistence;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.ttdc.persistence.migration.WebComments;
import org.ttdc.persistence.migration.WebForums;
import org.ttdc.persistence.migration.WebMain;
import org.ttdc.persistence.migration.WebMovieTitles;
import org.ttdc.persistence.migration.WebSectionNames;
import org.ttdc.persistence.migration.WebUsers;

/**
 * This is a connection to ttdc db...
 * @author Trevis
 *
 */
public class PersistenceTtdc {
	private static Logger log = Logger.getLogger(PersistenceTtdc.class);
	private static final SessionFactory sessionFactory;
	
	static {
        try {
            sessionFactory = new AnnotationConfiguration()
            	.configure("hibernate.cfg.ttdc.xml")
            	.addAnnotatedClass(WebUsers.class)
            	.addAnnotatedClass(WebMain.class)
            	.addAnnotatedClass(WebMovieTitles.class)
            	.addAnnotatedClass(WebSectionNames.class)
            	.addAnnotatedClass(WebForums.class)
            	.addAnnotatedClass(WebComments.class)
            	.buildSessionFactory();
            
        } catch (Throwable ex) {
        	log.error(ex);
            throw new ExceptionInInitializerError(ex);
        }
	}
	
	/**
	 * Gets the current session and starts a transaction.
	 * @return
	 */
	public static Session session(){
		Session session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		return session;
	}
	
	/**
	 * Commits the current transaction and closes the session.
	 */
	public static void commit(){
		sessionFactory.getCurrentSession().getTransaction().commit();
		sessionFactory.getCurrentSession().close();
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

}
