package org.ttdc.gwt.server.dao;

import static org.ttdc.persistence.Persistence.session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.ttdc.gwt.shared.util.PaginatedList;
import org.ttdc.persistence.objects.Post;
import org.ttdc.util.PathSegmentizer;

public class ThreadDao extends FilteredPostPaginatedDaoBase{
	private String rootId;
	private String threadId;
	private Post sourcePost;
	
	public final static int THREAD_REPLY_MAX_RESULTS = 20; //TODO probably want to move this to being a user choice?
	
	public void setRootId(String rootId) {
		this.rootId = rootId; 
	}

	public String getRootId() {
		return rootId;
	}
	
//	public PaginatedList<Post> loadByCreateDate() {
//		PaginatedList<Post> results;
//		//Grab the first page of starters requested by the user
//		results = DaoUtils.executeLoadFromPostId(this,"ThreadDao.StartersByCreateDate", "ThreadDao.StartersCount",rootId,buildFilterMask(getFilterFlags()));
//		List<Post> posts = loadAllPostsForThreads(results.getList());
//		for(Post p : results.getList()){
//			loadRepliesFromPostList(p, posts);
//		}
//		return results;
//	}
	
	public PaginatedList<Post> loadByCreateDate() {
		PaginatedList<Post> results;
		
		int page = 1;
		int subPage = 1;
		if(getCurrentPage() == -1){
			if(!sourcePost.isRootPost() && getCurrentPage() == -1){
				//Source is not a root, find the thread page
				page = determineThreadPage(HQL_ThreadIdsByCreateDate);
				subPage = determineSubPageInPath();
			}
			setCurrentPage(page);
		}
		
		//Grab the first page of starters requested by the user
		results = DaoUtils.executeLoadFromPostId(this,"ThreadDao.StartersByCreateDate", "ThreadDao.StartersCount",rootId,buildFilterMask(getFilterFlags()));
		List<Post> posts = loadAllPostsForThreads(results.getList());

		loadReplies(results, subPage, posts);
		
		return results;
	}
	
	private final static String HQL_ThreadIdsByDate = "SELECT post.postId FROM Post post " +
			"WHERE post.parent.postId=:parentId AND bitwise_and( post.metaMask, :filterMask ) = 0 " +
			"ORDER BY post.threadReplyDate DESC";
	
	private final static String HQL_SUB_THREAD_BY_PATH = "SELECT post.postId FROM Post post " +
			"WHERE post.thread.postId=:postId AND bitwise_and( post.metaMask, :filterMask ) = 0 " +
			"ORDER BY post.path DESC";
	
	private final static String HQL_ThreadIdsByCreateDate = "SELECT post.postId FROM Post post " +
		"WHERE post.parent.postId=:parentId AND bitwise_and( post.metaMask, :filterMask ) = 0 " +
		"ORDER BY post.date DESC";

	
	
	public PaginatedList<Post> loadByReplyDate() {
		PaginatedList<Post> resultsConversations;
		//Grab the first page of starters requested by the user
		int page = 1;
		int subPage = 1;
		if(getCurrentPage() == -1){
			if(!sourcePost.isRootPost() && getCurrentPage() == -1){
				//Source is not a root, find the thread page
				page = determineThreadPage(HQL_ThreadIdsByDate);
				subPage = determineSubPageInPath();
			}
			setCurrentPage(page);
		}
		
		resultsConversations = DaoUtils.executeLoadFromPostId(this,"ThreadDao.StartersByReplyDate","ThreadDao.StartersCount",rootId, buildFilterMask(getFilterFlags()));
		List<Post> replies = loadAllPostsForThreads(resultsConversations.getList());
		
		loadReplies(resultsConversations, subPage, replies);
		return resultsConversations;
	}

	private void loadReplies(PaginatedList<Post> resultsConversations, int subPage,
			List<Post> posts) {
		for(Post p : resultsConversations.getList()){
			if(p.equals(sourcePost.getThread()) && subPage != 1)
				loadRepliesFromPostList(p, posts, subPage);
			else
				loadRepliesFromPostList(p, posts);
		}
	}

	private int determineSubPageInPath() {
		int subPage = 1;
		if(!sourcePost.isThreadPost()){
			//Reply post
			//10F4325D-8BE0-4346-B126-F257D5A308B5
			subPage = determinePageNumber(HQL_SUB_THREAD_BY_PATH, 
										sourcePost.getThread().getPostId(),
										sourcePost.getPostId(),
										THREAD_REPLY_MAX_RESULTS);
		}
		return subPage;
	}

