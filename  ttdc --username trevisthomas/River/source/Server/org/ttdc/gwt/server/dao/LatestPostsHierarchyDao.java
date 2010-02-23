package org.ttdc.gwt.server.dao;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.time.StopWatch;
import org.apache.log4j.Logger;
import org.hibernate.Query;
import org.ttdc.persistence.objects.Post;

import static org.ttdc.persistence.Persistence.*;

@Deprecated
public class LatestPostsHierarchyDao {
	private final static Logger log = Logger.getLogger(LatestPostsHierarchyDao.class);
	private final StopWatch stopwatch = new StopWatch();	
	private List<String> filteredTagIdList = new ArrayList<String>();
	private int threadCount = 10;//Default
	
	public List<Post> load(){
		log.debug("load() started.");
		stopwatch.start();
		try{		
			List<Post> list;
			if(filteredTagIdList.size() > 0){
				//list = executeQuery();
				//TODO add filtering for hierarchy!
				list = executeQueryNoFilters(); 
			}
			else{
				list = executeQueryNoFilters();
			}
			return list;
		}
		finally{
			log.debug("load() completed in: " +stopwatch);
		}
	}
	
	
	/*
	 * Trevis you changed this dao so that it returns a flat list of posts to represent the hierarhcy
	 * because it made the Inflatinator stuff a lot easer.  You still need to do filtering though.
	 * 
	 * Also, the LatestPostHierarchyTest unit test is busted because of that change.
	 */
	private List<Post> executeQueryNoFilters(){
		List<String> threadIds = new ArrayList<String>();
			
		Query query = session().createQuery("SELECT p.thread.postId FROM Post p WHERE thread.postId is not NULL ORDER BY date desc");
	
		@SuppressWarnings("unchecked")
		Iterator<Object> itr = query.iterate();
		while(itr.hasNext() && threadIds.size() < threadCount){
			Object obj = itr.next();
			String threadId = obj.toString();
			if(!threadIds.contains(threadId))
				threadIds.add(obj.toString());
		}
		/*
		@SuppressWarnings("unchecked")
		List<Post> list = session().createQuery("SELECT p FROM Post p WHERE p.postId IN (:postIds)")
			.setParameterList("postIds", threadIds)
			.list();
		*/
		List<Post> list = session().createQuery("SELECT p FROM Post p WHERE p.thread.postId IN (:threadIds)")
		.setParameterList("threadIds", threadIds)
		.list();
		
		
		Collections.sort(list, new Post.ThreadPathComparator(threadIds));
		/*
		Collections.sort(list, new Post.ByPostIdReferenceComparator(threadIds));
		Collections.sort(list, new Post.PathComparator());
		*/
		
		
		return list;
	}
	
	/*
	private List<Post> executeQueryNoFilters(){
		List<String> threadIds = new ArrayList<String>();
			
		Query query = session().createQuery("SELECT p.thread.postId FROM Post p WHERE thread.postId is not NULL ORDER BY date desc");
	
		@SuppressWarnings("unchecked")
		Iterator<Object> itr = query.iterate();
		while(itr.hasNext() && threadIds.size() < threadCount){
			Object obj = itr.next();
			String threadId = obj.toString();
			if(!threadIds.contains(threadId))
				threadIds.add(obj.toString());
		}
		
		@SuppressWarnings("unchecked")
		List<Post> list = session().createQuery("SELECT p FROM Post p WHERE p.postId IN (:postIds)")
			.setParameterList("postIds", threadIds)
			.list();
		
		Collections.sort(list, new Post.ByPostIdReferenceComparator(threadIds));
		
		return list;
	}
	*/
	public List<String> getFilteredTagIdList() {
		return filteredTagIdList;
	}
	public void setFilteredTagIdList(List<String> filteredTagIdList) {
		this.filteredTagIdList = filteredTagIdList;
	}
	public int getThreadCount() {
		return threadCount;
	}
	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}
	
}
