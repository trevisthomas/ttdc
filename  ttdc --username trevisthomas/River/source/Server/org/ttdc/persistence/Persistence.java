package org.ttdc.persistence;

import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.ttdc.gwt.server.dao.InitConstants;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.AssociationPostTagLite;
import org.ttdc.persistence.objects.DaySummaryEntity;
import org.ttdc.persistence.objects.Entry;
import org.ttdc.persistence.objects.Image;
import org.ttdc.persistence.objects.ImageFull;
import org.ttdc.persistence.objects.Inbox;
import org.ttdc.persistence.objects.InboxCache;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.PostLite;
import org.ttdc.persistence.objects.Privilege;
import org.ttdc.persistence.objects.Shacktag;
import org.ttdc.persistence.objects.SimplePostEntity;
import org.ttdc.persistence.objects.Style;
import org.ttdc.persistence.objects.Tag;
import org.ttdc.persistence.objects.TagLite;
import org.ttdc.persistence.objects.ThreadSummaryEntity;
import org.ttdc.persistence.objects.UserObject;
import org.ttdc.persistence.objects.UserObjectTemplate;

public final class Persistence {
	private static final Logger log = Logger.getLogger(Persistence.class);
	private static final SessionFactory sessionFactory;
	static {
        try {
            Configuration configuration = new AnnotationConfiguration()
        	.configure()
        	//.configure("hibernate.cfg.mysql.xml")
        	.addAnnotatedClass(Person.class)
        	.addAnnotatedClass(Tag.class)
        	.addAnnotatedClass(Image.class)
        	.addAnnotatedClass(Style.class)
        	.addAnnotatedClass(UserObject.class)
        	.addAnnotatedClass(Post.class)
        	.addAnnotatedClass(AssociationPostTag.class)
        	.addAnnotatedClass(Privilege.class)
        	.addAnnotatedClass(UserObjectTemplate.class)
        	.addAnnotatedClass(TagLite.class)
        	.addAnnotatedClass(PostLite.class)
        	.addAnnotatedClass(AssociationPostTagLite.class)
        	.addAnnotatedClass(ImageFull.class)
        	.addAnnotatedClass(Entry.class)
        	.addAnnotatedClass(Inbox.class)
        	.addAnnotatedClass(InboxCache.class)
        	.addAnnotatedClass(Shacktag.class)
        	.addAnnotatedClass(SimplePostEntity.class)
        	.addAnnotatedClass(ThreadSummaryEntity.class)
        	.addAnnotatedClass(DaySummaryEntity.class);
            
            //Warning: Hibernate choked when i had the function name mixed case.
            configuration.addSqlFunction("bitwise_or", new SQLFunctionTemplate(Hibernate.BIG_INTEGER, " ?1 | ?2 "));
            configuration.addSqlFunction("bitwise_and", new SQLFunctionTemplate(Hibernate.BIG_INTEGER, " ?1 & ?2 "));
            
            
            sessionFactory = configuration.buildSessionFactory();

        } catch (Throwable ex) {
        	log.error(ex);
            throw new ExceptionInInitializerError(ex);
        }
        
        //Forcing the constants to initialize before anything else.
		InitConstants.refresh();
		//InitConstants.ANONYMOUS.getLogin();
    	
	}
	
	
	public static SessionFactory getSessionFactory(){
		return sessionFactory;
	}
	
	/**
	 * Gets the current session and starts a transaction.
	 * @return
	 */
	public static Session beginSession(){
//		if(sessionFactory.getCurrentSession().isOpen() && sessionFactory.getCurrentSession().getTransaction().isActive()){
//			throw new RuntimeException("You my friend are double dipping in the session");
//		}
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
		Session session = Persistence.beginSession();
		session.save(obj);
		Persistence.commit();
	}
	
	public static void update(Object obj){
		Session session = Persistence.beginSession();
		session.update(obj);
		Persistence.commit();
	}

}