	private int determineThreadPage(String query) {
		int page = 1;
		threadId = sourcePost.getThread().getPostId();
		@SuppressWarnings("unchecked")
		List<String> list = session().createQuery(query)
			.setParameter("parentId", sourcePost.getRoot().getPostId())
			.setParameter("filterMask", buildFilterMask(getFilterFlags()))
			.list();
		
		int ndx = list.indexOf(threadId);
		if(ndx >= 0){
			page = (ndx / getPageSize())+1;
		}
		return page;
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
		List<Post> flatReplyHierarchy = flattenThread(thread, posts);	
		if(flatReplyHierarchy.size() > THREAD_REPLY_MAX_RESULTS){
			thread.setPosts(flatReplyHierarchy.subList(flatReplyHierarchy.size()-THREAD_REPLY_MAX_RESULTS, flatReplyHierarchy.size()));
		}
		else{
			thread.setPosts(flatReplyHierarchy);
		}
		PathSegmentizer.segmentizeChildPaths(thread);
	}

	private void loadRepliesFromPostList(Post thread, List<Post> posts, int subPage) {
		List<Post> flatReplyHierarchy = flattenThread(thread, posts);	
		if(flatReplyHierarchy.size() > THREAD_REPLY_MAX_RESULTS){
			int start = flatReplyHierarchy.size()-(THREAD_REPLY_MAX_RESULTS * subPage);
			start = start <= 0 ? 0 : start; //I think that what was happening is that if the post was on the first subpage and the results were not divisible by 5 that you'd get a negative start index. This clamps it at zero
			int end = (start + THREAD_REPLY_MAX_RESULTS) <= flatReplyHierarchy.size() ? (start + THREAD_REPLY_MAX_RESULTS) : flatReplyHierarchy.size();
			thread.setReplyStartIndex(start);
			thread.setReplyPage(subPage);
			thread.setPosts(flatReplyHierarchy.subList(start, end));
		}
		else{
			thread.setReplyStartIndex(1);
			thread.setPosts(flatReplyHierarchy);
		}
		PathSegmentizer.segmentizeChildPaths(thread);
	}
	
	
//	private List<Post> flattenThread(Post thread, List<Post> posts) {
//		List<Post> flatReplyHierarchy = new ArrayList<Post>();
//		for(Post p : posts){
//			if(!p.isThreadPost() && p.getThread().getPostId().equals(thread.getPostId()))
//				flatReplyHierarchy.add(p);
//		}
//		
//		int [] pathSegmentMaximums = PathSegmentizer.calculatePathSegmentMaximums(flatReplyHierarchy);
//		thread.setPathSegmentMaximums(pathSegmentMaximums);
//		return flatReplyHierarchy;
//	}
	
	//Some of this logic is duplicated in LatestPostsDao... take a look into consolidating it
	private List<Post> flattenThread(Post thread, List<Post> posts) {
		Post prevPost = null;
		List<Post> flatReplyHierarchy = new ArrayList<Post>();
		for(Post p : posts){
			if(!p.isThreadPost() && p.getThread().getPostId().equals(thread.getPostId())){
				flatReplyHierarchy.add(p);
				if(prevPost != null){
					if(p.getPath().length() < prevPost.getPath().length()){
						prevPost.setEndOfBranch(true);
					}
				}
				prevPost = p;
			}
		}	
		if(prevPost != null){
			prevPost.setEndOfBranch(true);
		}
		
		thread.setPosts(flatReplyHierarchy);
		
//		if(flatReplyHierarchy.size() > THREAD_REPLY_MAX_RESULTS){
//			thread.setPosts(flatReplyHierarchy.subList(flatReplyHierarchy.size()-THREAD_REPLY_MAX_RESULTS, flatReplyHierarchy.size()));
//		}
//		else
//			thread.setPosts(flatReplyHierarchy);
		
		
		int [] pathSegmentMaximums = PathSegmentizer.calculatePathSegmentMaximums(flatReplyHierarchy);
		thread.setPathSegmentMaximums(pathSegmentMaximums);
		
		PathSegmentizer.segmentizeChildPaths(thread);
		
		return flatReplyHierarchy;
		
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
	 * 
	 * NEW (8/16/2010) If the requested page is set to -1 i just get everything
	 * 
	 */
	public PaginatedList<Post> loadThreadSummmary(){
		if(getCurrentPage() > -1){
			setPageSize(THREAD_REPLY_MAX_RESULTS);
			PaginatedList<Post> results;
			results = DaoUtils.executeLoadFromPostId(this, "ThreadDao.Thread", "ThreadDao.ThreadCount", threadId, buildFilterMask(getFilterFlags()));
			
			//Only called to populate tree drawing hints
			flattenThread(results.getList().get(0).getThread(),results.getList());
			
			Collections.reverse(results.getList());
			return results;
		}
		else{
			setPageSize(100);//Silly right? (ok, 10k was crazy, now it's smaller... a choice needs to happen here)
			setCurrentPage(1);
			PaginatedList<Post> results;
			results = DaoUtils.executeLoadFromPostId(this, "ThreadDao.Thread", "ThreadDao.ThreadCount", threadId, buildFilterMask(getFilterFlags()));
			
			//Only called to populate tree drawing hints
			flattenThread(results.getList().get(0).getThread(),results.getList());
			
			Collections.reverse(results.getList());
			return results;
		}
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
