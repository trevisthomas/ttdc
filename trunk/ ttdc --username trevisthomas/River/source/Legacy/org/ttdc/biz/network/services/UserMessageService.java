package org.ttdc.biz.network.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.util.ServiceException;

/**
 * Service for storing messages for active users. This service keeps a bucket of messages for 
 * each client who is actively pinning the site. I'm implementing it initially to allow edited front
 * page posts to be refreshed but it should probably handle all of the dynamic user update stuff.
 *  
 * 
 * Singleton
 * 
 * @author Trevis
 *
 */
public class UserMessageService {
	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

	private static final Logger log = Logger.getLogger(UserMessageService.class);
	private final static ConcurrentMap<String,Entry> refreshCache = new ConcurrentHashMap<String,Entry>();
	private final static long MAX_AGE_MS = 1000 * 60 * 10;
	
	private static class Entry{
		private volatile Date lastAccessed = new Date();
		private final Queue<Post> refresh = new ConcurrentLinkedQueue<Post>();
		
		public Date getLastAccessed() {
			return lastAccessed;
		}
		public void setLastAccessed(Date lastAccessed) {
			this.lastAccessed = lastAccessed;
		}
		public boolean hasRefresh(){
			return refresh.peek() != null;
		}
		public void addRefresh(Post post){
			refresh.add(post);
		}
		public List<Post> popAllRefresh(){
			List<Post> list = new ArrayList<Post>();
			while(!refresh.isEmpty()){
				list.add(refresh.poll());
			}
			return list;
		}
	}
	
	private static class Cleanup implements Runnable{
		public void run() {
			//log.debug("Running Cleanup");
			for(String key : refreshCache.keySet()){
				Entry e = refreshCache.get(key);
				Date now = new Date();
				if(now.getTime() - e.getLastAccessed().getTime() > MAX_AGE_MS){
					refreshCache.remove(key);
					log.debug("**** Removing! ****");
				}
			}
		}
		
	}
	
	private UserMessageService(){
		scheduler.scheduleAtFixedRate(new Cleanup(), 10, 60, TimeUnit.SECONDS);
	}
	
	private static class UserMessageServiceHolder{
		private final static UserMessageService INSTANCE = new UserMessageService();
	}
	public static UserMessageService getInstance(){
		return UserMessageServiceHolder.INSTANCE;
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
	 * This method should be called when a post changes (is edited) it will leave a message
	 * for every pinging user that this post
	 * 
	 * @param p
	 * @return
	 */
	public void refreshPostForAllUsers(Post p){
		for(Entry e : refreshCache.values()){
			e.addRefresh(p);
		}
	}
	
	/**
	 * 
	 * Checks to see if there is any content newer than the provided date. A list of posts 
	 * are returned if there is any new content
	 * 
	 * @param date
	 * @param postId
	 * @param refresh
	 * @param rootIds
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public void pingForContent(Person person, String postId, List<Post> refresh, List<String> rootIds) throws ServiceException{
		try{
			if(!person.isAnonymous()){
				Entry entry = refreshCache.putIfAbsent(person.getPersonId(), new Entry());
				if(entry != null){
					entry.setLastAccessed(new Date());
				}
			}
			
			Session session = Persistence.beginSession();
			String latestPostId = CommentService.getInstance().getLatestPostId();
			if(!postId.equals(latestPostId)){
				Query query = session.getNamedQuery("post.getAllLatest").setMaxResults(person.getNumCommentsFrontpage());
				List<Post> latest = (List<Post>)query.list();
				boolean found = false;
				for(Post p : latest){
					if(p.getPostId().equals(postId)){
						found = true;
					}
					if(!found){
						refresh.add(p);
					}
					
					String id = p.getRoot().getPostId();
					if(!rootIds.contains(id)){
						rootIds.add(id);
					}
				}
			}
			if(!person.isAnonymous())
				refresh.addAll(refreshCache.get(person.getPersonId()).popAllRefresh());
		}
		catch(Exception t){
			log.error(t);
			throw new ServiceException(t);
		}
	}
	
	
}
