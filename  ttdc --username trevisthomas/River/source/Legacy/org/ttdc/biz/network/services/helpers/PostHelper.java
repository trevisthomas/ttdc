package org.ttdc.biz.network.services.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.ttdc.biz.network.services.InboxService;
import org.ttdc.biz.network.services.SearchService;
import org.ttdc.persistence.Persistence;
import org.ttdc.persistence.objects.AssociationPostTag;
import org.ttdc.persistence.objects.Person;
import org.ttdc.persistence.objects.Post;
import org.ttdc.persistence.objects.PostCounter;
import org.ttdc.persistence.objects.PostLite;
import org.ttdc.util.ServiceException;

public class PostHelper {
	private static Logger log = Logger.getLogger(SearchService.class);
	
	/*
	private static PostHelper me;
	public static PostHelper getInstance(){
		if(me == null){
			me = new PostHelper();
		}
		return me;
	}
	*/
	
	
	
	/*
	 * 
	 * TREVIS These two methods do similar things but they do them different ways. The first one is way faster for tagbrowser.
	 * Investigate.
	 * 
	 */
	/**
	 * Loads the posts from the id's in the collections.  Their entries are not initialized.
	 * @param postIds
	 * @return
	 */
	
	@SuppressWarnings("unchecked")
	public static List<Post> loadPosts(List<String> postIds){
		List<Post> list = new ArrayList<Post>();
		Session session = Persistence.session();
		Query query = session.getNamedQuery("post.getByPostIds").setParameterList("postIds", postIds);
		list = query.list();
//		Collections.sort(list,new Post.ByPostIdReferenceComparator(postIds));
		return list;
	}
	
	/**
	 * Loads a set of posts from a list of id's.  Filtered tagId's are processed as posts are loaded
	 * @param postIds
	 * @param filteredTagIds
	 * @return
	 */
	public static List<Post> loadPosts(List<String> postIds, List<String> filteredTagIds) {
		List<Post> posts = new ArrayList<Post>();
		Session session = Persistence.session();
		for(String id : postIds){
			Post p = (Post)session.load(Post.class,id); 
//			p.initialize();
//			posts.add(p);
//			p.setExpanded(true);
//			p.setRelativeAge("");
//			p.setHidden(false);
			for(String t : filteredTagIds){
//				if(p.containsTag(t))
//					//p.setFiltered(true);
//					p.setHidden(true);
			}
		}
		return posts;
	}
	
	/**
	 * Simple initialize.  Only does associations and entries.  Doesnt mark read or unread 
	 * or anything else.  This should only be used in special cases like refreshing movie 
	 * content for showing review summaries. 
	 *
	 * @param list
	 */
	public static void initializePosts(List<Post> list){
		for(Post p : list){
			Hibernate.initialize(p.getTagAssociations());
			Hibernate.initialize(p.getEntries());
		}
	}
	
	/**
	 * This method takes post object and recursively initializes the posts who's
	 * id's are in the toShow list, and are not filtered out
	 * 
	 * @param p
	 * @param toShow
	 * @param filteredTagIds
	 * @param expanded
	 */
	/*
	private static void initializePosts(Post p, Collection<String> toShow, List<String> filteredTagIds, boolean expanded){
		List<Post> list = new ArrayList<Post>();
		list.add(p);
		if(p.isMovie())
			initializePosts(p.getReviews());
			
		Hibernate.initialize(p.getTagAssociations());
		initializePosts(list, toShow, filteredTagIds, expanded);
	}
	*/
	/**
	 * Initializes post recursively calling children applying the filters and read status defined by the person.
	 * 
	 * 
	 * @param person
	 * @param p
	 * @param toShow
	 * @throws ServiceException
	 */
	public static void initializePosts(Person person, Post p, Collection<String> toShow){
//		List<Post> list = new ArrayList<Post>();
//		list.add(p);
//		if(p.isMovie())
//			initializePosts(p.getReviews());
//		Hibernate.initialize(p.getTagAssociations());
//		
//		//Trevis: The above initialization may be redundant.
//		
//		initializePosts(person, list, toShow);
	}
	
