package org.ttdc.biz.network.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.InboxCache;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.Tag;
import org.ttdc.util.ServiceException;

public final class InboxService {
	private static final Logger log = Logger.getLogger(InboxService.class);
	private final Map<String,Map<String,InboxCache>> cache = new HashMap<String,Map<String,InboxCache>>();
	private final static String SITE_KEY = "SITEKEY";
	
	public static InboxService getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
	private static class SingletonHolder {
		private final static InboxService INSTANCE = new InboxService();
	}
	
	private InboxService(){
		//Load caches from DB.
		try{
			Session session = Persistence.beginSession();
			Query query = session.getNamedQuery("inboxCache.getAll");
			@SuppressWarnings("unchecked") List<InboxCache> inboxCaches = query.list();
			for(InboxCache c : inboxCaches){
				Map<String,InboxCache> list = cache.get(c.getPerson().getPersonId());
				if(list == null){
					list = new HashMap<String,InboxCache>();
					cache.put(c.getPerson().getPersonId(), list);
				}
				list.put(c.getPost() != null ? c.getPost().getPostId() : SITE_KEY, c);
			}
			log.info("Built cache for "+cache.size()+ " users.");
		}
		catch(Exception t){
			log.error(t);
			throw new ExceptionInInitializerError(t);
		}
	}
	
	/**
	 * A utility method for properly assigning the 'read' state of the provided post hierarchy
	 * @param posts
	 * @param inbox
	 */
	/*
	public void flagUnreadPosts(Person person, Post post){
		List<Post> posts = new ArrayList<Post>();
		posts.add(post);
		flagUnreadPosts(person,posts);
	}
	*/
	
	/**
	 * A utility method for properly assigning the 'read' state of the provided post hierarchy
	 * @param posts
	 * @param inbox
	 */
	/*
	public void flagUnreadPosts(Person person, List<Post> posts){
		Map<String,InboxCache> inbox = cache.get(person.getPersonId());
		if(inbox == null){
			//This person has nothing marked read.
			//Just make an empty one for now.  They'll be taken care of once they mark something read
			inbox = new HashMap<String,InboxCache>();
		}
		recurseUnread(posts,inbox);
	}
	*/
	
	/**
	 * Recurses a hierarchy of posts deciding which ones have been read by this person
	 * @param posts
	 * @param inbox
	 * 
	 */
	private void recurseUnread(List<Post> posts,Map<String, InboxCache> inbox){
		for(Post p : posts){
			Date siteReadDate = null;
			Date postReadDate = null;
			Date date = null;
			
			if(!p.isHidden()){
				if(inbox.containsKey(SITE_KEY)){
					siteReadDate = inbox.get(SITE_KEY).getDate();
				}
				if(inbox.containsKey(p.getRoot().getPostId())){
					postReadDate = inbox.get(p.getRoot().getPostId()).getDate();
				}
				
				if(siteReadDate != null && postReadDate != null){
					//Choose which is more recent
					date = siteReadDate.compareTo(postReadDate) > 0 ? siteReadDate : postReadDate;
				}
				else{
					//else choose which is not null.  If both are null, date is null
					date = siteReadDate != null ? siteReadDate : postReadDate; 
				}
				
				if(date != null){
					if(date.compareTo(p.getDate()) > 0){
						//p.setUnread(false);
					}
					else{
						p.setUnread(true);
					}	
				}
				else{
					p.setUnread(true);
				}
				recurseUnread(p.getPosts(),inbox);
				
			}
			else{
				//p.setUnread(false);//Hm, this shouldnt be necessary
			}
		}
	}
	
