package org.ttdc.gwt.server.dao;

import static org.ttdc.persistence.Persistence.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.Post;

public class ThreadDao extends PaginatedDaoBase{
	private String rootId;
	private String threadId;
	public final static int THREAD_REPLY_MAX_RESULTS = 5; //TODO probably want to move this to being a user choice?
	private List<String> filteredTagIdList = new ArrayList<String>();
	
	public void setRootId(String rootId) {
		this.rootId = rootId; 
	}

	public String getRootId() {
		return rootId;
	}
	
	
	public PaginatedList<Post> loadByCreateDate() {
		PaginatedList<Post> results;
		//Grab the first page of starters requested by the user
		if(filteredTagIdList.size() > 0){
			results = DaoUtils.executeLoadFromPostId(this,"ThreadDao.StartersByCreateDate",rootId,filteredTagIdList);
		}
		else{
			results = DaoUtils.executeLoadFromPostIdNoFilter(this,"ThreadDao.StartersNoFilterByCreateDate",rootId);
		}
		
		List<Post> posts = loadAllPostsForThreads(results.getList());
		
		//Trevis, what on earth is this line for?
//		posts.get(0);
		///////// TEST THIS
		
		for(Post p : results.getList()){
			loadRepliesFromPostList(p, posts);
		}
		return results;
	}
	
	public PaginatedList<Post> loadByReplyDate() {
		PaginatedList<Post> results;
		//Grab the first page of starters requested by the user
		if(filteredTagIdList.size() > 0){
			results = DaoUtils.executeLoadFromPostId(this,"ThreadDao.StartersByReplyDate",rootId,filteredTagIdList);
		}
		else{
			results = DaoUtils.executeLoadFromPostIdNoFilter(this,"ThreadDao.StartersNoFilterByReplyDate",rootId);
		}
		
		
		
		//Infuse the post with the reply summaries
//		for(Post p : results.getList()){
//			SearchResults<Post> replies = loadThreadSummaryInternal(p.getPostId());
//			//TODO add a marker to let the client side know if there are more posts in this thread.
//			p.setPosts(replies.getList());
//		}
		
		List<Post> posts = loadAllPostsForThreads(results.getList());
		//Trevis, what on earth is this line for?
//		posts.get(0);
		///////// TEST THIS
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
		if(filteredTagIdList.size() > 0){
			posts = DaoUtils.executeLoadFromPostIds("ThreadDao.RepliesInThreads", postIds, filteredTagIdList);
		}
		else{
			posts = DaoUtils.executeLoadFromPostIdsNoFilter("ThreadDao.RepliesInThreadsNoFilter", postIds);
		}
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
		if(filteredTagIdList.size() > 0){
			results = DaoUtils.executeLoadFromPostId(this, "ThreadDao.Thread", threadId, filteredTagIdList);
		}
		else{
			results = DaoUtils.executeLoadFromPostIdNoFilter(this, "ThreadDao.ThreadNoFilter", threadId);
		}
		Collections.reverse(results.getList());
		return results;
	}
	
	private PaginatedDaoBase makeArtificialPaginationInfoForReplies(int maxRepliesToShow){
		PaginatedDaoBase paginationInfo = new PaginatedDaoBase(){}; 
		paginationInfo.setCurrentPage(1);
		paginationInfo.setPageSize(maxRepliesToShow);
		return paginationInfo;
	}
	
	public List<String> getFilteredTagIdList() {
		return filteredTagIdList;
	}

	public void setFilteredTagIdList(List<String> filteredTagIdList) {
		this.filteredTagIdList = filteredTagIdList;
	}
	
	public void addFilterTagId(String filterTagId){
		this.filteredTagIdList.add(filterTagId);
	}

	public String getThreadId() {
		return threadId;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}
	
	
}