	/**
	 * This method takes a list of posts and recursively initializes the posts who's
	 * id's are in the toShow list, and are not filtered out.  
	 * 
	 * Read flag is applied and filtered content is removed.
	 * 
	 * @param list
	 * @param toShow
	 * @param filteredTagIds
	 * @param expanded
	 * @param frontPageMode - i only filter muted posts on the front page.
	 */
	public static void initializePosts(Person person, List<Post> list, Collection<String> toShow, boolean frontPageMode){
//		for(Post p : list){
//			//Hibernate.initialize(p.getPosts());
//			//if(!p.isHidden())
//			if(toShow.contains(p.getPostId())){
//				if(p.isMovie())
//					initializePosts(p.getReviews());
//				
//				InboxService.getInstance().flagUnreadPost(person,p);
//				
//				Hibernate.initialize(p.getPosts());
//				Hibernate.initialize(p.getTagAssociations());
//				if(p.getParent()!=null)
//					Hibernate.initialize(p.getParent().getTagAssociations());
//				
//				Hibernate.initialize(p.getEntries());
//				List<String> filteredTagIds;
//				if(frontPageMode)
//					filteredTagIds =  person.getFrontPageFilteredTagIds();
//				else
//					filteredTagIds =  person.getFilteredTagIds();
//				
//				if(!testPostFilter(p,filteredTagIds)){
//					continue;
//				}
//				else{
//					p.setHidden(false);
//				}
//			}
//			initializePosts(person,p.getPosts(),toShow);
//		}
	}
	
	/**
	 * This method takes a list of posts and recursively initializes the posts who's
	 * id's are in the toShow list, and are not filtered out.  
	 * 
	 * Read flag is applied and filtered content is removed.
	 * 
	 * @param list
	 * @param toShow
	 * @param filteredTagIds
	 * @param expanded
	 */
	public static void initializePosts(Person person, List<Post> list, Collection<String> toShow){
		initializePosts(person, list, toShow, false);
	}
	
	
	public static void initializePostsFlat(Person person, List<Post> list){
		initializePostsFlat(person,list,true);
	}
	/**
	 * this method initializes a list of posts without recursing.  Thus it initializes them flat. 
	 * 
	 * Read flag is applied and filtered content is removed.
	 * 
	 * @param person
	 * @param list
	 * @param initMovieReviews - tells the initializer weather or not to init movie reviews.  I had to stop them
	 * 							 from initializing when rendering the movie table because it was slowing things down a lot.
	 * 							Remember this is only done because you need them to be initialized when showing the movie root in 
	 * 							other contexts.
	 */
	public static void initializePostsFlat(Person person, List<Post> list, boolean initMovieReviews){
//		for(Post p : list){
//			p.setHidden(false);
//			p.setExpanded(true); //Probably not necessary
//			
//			InboxService.getInstance().flagUnreadPost(person,p);
//			
//			/*This is kind of a hack.  Come up with a better way*/
//			if(initMovieReviews && p.isMovie()){
//				initializePosts(p.getReviews());
//			}
//			
//			Hibernate.initialize(p.getTagAssociations());
//			if(p.getParent()!=null)
//				Hibernate.initialize(p.getParent().getTagAssociations());
//			for(String tagId : person.getFilteredTagIds()){
//				if(p.containsTag(tagId))
//					//p.setFiltered(true);
//					p.setHidden(true);
//			}
//			
//			Hibernate.initialize(p.getEntries());
//			
//			//Trevis: You should probably try to figure out how to determine if this has already been done?
//			Hibernate.initialize(p.getRoot().getTagAssociations());
//		}
	}
	
