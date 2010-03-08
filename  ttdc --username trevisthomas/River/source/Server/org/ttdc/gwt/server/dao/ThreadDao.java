package org.ttdc.gwt.server.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.Post;

public class ThreadDao extends FilteredPostPaginatedDaoBase{
	private String rootId;
	private String threadId;
	public final static int THREAD_REPLY_MAX_RESULTS = 5; //TODO probably want to move this to being a user choice?
	
	public void setRootId(String rootId) {
		this.rootId = rootId; 
	}

	public String getRootId() {
		return rootId;
	}
	
	public PaginatedList<Post> loadByCreateDate() {
		PaginatedList<Post> results;
		//Grab the first page of starters requested by the user
		results = DaoUtils.executeLoadFromPostId(this,"ThreadDao.StartersByCreateDate", "ThreadDao.StartersCount",rootId,buildFilterMask(getFilterFlags()));
		List<Post> posts = loadAllPostsForThreads(results.getList());
		for(Post p : results.getList()){
			loadRepliesFromPostList(p, posts);
		}
		return results;
	}
	
	public PaginatedList<Post> loadByReplyDate() {
		PaginatedList<Post> results;
		//Grab the first page of starters requested by the user
		results = DaoUtils.executeLoadFromPostId(this,"ThreadDao.StartersByReplyDate","ThreadDao.StartersCount",rootId, buildFilterMask(getFilterFlags()));
		List<Post> posts = loadAllPostsForThreads(results.getList());
		for(Post p : results.getList()){
			loadRepliesFromPostList(p, posts);
		}
		return results;
	}
	
	private void loadRepliesFromPostList(Post thread, List<Post> posts) {
		List<Post> flatReplyHierarchy = new ArrayList<Post>();
		for(Post p : posts){
			if(!p.isThreadPost() && p.getThread().getPostId().equals(thread.getPostId()))
				flatReplyHierarchy.add(p);
		}	
		if(flatReplyHierarchy.size() > THREAD_REPLY_MAX_RESULTS){
			thread.setPosts(flatReplyHierarchy.subList(flatReplyHierarchy.size()-THREAD_REPLY_MAX_RESULTS, flatReplyHierarchy.size()));
		}
		else
			thread.setPosts(flatReplyHierarchy);
		
	}

	private List<Post> loadAllPostsForThreads(List<Post> threadStarters){
		List<String> postIds = new ArrayList<String>();
		for(Post post : threadStarters){
			postIds.add(post.getPostId());
		}
		List<Post> posts;
		posts = DaoUtils.executeLoadFromPostIds("ThreadDao.RepliesInThreads", postIds, buildFilterMask(getFilterFlags()));
		
		return posts;
	}
	
	/*
	 * 
	 * This public version is only here to allow ajax calls to page through the replies in a
	 * conversation.  (the view will will just present the user with a more button...
	 * 
	 * REMMEMBER this method pages in reverse order (from the newest to the oldest posts in a thread
	 */
	public PaginatedList<Post> loadThreadSummmary(){
		setPageSize(THREAD_REPLY_MAX_RESULTS);
		PaginatedList<Post> results;
		results = DaoUtils.executeLoadFromPostId(this, "ThreadDao.Thread", "ThreadDao.ThreadCount", threadId, buildFilterMask(getFilterFlags()));
		Collections.reverse(results.getList());
		return results;
	}
	
//	private PaginatedDaoBase makeArtificialPaginationInfoForReplies(int maxRepliesToShow){
//		PaginatedDaoBase paginationInfo = new PaginatedDaoBase(){}; 
//		paginationInfo.setCurrentPage(1);
//		paginationInfo.setPageSize(maxRepliesToShow);
//		return paginationInfo;
//	}
	
	public String getThreadId() {
		return threadId;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}
	
	
}
