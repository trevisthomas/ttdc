package org.ttdc.gwt.server.dao;

import static org.ttdc.persistence.Persistence.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.Post;

public class ThreadDao extends FilteredPostPaginatedDaoBase{
	private String rootId;
	private String threadId;
	private Post sourcePost;
	
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
	
	private final static String HQL_ThreadIdsByDate = "SELECT post.postId FROM Post post " +
			"WHERE post.parent.postId=:parentId AND bitwise_and( post.metaMask, :filterMask ) = 0 " +
			"ORDER BY post.threadReplyDate DESC";
	
	private final static String HQL_SUB_THREAD_BY_PATH = "SELECT post.postId FROM Post post " +
			"WHERE post.thread.postId=:postId AND bitwise_and( post.metaMask, :filterMask ) = 0 " +
			"ORDER BY post.path DESC";
	
	public PaginatedList<Post> loadByReplyDate() {
		PaginatedList<Post> results;
		//Grab the first page of starters requested by the user
		int page = 1;
		int subPage = 1;
		if(getCurrentPage() == -1){
			if(!sourcePost.isRootPost() && getCurrentPage() == -1){
				//Source is not a root, find the thread page
				threadId = sourcePost.getThread().getPostId();
				@SuppressWarnings("unchecked")
				List<String> list = session().createQuery(HQL_ThreadIdsByDate)
					.setParameter("parentId", sourcePost.getRoot().getPostId())
					.setParameter("filterMask", buildFilterMask(getFilterFlags()))
					.list();
				
				int ndx = list.indexOf(threadId);
				if(ndx >= 0){
					page = (ndx / getPageSize())+1;
				}
				
				if(!sourcePost.isThreadPost()){
					//Reply post
					//10F4325D-8BE0-4346-B126-F257D5A308B5
					subPage = determinePageNumber(HQL_SUB_THREAD_BY_PATH, 
												sourcePost.getThread().getPostId(),
												sourcePost.getPostId(),
												THREAD_REPLY_MAX_RESULTS);
					
					
				}
			}
			
			setCurrentPage(page);
		}
		
		results = DaoUtils.executeLoadFromPostId(this,"ThreadDao.StartersByReplyDate","ThreadDao.StartersCount",rootId, buildFilterMask(getFilterFlags()));
		List<Post> posts = loadAllPostsForThreads(results.getList());
		for(Post p : results.getList()){
			if(p.equals(sourcePost.getThread()) && subPage != 1)
				loadRepliesFromPostList(p, posts, subPage);
			else
				loadRepliesFromPostList(p, posts);
		}
		return results;
	}
	
	private int determinePageNumber(String query, String refPostId, String sourcePostId,int perPage) {

		int page = 1;	
		@SuppressWarnings("unchecked")
		List<String> list = session().createQuery(query)
			.setParameter("postId", refPostId)
			.setParameter("filterMask", buildFilterMask(getFilterFlags()))
			.list();
		
		int ndx = list.indexOf(sourcePostId);
		if(ndx >= 0){
			page = (ndx / perPage)+1;
		}
		
		return page;
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
	
	private void loadRepliesFromPostList(Post thread, List<Post> posts, int subPage) {
		List<Post> flatReplyHierarchy = new ArrayList<Post>();
		for(Post p : posts){
			if(!p.isThreadPost() && p.getThread().getPostId().equals(thread.getPostId()))
				flatReplyHierarchy.add(p);
		}	
		if(flatReplyHierarchy.size() > THREAD_REPLY_MAX_RESULTS){
			int start = flatReplyHierarchy.size()-(THREAD_REPLY_MAX_RESULTS * subPage);
			int end = start + THREAD_REPLY_MAX_RESULTS;
			thread.setPosts(flatReplyHierarchy.subList(start, end));
		}
		else
			thread.setPosts(flatReplyHierarchy);
		
	}

	private List<Post> loadAllPostsForThreads(List<Post> threadStarters){
		List<String> postIds = new ArrayList<String>();
		for(Post post : threadStarters){
			postIds.add(post.getPostId());
		}
		List<Post> posts = new ArrayList<Post>();
		if(postIds.size() > 0)
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
	
	public void setSourcePost(Post post){
		this.sourcePost = post;
	}
	
}