	/**
	 * Checks if a post is not tagged with one of the tags in the filterdTagId's list. If it is
	 * this method returns false. True is returned otherwise.
	 * 
	 * @param p
	 * @param filteredTagIds
	 * @return
	 */
	private static boolean testPostFilter(Post p, List<String> filteredTagIds){
		List<AssociationPostTag> asses = p.getTagAssociations();
		for(AssociationPostTag ass : asses){
			if(filteredTagIds.contains(ass.getTag().getTagId())){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Takes a list of thread root id's and builds a mapping to relate the postId's to the number of posts
	 * in that thread.
	 * 
	 * @param rootIds
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Map<String,PostCounter> loadPostCounters(Collection<String> rootIds){
		if(rootIds.size() == 0) return new HashMap<String,PostCounter>();
		
		Session session = Persistence.session();
		Map<String,PostCounter> map = new HashMap<String,PostCounter>();
		Query query = session.getNamedQuery("object.postCounter").setCacheable(true).setParameterList("rootIds", rootIds);
		List<Object[]> rows = query.list();
		for(Object[] row : rows){
			PostCounter pc = new PostCounter();
			pc.setCount((Long)row[0] -1);
			pc.setRootId((String)row[1]);
			map.put(pc.getRootId(),pc);
		}
		return map;
	}
	
	/**
	 * Gets the counter obj for a single root
	 * @param rootId
	 * @return
	 */
	public static PostCounter loadPostCounter(String rootId){
		
		Session session = Persistence.session();
		Query query = session.getNamedQuery("object.postCounter").setCacheable(true).setParameter("rootIds", rootId);
		Object[] row = (Object[])query.uniqueResult();
		PostCounter pc = new PostCounter();
		if(row != null){
			pc.setCount((Long)row[0] - 1);
			pc.setRootId((String)row[1]);
		}
		else{
			pc.setCount(0);
			pc.setRootId(rootId);
		}
		return pc;
		
	}
	/**
	 * Takes a list of threads and loads the post counters into them
	 * 
	 * @param threads
	 */
	public static Map<String,PostCounter> loadPostCounters(List<Post> threads){
		Map<String,PostCounter> totalsMap = PostHelper.loadPostCounters(PostHelper.extractIds(threads));
		for(Post p : threads){
//			if(totalsMap.containsKey(p.getPostId())){
//				p.setPostCounter(totalsMap.get(p.getPostId()));
//			}
		}
		return totalsMap;
	}	
	
	
	/**
	 * walks through a flat list of post objcets and builds a list of the id's of those posts
	 * 
	 * @param posts
	 * @return
	 */
	public static List<String> extractIds(List<Post> posts){
		List<String> postIds = new ArrayList<String>();
		for(Post pl : posts){
			postIds.add(pl.getPostId());
		}
		return postIds;
	}
	
	public static List<String> extractIdsLite(List<PostLite> posts){
		List<String> postIds = new ArrayList<String>();
		for(PostLite pl : posts){
			postIds.add(pl.getPostId());
		}
		return postIds;
	}
	
	/**
	 * Turns a list of posts into a unique set of postIds
	 * @param posts
	 * @param ids
	 */
	public static void extractIds(List<Post> posts, Set<String> ids){
		for(Post pl : posts){
			ids.add(pl.getPostId());
		}
	}
	/**
	 * This method is intended to help make string arrays out of 
	 * hibernate query lists when the list contains nothing but an id.
	 * As long as it is a list of Object arrays and the first value is a string
	 * it will work but it was intended for grabbing guids. 
	 * 
	 * @param results
	 * @return
	 */
	public static List<String> loadGuidsFromQuery(List<Object[]> results){
		List<String> list = new ArrayList<String>();
		for(Object[] objs : results){
			list.add((String)objs[0]);
		}
		return list;
	}
	/**
	 * For getting a subset of a result set of id's. Top command was causing
	 * weird performance issues on the UnintTagByMass query
	 * 
	 * @param itr
	 * @param max
	 * @return
	 */
	public static List<String> loadGuidsFromQuery(Iterator itr, int max){
		List<String> list = new ArrayList<String>();
		int count = 0;
		while(itr.hasNext()){
			Object obj = itr.next();
			list.add((String)obj);
			count++;
			if(count == max){
				break;
			}
		}
		return list;
	}
	
	/**
	 * Takes a root post and a empty list as arguements. The empty list 'flat' will be loaded
	 * with a flat list of the posts from the hierarchy.  This was initially created for the flat thread view page.
	 * 
	 * @param rootPost
	 * @param flat
	 */
	public static void loadPostsFlat(Post rootPost,List<Post> flat){
		flat.add(rootPost);
		for(Post p : rootPost.getPosts()){
			loadPostsFlat(p,flat);
		}
	}
	
	/**
	 * Finds the paginated results 
	 * @param page
	 * @return
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public static List<Post> getPaginatedPage(int page) throws ServiceException{
		try{
			Paginator<Post> paginator = Paginator.getActivePaginator();
			if(paginator != null){
				Persistence.beginSession();//getPage needs it.
				List<Post> list = paginator.getPage(page);
				Persistence.commit();
				return list;
			}
			else
				throw new ServiceException("Paginated data couldn't be found.");
		}
		catch(ServiceException e){
			throw e;
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}
	
	/**
	 * Traverses pages backward.  (Last page is full size, first page shows the remainder set)
	 * @param page
	 * @return
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	public static List<Post> getPaginatedPageInverse(String key, int page) throws ServiceException{
		try{
			Paginator<Post> paginator = Paginator.getActivePaginator(key);
			if(paginator != null){
				Persistence.beginSession();//getPage needs it.
				List<Post> list = paginator.getPageInverse(page);
				Persistence.commit();
				return list;
			}
			else
				throw new ServiceException("Paginated data couldn't be found.");
		}
		catch(ServiceException e){
			throw e;
		}
		catch(Throwable t){
			log.error(t);
			Persistence.rollback();
			throw new ServiceException(t);
		}
	}

}	
