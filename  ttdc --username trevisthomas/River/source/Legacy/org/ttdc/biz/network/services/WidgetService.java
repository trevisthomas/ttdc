package org.ttdc.biz.network.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.ttdc.biz.network.services.helpers.PersonsPost;
import org.ttdc.biz.network.services.helpers.PostHelper;
import org.ttdc.biz.network.services.helpers.TrafficCache;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.PostCounter;
import org.ttdc.persistence.objects.PostLite;
import org.ttdc.persistence.objects.Tag;
import org.ttdc.util.ServiceException;
import org.ttdc.util.web.HTMLCalendar;
import org.ttdc.util.web.Month;

public final class WidgetService {
	private final static Logger log = Logger.getLogger(WidgetService.class);
	
	private WidgetService() {}
	
	public static WidgetService getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder {
		private final static WidgetService INSTANCE = new WidgetService();
	}
	
	@SuppressWarnings("unchecked")
	public List<Person> getTraffic() throws ServiceException{
		try{
			/*
			Session session = Persistence.session();
			Query query = session.getNamedQuery("person.getTraffic").setMaxResults(8);
			List<Person> people = query.list();
			Persistence.commit();
			return people;
			*/
			return TrafficCache.getInstance().getPeople();
		}
		/*
		catch(ServiceException e){
			throw e;
		}
		*/
		catch(Throwable t){
			log.error(t);
			throw new ServiceException(t);
		}
		
	}
	
	/**
	 * Generates a list of months representing the content needed to render a year calendar
	 * 
	 * @param y
	 * @return
	 */
	public List<Month> getYearCalendar(int y){
		List<Month> months = new ArrayList<Month>();
		Session session = Persistence.beginSession();
		for(int m = 1; m < 13;m++){
			Query query = session.getNamedQuery("object.daysWithContent").setInteger("year", y).setInteger("month", m);
			@SuppressWarnings("unchecked") List<Integer> days = query.list();
		    Month month = HTMLCalendar.buildMonthObject(m, y, days, true);
		    months.add(month);
		}
		return months;
	}
	
	public Month getCurrentCalendar(){
		Calendar rightNow = Calendar.getInstance();
		int m = rightNow.get(Calendar.MONTH) + 1;
	    int y = rightNow.get(Calendar.YEAR);
	    
	    Session session = Persistence.beginSession();
	    Query query = session.getNamedQuery("object.daysWithContent").setInteger("year", y).setInteger("month", m);
	    
	    @SuppressWarnings("unchecked") List<Integer> days = query.list();
	    
	    Month month = HTMLCalendar.buildMonthObject(m, y, days, true);
	    return month;
		
	}
	
	/**
	 * I decided to make the movie widget trigger on ratings only. So it will show
	 * the latest x-number of ratings. 
	 * 
	 * @return
	 */
	public List<PersonsPost> getMovieWidgetData(){
		List<PersonsPost> posts = new ArrayList<PersonsPost>();
		
		Session session = Persistence.beginSession();
		Query query = session.getNamedQuery("ass.getLatestOfType").setString("type", Tag.TYPE_RATING).setMaxResults(10);
		@SuppressWarnings("unchecked") List<AssociationPostTag> asses = query.list();
		
		for(AssociationPostTag ass : asses){
			ass.initialize();
			PersonsPost pp = new PersonsPost(ass.getPost(), ass.getCreator());
			posts.add(pp);
		}
		return posts;
	}
	
	/**
	 * Gets the the most recently replied to x-number of posts.
	 *  
	 * @return
	 */
	public List<Post> getHotTopics(int count) throws ServiceException{
		try{
			
			
			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("post.getAllLatest").setMaxResults(count * 50);
			
			@SuppressWarnings("unchecked") List<Post> tmplist = query.list();
			List<Post> rootPosts = new ArrayList<Post>();
			List<Post> posts = new ArrayList<Post>();
			for(Post p : tmplist ){
				if(!rootPosts.contains(p.getRoot()) && !p.getRoot().isDeleted() && !p.getRoot().isPrivate()){
					rootPosts.add(p.getRoot());
					posts.add(p);
					if(rootPosts.size() == count) break;
				}
			}
			
			List<String> rootIds = PostHelper.extractIds(rootPosts);
			
			Map<String,PostCounter> counters = PostHelper.loadPostCounters(rootIds);
			
			
			query = session.getNamedQuery("post.getByPostIds").setParameterList("postIds", rootIds);
			
			for(Post p : posts){
				p.initialize();	
				p.setPostCounter(counters.get(p.getRoot().getPostId()));
			}
			
			Persistence.commit();
			return posts;
		}
		catch(Throwable t){
			throw new ServiceException(t);
		}
	}
	
	/**
	 * Gets the most recently added x number of threads
	 * @return
	 */
	public List<Post> getNewThreads(int count) throws ServiceException{
		try{
			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("postLite.getLatestThreads").setMaxResults(count);
			
			@SuppressWarnings("unchecked") List<PostLite> liteRoots = query.list();
			List<String> rootIds = PostHelper.extractIdsLite(liteRoots);
			
			Map<String,PostCounter> counters = PostHelper.loadPostCounters(rootIds);
			
			query = session.getNamedQuery("post.getByPostIds").setParameterList("postIds", rootIds);
			
			@SuppressWarnings("unchecked") List<Post> posts = query.list();
			for(Post p : posts){
				p.setPostCounter(counters.get(p.getPostId()));
			}
			
			Collections.sort(posts,new Post.DateComparatorDesc());
			
			PostHelper.initializePosts(posts);
			
			Persistence.commit();
			return posts;
		}
		catch(Throwable t){
			throw new ServiceException(t);
		}
	}
	
	/**
	 * Gets the most popular x number of threads
	 * @return
	 */
	public List<Post> getMostPopularThreads(int count) throws ServiceException{
		try{
			
			
			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("postLite.getMostPopular").setMaxResults(count);
			
			@SuppressWarnings("unchecked") List<PostLite> liteRoots = query.list();
			List<String> rootIds = PostHelper.extractIdsLite(liteRoots);
			
			Map<String,PostCounter> counters = PostHelper.loadPostCounters(rootIds);
			
			query = session.getNamedQuery("post.getByPostIds").setParameterList("postIds", rootIds);
			
			@SuppressWarnings("unchecked") List<Post> posts = query.list();
			for(Post p : posts){
				p.setPostCounter(counters.get(p.getPostId()));
			}
			
			Collections.sort(posts,new Post.PostCounterComparator());
			
			PostHelper.initializePosts(posts);
			
			Persistence.commit();
			return posts;
		}
		catch(Throwable t){
			throw new ServiceException(t);
		}
	}
}