	/**
	 * 
	 * Flags the unread state of a single post.  The exposed interface used to be recursive but
	 * in an effort to improve efficiency and clean the code a bit i'm exposing it for per post. 
	 * that way i can do this while i initialize. 
	 * 
	 * Returns immediately if person is null or anonymous
	 *   
	 * 
	 * @param p
	 * @param person
	 * @throws ServiceException
	 */
	public void flagUnreadPost(Person person, Post p){
		try{
			if(person == null || person.isAnonymous()) return;
			
			//Piggybacking on existing method to set Earmark.
			p.setEarmarked(p.hasTagAssociation(Tag.TYPE_EARMARK, person));
			
			Map<String,InboxCache> inbox = cache.get(person.getPersonId());
			
			Date siteReadDate = null;
			Date postReadDate = null;
			Date date = null;
			
			if(inbox == null){
				p.setUnread(true);
				return;
			}
			
			if(inbox.containsKey(SITE_KEY)){
				siteReadDate = inbox.get(SITE_KEY).getDate();
			}
			if(inbox.containsKey(p.getRoot().getPostId())){
				postReadDate = inbox.get(p.getRoot().getPostId()).getDate();
			}
			
			if(siteReadDate != null && postReadDate != null){
				//Choose which is more recent
				date = siteReadDate.compareTo(postReadDate) > 0 ? siteReadDate : postReadDate;
			}
			else{
				//else choose which is not null.  If both are null, date is null
				date = siteReadDate != null ? siteReadDate : postReadDate; 
			}
			
			if(date != null){
				if(date.compareTo(p.getDate()) > 0){
					//p.setUnread(false);
				}
				else{
					p.setUnread(true);
				}	
			}
			else{
				p.setUnread(true);
			}
		}
		catch(Exception e){
			log.error(e.getMessage());
			throw new RuntimeException(e);
		}
		
	}
	
	/**
	 * Marks the thread identified by rootPost as read by the person at 
	 * this moment in time  
	 * 
	 * @param person
	 * @param rootPost
	 */
	public void markThreadRead(Person person, Post rootPost){
		Session session = Persistence.beginSession();
		
		Map<String,InboxCache> inbox = cache.get(person.getPersonId());
		if(inbox == null){
			inbox = new HashMap<String,InboxCache>();
			cache.put(person.getPersonId(),inbox);
		}
		if(inbox.containsKey(rootPost.getPostId())){
			InboxCache item = inbox.get(rootPost.getPostId());
			item.setDate(new Date());
			session.update(item);
		}else{
			InboxCache item = new InboxCache();
			item.setPerson(person);
			item.setDate(new Date());
			item.setPost(rootPost);
			session.save(item);
			inbox.put(item.getPost().getPostId(),item);
		}
		Persistence.commit();	
	}
	
	public void markThreadRead(Person person, String postId){
		Session session = Persistence.beginSession();
		Post post = (Post)session.load(Post.class,postId);
		post = post.getRoot();
		Persistence.commit();
		markThreadRead(person, post.getRoot());
	}
	
	/**
	 * To make an InboxCache object for the site, i just set the post to null. I also perform cleanup here.
	 * Once the site is marked read for a person then all other thread read info is unnecessary so i just delete them
	 * Note, they can continue to mark threads as read afterward because new posts can be tracked per thread as long
	 * as the site as read date is older.  Thread marked as read with a date older than site marked as read is redundant.  
	 *  
	 * @param person
	 */
	public void markSiteRead(Person person) throws ServiceException{
		try{
			Session session = Persistence.beginSession();
			
			Map<String,InboxCache> inbox = new HashMap<String,InboxCache>();
			cache.put(person.getPersonId(),inbox);
			
			session.getNamedQuery("inboxCache.deletePerson").setString("personId",person.getPersonId()).executeUpdate();
			Persistence.commit();
			
			session = Persistence.beginSession();
			
			InboxCache item = new InboxCache();
			item.setPerson(person);
			item.setDate(new Date());
			item.setPost(null);
			session.save(item);
			inbox.put(SITE_KEY,item);
			
			Persistence.commit();
		}
		catch(Exception e){
			log.error(e);
			Persistence.rollback();
			throw new ServiceException(e);
		}
	}
}
